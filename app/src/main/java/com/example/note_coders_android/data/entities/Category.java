package com.example.note_coders_android.data.entities;

import android.os.Parcel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Category {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;

    public Category(String title) {
        this.title = title;
    }

    protected Category(Parcel in) {
        id = in.readInt();
        title = in.readString();
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

    @Override
    public String toString() {
        return title;
    }

    public static Category[] populateData()
    {
        //array of categories- default ones
        return new Category[]
                {
                new Category("All notes"),
                new Category("Uncategorized")
        };
    }
}
