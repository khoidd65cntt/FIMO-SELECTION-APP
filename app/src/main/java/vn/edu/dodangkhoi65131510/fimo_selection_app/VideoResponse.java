package vn.edu.dodangkhoi65131510.fimo_selection_app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VideoResponse {
    @SerializedName("results")
    private List<VideoItem> results;

    public List<VideoItem> getResults() {
        return results;
    }
}