package vn.edu.dodangkhoi65131510.fimo_selection_app;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Movie implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("release_date")
    private String releaseDate;

    private String trailerKey;
    private String fullMovieUrl;

    public Movie(String id, String title, String overview, String posterPath, double voteAverage) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
    }

    public Movie(String id, String title, String overview, String posterPath, String backdropPath, double voteAverage, String releaseDate) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTieuDe() {
        return title;
    }

    public void setTieuDe(String title) {
        this.title = title;
    }

    public String getMoTa() {
        return overview;
    }

    public void setMoTa(String overview) {
        this.overview = overview;
    }

    public String getAnhBiaUrl() {
        if (posterPath != null && !posterPath.contains("android.resource") && !posterPath.contains("http")) {
            return "https://image.tmdb.org/t/p/w500" + posterPath;
        }
        return posterPath;
    }

    public void setAnhBiaUrl(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        if (backdropPath != null && !backdropPath.contains("http")) {
            return "https://image.tmdb.org/t/p/w780" + backdropPath;
        }
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public String getFullMovieUrl() {
        return fullMovieUrl;
    }

    public void setFullMovieUrl(String fullMovieUrl) {
        this.fullMovieUrl = fullMovieUrl;
    }
}