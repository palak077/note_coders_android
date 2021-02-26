package com.example.note_coders_android.data.entities;

import android.location.Location;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;

@Entity
public class Note {

    @PrimaryKey(autoGenerate = true)
    int id;
    private String categoryTitle;
    private String title;
    private String description;
    private Date dateCreated;
    private Location location;
    private ArrayList<String> optionalImagePath;
    private String optionalAudioPath;

    public Note(String title, String description, Date dateCreated, Location location, String categoryTitle, ArrayList<String> optionalImagePath, String optionalAudioPath) {
        this.title = title;
        this.description = description;
        this.dateCreated = dateCreated;
        this.location = location;
        this.categoryTitle = categoryTitle;
        this.optionalImagePath = optionalImagePath;
        this.optionalAudioPath = optionalAudioPath;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public ArrayList<String> getOptionalImagePath() {
        return optionalImagePath;
    }

    public void setOptionalImagePath(ArrayList<String> optionalImagePath) {
        this.optionalImagePath = optionalImagePath;
    }

    public String getOptionalAudioPath() {
        return optionalAudioPath;
    }

    public void setOptionalAudioPath(String optionalAudioPath) {
        this.optionalAudioPath = optionalAudioPath;
    }
}