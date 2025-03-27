package com.example.gmh_app.Activities;

public class ScreenItems {
    private int screenImg;
    private String title;
    private String description;

    public ScreenItems(int screenImg, String title, String description) {
        this.screenImg = screenImg;
        this.title = title;
        this.description = description;
    }

    public int getScreenImg() {
        return screenImg;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}