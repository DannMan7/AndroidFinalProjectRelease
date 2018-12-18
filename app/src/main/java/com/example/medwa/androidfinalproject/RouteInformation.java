package com.example.medwa.androidfinalproject;
import java.util.Calendar;
import java.util.Date;

// Route Information Class
public class RouteInformation {

    // Declaring Member Variables of RouteInformation
    private static String bus;
    private static double latitude;
    private static double longitude;
    private static String snippet;
    private static long avatar;
    private static Date timeStamp = Calendar.getInstance().getTime();

    // Default Constructor for Route Information
    public RouteInformation() {

    }
    // Getters and Setters for RouteInformation Class

    // Getter for Snippet
    public  String getSnippet() { return snippet; }
    // Setter for Snippet
    public  void setSnippet(String snippet) { RouteInformation.snippet = snippet; }
    // Getter for Avatar
    public  long getAvatar() { return avatar; }
    // Setter for Avatar
    public  void setAvatar(long avatar) { RouteInformation.avatar = avatar; }
    // Getter for Bus
    public String getBus() {
        return bus;
    }
    // Setter for Bus
    public void setBus(String bus) {
        this.bus = bus;
    }
    // Getter for Latitude
    public double getLatitude() {
        return latitude;
    }
    // Setter for Latitude
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    // Getter for Longitude
    public double getLongitude() {
        return longitude;
    }
    // Setter for Longitude
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    // Getter for TimeStamp
    public Date getTimeStamp() {
        return timeStamp;
    }
    // Setter for TimeStamp
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

