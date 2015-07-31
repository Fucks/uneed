package com.project.uneed.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Announces {

    private String title;
    private String userName;
    private Bitmap announcesImage;
    private String announcessDescription;

    public Announces(String title, String userName, Bitmap announcesImage, String announcessDescription) {
        this.title = title;
        this.userName = userName;
        this.announcesImage = announcesImage;
        this.announcessDescription = announcessDescription;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Bitmap getAnnouncesImage() {
        return announcesImage;
    }

    public void setAnnouncesImage(Bitmap announcesImage) {
        this.announcesImage = announcesImage;
    }

    public String getAnnouncessDescription() {
        return announcessDescription;
    }

    public void setAnnouncessDescription(String announcessDescription) {
        this.announcessDescription = announcessDescription;
    }
}
