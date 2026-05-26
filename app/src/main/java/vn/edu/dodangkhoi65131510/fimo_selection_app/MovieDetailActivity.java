package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
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
    private ImageView imgPlayerBackground;
    private ImageView btnPlayVideo;
    private Dialog fullscreenDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        fullscreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        imgPlayerBackground = findViewById(R.id.imgPlayerBackground);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);
        playerView = findViewById(R.id.playerView);
        ImageView imgDetailPoster = findViewById(R.id.imgDetailPoster);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDesc = findViewById(R.id.tvDetailDesc);
        ImageView btnBack = findViewById(R.id.btnBackDetail);
        Button btnTrailer = findViewById(R.id.btnTrailer);

        rvCast = findViewById(R.id.rvCast);
        castAdapter = new CastAdapter(castList);
        if (rvCast != null) {
            rvCast.setAdapter(castAdapter);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        String movieId = getIntent().getStringExtra("MOVIE_ID");
        String title = getIntent().getStringExtra("MOVIE_TITLE");
        String desc = getIntent().getStringExtra("MOVIE_DESC");
        String poster = getIntent().getStringExtra("MOVIE_POSTER");

        if (title != null) tvTitle.setText(title);
        if (desc != null && !desc.contains("youtube.com")) tvDesc.setText(desc);

        if (poster != null) {
            if (imgPlayerBackground != null) Glide.with(this).load(poster).centerCrop().into(imgPlayerBackground);
            if (imgDetailPoster != null) Glide.with(this).load(poster).centerCrop().into(imgDetailPoster);
        }

        if (movieId != null) {
            fetchTrailerKey(movieId);
            fetchMovieCast(movieId);
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

        if (btnPlayVideo != null) {
            btnPlayVideo.setOnClickListener(v -> {
                imgPlayerBackground.setVisibility(View.GONE);
                btnPlayVideo.setVisibility(View.GONE);
                playerView.setVisibility(View.VISIBLE);
                Toast.makeText(MovieDetailActivity.this, "Đang lấy dữ liệu video...", Toast.LENGTH_SHORT).show();

                if (movieId != null) {
                    final boolean[] isLoaded = {false};

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Movies").child(movieId);
                    dbRef.child("videoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (isLoaded[0]) return;
                            isLoaded[0] = true;

                            String videoUrl = null;
                            if (snapshot.exists()) {
                                videoUrl = snapshot.getValue(String.class);
                            }

                            if (videoUrl == null || videoUrl.isEmpty()) {
                                videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8";
                            }
                            initializePlayer(videoUrl);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            if (isLoaded[0]) return;
                            isLoaded[0] = true;
                            initializePlayer("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8");
                        }
                    });

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (!isLoaded[0]) {
                            isLoaded[0] = true;
                            Toast.makeText(MovieDetailActivity.this, "Mạng Firebase yếu, tự động chiếu link dự phòng!", Toast.LENGTH_SHORT).show();
                            initializePlayer("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8");
                        }
                    }, 3000);

                } else {
                    initializePlayer("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8");
                }
            });
        }

        fullscreenDialog.setOnDismissListener(dialog -> {
            if (playerView.getParent() != null) {
                ((ViewGroup) playerView.getParent()).removeView(playerView);
            }
            FrameLayout container = findViewById(R.id.playerContainer);
            container.addView(playerView);

            // Xoay màn hình về lại dọc khi tắt Fullscreen
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        });
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

                        // Ẩn thanh trạng thái và thanh điều hướng để mở rộng 100% diện tích phim
                        if (fullscreenDialog.getWindow() != null) {
                            fullscreenDialog.getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        }

                        fullscreenDialog.show();
                        // Ép màn hình xoay ngang
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