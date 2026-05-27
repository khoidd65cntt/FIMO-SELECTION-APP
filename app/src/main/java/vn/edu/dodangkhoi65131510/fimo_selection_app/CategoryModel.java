package vn.edu.dodangkhoi65131510.fimo_selection_app;

import java.util.List;

public class CategoryModel {
    private String title;
    private List<Movie> movieList;
    private String mediaType;

    public CategoryModel(String title, List<Movie> movieList, String mediaType) {
        this.title = title;
        this.movieList = movieList;
        this.mediaType = mediaType;
    }

    public String getTitle() { return title; }
    public List<Movie> getMovieList() { return movieList; }
    public void setMovieList(List<Movie> movieList) { this.movieList = movieList; }
    public String getMediaType() { return mediaType; }
}