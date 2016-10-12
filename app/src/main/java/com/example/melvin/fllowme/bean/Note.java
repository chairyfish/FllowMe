package com.example.melvin.fllowme.bean;

/**
 * Created by Melvin on 2016/9/4.
 */
public class Note {
    private double longitude, latitude;
    private String cotnent, path1, path2, path3, date;

    public Note(double longitude, double latitude, String cotnent, String path1, String path2, String path3, String date) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.cotnent = cotnent;
        this.path1 = path1;
        this.path2 = path2;
        this.path3 = path3;
        this.date = date;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getCotnent() {
        return cotnent;
    }

    public String getPath2() {
        return path2;
    }

    public String getPath1() {
        return path1;
    }

    public String getPath3() {
        return path3;
    }

    public String getDate() {
        return date;
    }
}
