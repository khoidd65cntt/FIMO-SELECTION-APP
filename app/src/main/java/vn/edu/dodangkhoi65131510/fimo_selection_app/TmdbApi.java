package vn.edu.dodangkhoi65131510.fimo_selection_app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApi {
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("discover/movie")
    Call<MovieResponse> getPopularAnime(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("with_genres") String genres,
            @Query("with_original_language") String originalLanguage,
            @Query("sort_by") String sortBy,
            @Query("page") int page
    );

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("region") String region,
            @Query("page") int page
    );

    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("query") String query,
            @Query("page") int page
    );

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideos(
            @Path("movie_id") String movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/credits")
    Call<CreditsResponse> getMovieCredits(
            @Path("movie_id") String movieId,
            @Query("api_key") String apiKey
    );

    @GET("discover/movie")
    Call<MovieResponse> getMoviesByFilter(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("with_genres") String genreId,
            @Query("with_origin_country") String countryCode,
            @Query("primary_release_year") String year,
            @Query("page") int page
    );

    @GET("movie/{movie_id}/recommendations")
    Call<MovieDetailActivity.RecommendResponse> getMovieRecommendations(
            @Path("movie_id") String movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("tv/{tv_id}/recommendations")
    Call<MovieDetailActivity.RecommendResponse> getTvRecommendations(
            @Path("tv_id") String tvId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("movie/{movie_id}/similar")
    Call<MovieDetailActivity.RecommendResponse> getSimilarMovies(
            @Path("movie_id") String movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("tv/{tv_id}/similar")
    Call<MovieDetailActivity.RecommendResponse> getTvSimilar(
            @Path("tv_id") String tvId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    // LỆNH MỚI: LẤY PHIM ĐANG CHIẾU / THỊNH HÀNH ĐỂ APP LÚC NÀO CŨNG GỢI Ý PHIM MỚI
    @GET("movie/now_playing")
    Call<MovieDetailActivity.RecommendResponse> getFreshMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("tv/on_the_air")
    Call<MovieDetailActivity.RecommendResponse> getFreshTvShows(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );
}