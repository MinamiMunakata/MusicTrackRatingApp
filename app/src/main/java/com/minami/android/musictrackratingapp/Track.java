package com.minami.android.musictrackratingapp;

/**
 * Created by Minami on 2018/07/11.
 */

public class Track {
//    private int index;
//    private String track_id;
    private String title;
    private String rate;

    public Track() {
    }

    public Track(String title, String rate) {
//        this.index = index;
        this.title = title;
        this.rate = rate;
    }

//    public Track(int index, String track_id, String title, String rate) {
//        this.index = index;
//        this.track_id = track_id;
//        this.title = title;
//        this.rate = rate;
//    }

//    public int getIndex() {
//        return index;
//    }
//
//    public void setIndex(int index) {
//        this.index = index;
//    }

//    public String getTrack_id() {
//        return track_id;
//    }
//
//    public void setTrack_id(String track_id) {
//        this.track_id = track_id;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
