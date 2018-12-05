package com.example.medwa.androidfinalproject;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class StatusMarkers implements ClusterItem {
    private LatLng position;
    private String title;
    private String snippet;
    private int iconImage;

    public StatusMarkers(LatLng position, String title, String snippet, int iconImage) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconImage = iconImage;
    }

    public StatusMarkers() {

    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getIconImage() {
        return iconImage;
    }

    public void setIconImage(int iconImage) {
        this.iconImage = iconImage;
    }
}
