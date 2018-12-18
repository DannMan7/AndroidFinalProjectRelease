package com.example.medwa.androidfinalproject;

// ImageUpload Class
public class ImageUpload {
    // Class Member Variables
    private String name;
    private String url;

    // Constructor for ImageUpload
    public ImageUpload(String name, String url) {
        this.name = name;
        this.url = url;
    }
    // Default Constructor for ImageUpload
    public ImageUpload(){

    }

    // Getter for Name
    public String getName() {
        return name;
    }

    // Getter for Uri
    public String getUrl() {
        return url;
    }
}