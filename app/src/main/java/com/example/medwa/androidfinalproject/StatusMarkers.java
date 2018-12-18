package com.example.medwa.androidfinalproject;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

// Status Markers Class
public class StatusMarkers implements ClusterItem {
    // Member Variables for Status Markers Class
    private LatLng position;
    private String title;
    private String snippet;
    private int iconImage;

    // Constructor for Status Markers
    public StatusMarkers(LatLng position, String title, String snippet, int iconImage) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconImage = iconImage;
    }

    // Default Constructor for Status Markers
    public StatusMarkers() {

    }
    // Getters and Setters ofr Status Markers Class

    // LatLng Getter
    @Override
    public LatLng getPosition() {
        return position;
    }

    // LatLng Setter
    public void setPosition(LatLng position) {
        this.position = position;
    }

    // Title Getter
    @Override
    public String getTitle() {
        return title;
    }

    // Title Setter
    public void setTitle(String title) {
        this.title = title;
    }

    // Snippet Getter
    @Override
    public String getSnippet() {
        return snippet;
    }

    // Snippet Setter
    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    // IconImage Getter
    public int getIconImage() {
        return iconImage;
    }

    // IconImage Setter
    public void setIconImage(int iconImage) {
        this.iconImage = iconImage;
    }
}
