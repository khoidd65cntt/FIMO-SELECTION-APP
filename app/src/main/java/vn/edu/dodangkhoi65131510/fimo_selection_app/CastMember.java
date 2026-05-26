package vn.edu.dodangkhoi65131510.fimo_selection_app;

import com.google.gson.annotations.SerializedName;

public class CastMember {
    private String name;

    @SerializedName("profile_path")
    private String profilePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
}