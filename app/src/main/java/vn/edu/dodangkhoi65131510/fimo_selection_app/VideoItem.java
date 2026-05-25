package vn.edu.dodangkhoi65131510.fimo_selection_app;

import com.google.gson.annotations.SerializedName;

public class VideoItem {
    @SerializedName("key")
    private String key;

    @SerializedName("type")
    private String type;

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }
}