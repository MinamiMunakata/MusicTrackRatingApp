package com.minami.android.musictrackratingapp;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minami on 2018/07/11.
 */

public class Artist {
    private String id;
    private String name;
    private String genre;
    private ArrayList<Track> tracks;

    public Artist() {
    }


    public Artist(String id, String name, String genre) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.tracks = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

