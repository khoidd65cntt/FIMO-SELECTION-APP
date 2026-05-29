package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

    private String youtubeTrailerKey = null;
    private RecyclerView rvCast;
    private CastAdapter castAdapter;
    private List<CastMember> castList = new ArrayList<>();
    private ExoPlayer exoPlayer;
    private StyledPlayerView playerView;
    private WebView webViewFullMovie;
    private ImageView imgPlayerBackground;
    private ImageView btnPlayVideo;
    private Dialog fullscreenDialog;

    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;
    private WebChromeClient webChromeClient;

    private static final String DB_URL = "https://fimo-selection-app-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        fullscreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        imgPlayerBackground = findViewById(R.id.imgPlayerBackground);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);
        playerView = findViewById(R.id.playerView);
        webViewFullMovie = findViewById(R.id.webViewFullMovie);
        ImageView imgDetailPoster = findViewById(R.id.imgDetailPoster);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDesc = findViewById(R.id.tvDetailDesc);
        ImageView btnBack = findViewById(R.id.btnBackDetail);
        Button btnTrailer = findViewById(R.id.btnTrailer);
        Button btnFavorite = findViewById(R.id.btnFavorite);

        rvCast = findViewById(R.id.rvCast);
        castAdapter = new CastAdapter(castList);
        if (rvCast != null) {
            rvCast.setAdapter(castAdapter);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mCustomView != null) {
                    if (webChromeClient != null) {
                        webChromeClient.onHideCustomView();
                    }
                } else {
                    finish();
                }
            }
        });

        String movieId = getIntent().getStringExtra("MOVIE_ID");
        String title = getIntent().getStringExtra("MOVIE_TITLE");
        String desc = getIntent().getStringExtra("MOVIE_DESC");
        String poster = getIntent().getStringExtra("MOVIE_POSTER");
        String tempMediaType = getIntent().getStringExtra("MEDIA_TYPE");
        final String mediaType = (tempMediaType != null) ? tempMediaType : "movie";

        if (title != null) tvTitle.setText(title);
        if (desc != null && !desc.contains("youtube.com")) tvDesc.setText(desc);

        if (poster != null) {
            if (imgPlayerBackground != null) Glide.with(this).load(poster).centerCrop().into(imgPlayerBackground);
            if (imgDetailPoster != null) Glide.with(this).load(poster).centerCrop().into(imgDetailPoster);
        }

        if (btnTrailer != null) {
            btnTrailer.setOnClickListener(v -> {
                if (youtubeTrailerKey != null) {
                    showTrailerDialog(youtubeTrailerKey);
                } else {
                    Toast.makeText(this, "Đang tải Trailer, vui lòng thử lại sau vài giây!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && movieId != null && btnFavorite != null) {
            DatabaseReference favRef = FirebaseDatabase.getInstance(DB_URL)
                    .getReference("Users").child(user.getUid()).child("Favorites").child(movieId);

            favRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        btnFavorite.setText("Đã yêu thích");
                        btnFavorite.setTextColor(Color.parseColor("#E50914"));
                    } else {
                        btnFavorite.setText("Yêu thích");
                        btnFavorite.setTextColor(Color.WHITE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            btnFavorite.setOnClickListener(v -> {
                favRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            favRef.removeValue();
                            Toast.makeText(MovieDetailActivity.this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                        } else {
                            HashMap<String, String> favData = new HashMap<>();
                            favData.put("id", movieId);
                            favData.put("title", title);
                            favData.put("desc", desc);
                            favData.put("poster", poster);
                            favData.put("mediaType", mediaType);
                            favRef.setValue(favData);
                            Toast.makeText(MovieDetailActivity.this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });
        } else if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> Toast.makeText(MovieDetailActivity.this, "Vui lòng đăng nhập để sử dụng chức năng này", Toast.LENGTH_SHORT).show());
        }

        webChromeClient = new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                return true;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }

                mOriginalOrientation = getRequestedOrientation();
                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                mOriginalSystemUiVisibility = decor.getSystemUiVisibility();

                mCustomView = view;
                decor.addView(mCustomView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                decor.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                mCustomViewCallback = callback;
            }

            @Override
            public void onHideCustomView() {
                if (mCustomView == null) {
                    return;
                }

                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                decor.removeView(mCustomView);
                mCustomView = null;

                decor.setSystemUiVisibility(mOriginalSystemUiVisibility);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                if (mCustomViewCallback != null) {
                    mCustomViewCallback.onCustomViewHidden();
                    mCustomViewCallback = null;
                }
            }
        };

        if (movieId != null) {
            fetchTrailerKey(movieId);
            fetchMovieCast(movieId);

            if (btnPlayVideo != null) {
                btnPlayVideo.setOnClickListener(v -> {
                    imgPlayerBackground.setVisibility(View.GONE);
                    btnPlayVideo.setVisibility(View.GONE);

                    if (movieId != null) {
                        playerView.setVisibility(View.GONE);
                        webViewFullMovie.setVisibility(View.VISIBLE);

                        WebSettings webSettings = webViewFullMovie.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        webSettings.setDomStorageEnabled(true);
                        webSettings.setMediaPlaybackRequiresUserGesture(false);
                        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
                        webSettings.setSupportMultipleWindows(true);

                        webViewFullMovie.setWebChromeClient(webChromeClient);

                        webViewFullMovie.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                String url = request.getUrl().toString();
                                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                    return true;
                                }
                                if (url.contains("shopee") || url.contains("lazada") || url.contains("bet")) {
                                    return true;
                                }
                                return false;
                            }
                        });

                        String vidsrcUrl;
                        if ("tv".equalsIgnoreCase(mediaType)) {
                            vidsrcUrl = "https://vidsrc-embed.ru/embed/tv/" + movieId + "/1/1";
                        } else {
                            vidsrcUrl = "https://vidsrc-embed.ru/embed/movie/" + movieId;
                        }
                        webViewFullMovie.loadUrl(vidsrcUrl);

                        Toast.makeText(MovieDetailActivity.this, "Đang tải nguồn phim...", Toast.LENGTH_SHORT).show();

                        if (user != null) {
                            DatabaseReference historyRef = FirebaseDatabase.getInstance(DB_URL)
                                    .getReference("Users")
                                    .child(user.getUid())
                                    .child("History")
                                    .child(movieId);

                            HashMap<String, Object> historyData = new HashMap<>();
                            historyData.put("id", movieId);
                            historyData.put("title", title != null ? title : "Phim không tên");
                            historyData.put("desc", desc != null ? desc : "");
                            historyData.put("poster", poster != null ? poster : "");
                            historyData.put("mediaType", mediaType);
                            historyData.put("timestamp", System.currentTimeMillis());

                            historyRef.setValue(historyData);
                        }

                    } else {
                        Toast.makeText(MovieDetailActivity.this, "Không tìm thấy ID phim!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else {
            Toast.makeText(MovieDetailActivity.this, "Không tìm thấy ID phim!", Toast.LENGTH_SHORT).show();
        }

        fullscreenDialog.setOnDismissListener(dialog -> {
            if (playerView.getParent() != null) {
                ((ViewGroup) playerView.getParent()).removeView(playerView);
            }
            FrameLayout container = findViewById(R.id.playerContainer);
            container.addView(playerView);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeHelper.applyTheme(this);
    }

    private void initializePlayer(String videoUrl) {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(exoPlayer);

            String cleanUrl = videoUrl.trim();
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(cleanUrl));

            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();

            ImageView btnVolume = playerView.findViewById(R.id.btnCustomVolume);
            SeekBar seekVolume = playerView.findViewById(R.id.seekVolume);
            ImageView btnFullscreen = playerView.findViewById(R.id.btnCustomFullscreen);

            if (seekVolume != null && btnVolume != null) {
                seekVolume.setProgress((int) (exoPlayer.getVolume() * 100));

                seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            exoPlayer.setVolume(progress / 100f);
                            if (progress == 0) {
                                btnVolume.setAlpha(0.5f);
                            } else {
                                btnVolume.setAlpha(1.0f);
                            }
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            seekVolume.setVisibility(View.GONE);
                        }, 2000);
                    }
                });

                btnVolume.setOnClickListener(view -> {
                    if (seekVolume.getVisibility() == View.GONE) {
                        seekVolume.setVisibility(View.VISIBLE);
                    } else {
                        seekVolume.setVisibility(View.GONE);
                    }
                });
            }

            if (btnFullscreen != null) {
                btnFullscreen.setOnClickListener(view -> {
                    if (!fullscreenDialog.isShowing()) {
                        if (playerView.getParent() != null) {
                            ((ViewGroup) playerView.getParent()).removeView(playerView);
                        }

                        fullscreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                        if (fullscreenDialog.getWindow() != null) {
                            fullscreenDialog.getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        }

                        fullscreenDialog.show();
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    } else {
                        fullscreenDialog.dismiss();
                    }
                });
            }

            exoPlayer.addListener(new com.google.android.exoplayer2.Player.Listener() {
                @Override
                public void onPlayerError(@NonNull com.google.android.exoplayer2.PlaybackException error) {
                    Toast.makeText(MovieDetailActivity.this, "Lỗi máy ảo không phát được: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();
        }
    }

    private void fetchTrailerKey(String movieId) {
        TmdbApi apiService = ApiClient.getClient().create(TmdbApi.class);
        apiService.getMovieVideos(movieId, "3509f85d40f81d254b5afc2d8beaa8e1").enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (VideoItem item : response.body().getResults()) {
                        if (item.getType() != null && item.getType().equalsIgnoreCase("Trailer")) {
                            youtubeTrailerKey = item.getKey();
                            break;
                        }
                    }
                    if (youtubeTrailerKey == null && !response.body().getResults().isEmpty()) {
                        youtubeTrailerKey = response.body().getResults().get(0).getKey();
                    }
                }
            }
            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {}
        });
    }

    private void fetchMovieCast(String movieId) {
        TmdbApi apiService = ApiClient.getClient().create(TmdbApi.class);
        apiService.getMovieCredits(movieId, "3509f85d40f81d254b5afc2d8beaa8e1").enqueue(new Callback<CreditsResponse>() {
            @Override
            public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCast() != null) {
                    castList.clear();
                    if (response.body().getCast().size() > 10) {
                        castList.addAll(response.body().getCast().subList(0, 10));
                    } else {
                        castList.addAll(response.body().getCast());
                    }
                    castAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<CreditsResponse> call, Throwable t) {}
        });
    }

    private void showTrailerDialog(String videoId) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_dialog_trailer);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        YouTubePlayerView youTubePlayerView = dialog.findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }

            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);
                Toast.makeText(MovieDetailActivity.this, "Video bị chặn nhúng bản quyền. Đang mở YouTube...", Toast.LENGTH_LONG).show();
                dialog.dismiss();

                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });

        dialog.setOnDismissListener(d -> youTubePlayerView.release());
        dialog.show();
    }

    private class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {
        private final List<CastMember> list;

        public CastAdapter(List<CastMember> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cast, parent, false);
            return new CastViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
            CastMember member = list.get(position);
            holder.tvName.setText(member.getName());

            // Giải cứu chữ trắng trên nền trắng của diễn viên
            SharedPreferences prefs = holder.itemView.getContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
            boolean isNightMode = prefs.getBoolean("isNightMode", true);
            int textColor = isNightMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000");
            ThemeHelper.changeColorsSafe(holder.itemView, textColor, isNightMode);

            String imageUrl = "https://image.tmdb.org/t/p/w185" + member.getProfilePath();
            if (member.getProfilePath() != null && !member.getProfilePath().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .centerCrop()
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.imgAvatar);
            } else {
                holder.imgAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class CastViewHolder extends RecyclerView.ViewHolder {
            ImageView imgAvatar;
            TextView tvName;

            public CastViewHolder(@NonNull View itemView) {
                super(itemView);
                imgAvatar = itemView.findViewById(R.id.imgCastAvatar);
                tvName = itemView.findViewById(R.id.tvCastName);
            }
        }
    }
}