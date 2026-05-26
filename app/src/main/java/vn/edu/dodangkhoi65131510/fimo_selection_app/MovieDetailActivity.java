package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ImageView imgPlayerBackground = findViewById(R.id.imgPlayerBackground);
        ImageView imgDetailPoster = findViewById(R.id.imgDetailPoster);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDesc = findViewById(R.id.tvDetailDesc);
        ImageView btnBack = findViewById(R.id.btnBackDetail);

        Button btnTrailer = findViewById(R.id.btnTrailer);
        ImageView btnPlayVideo = findViewById(R.id.btnPlayVideo);

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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"), "video/mp4");
                startActivity(intent);
            });
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