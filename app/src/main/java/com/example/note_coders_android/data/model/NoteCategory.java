package com.example.note_coders_android.data.model;

import java.util.ArrayList;
import java.util.List;

import com.example.note_coders_android.data.entities.Note;

public class NoteCategory {

    private String title;
    private ArrayList<Note> notes;

    public NoteCategory(String title, ArrayList<Note> notes) {
        this.title = title;
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

}
