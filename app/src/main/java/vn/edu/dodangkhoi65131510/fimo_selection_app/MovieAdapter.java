package vn.edu.dodangkhoi65131510.fimo_selection_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;
    private String mediaType = "movie";

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    public MovieAdapter(Context context, List<Movie> movieList, String mediaType) {
        this.context = context;
        this.movieList = movieList;
        this.mediaType = mediaType;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        if (holder.imgPoster != null) {
            Glide.with(context).load(movie.getAnhBiaUrl()).centerCrop().into(holder.imgPoster);
        }

        if (holder.tvTitle != null) {
            holder.tvTitle.setText(movie.getTieuDe());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("MOVIE_ID", String.valueOf(movie.getId()));
            intent.putExtra("MOVIE_TITLE", movie.getTieuDe());
            intent.putExtra("MOVIE_DESC", movie.getMoTa());
            intent.putExtra("MOVIE_POSTER", movie.getAnhBiaUrl());
            intent.putExtra("MEDIA_TYPE", mediaType);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvTitle;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgMoviePoster);
            tvTitle = itemView.findViewById(R.id.tvMovieTitle);
        }
    }
}