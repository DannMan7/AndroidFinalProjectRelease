package com.example.medwa.androidfinalproject;

public class ImageUpload {private String name;
    private String url;

    public ImageUpload(String name, String url) {
        this.name = name;
        this.url = url;
    }
    public ImageUpload(){

    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}