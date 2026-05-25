package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

    private String youtubeTrailerKey = null;

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
}