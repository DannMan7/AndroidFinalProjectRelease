package com.example.medwa.androidfinalproject;

import android.location.Location;

import java.util.Calendar;
import java.util.Date;

public class RouteInformation {

    private static String bus;
    private static double latitude;
    private static double longitude;
    private static String snippet;
    private static long avatar;
    private static Date timeStamp = Calendar.getInstance().getTime();

    public RouteInformation() {

    }

    public  String getSnippet() { return snippet; }

    public  void setSnippet(String snippet) { RouteInformation.snippet = snippet; }

    public  long getAvatar() { return avatar; }

    public  void setAvatar(long avatar) { RouteInformation.avatar = avatar; }

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

