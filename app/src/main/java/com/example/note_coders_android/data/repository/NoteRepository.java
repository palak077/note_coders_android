//connects all the parts with one another and creates the instance of the database

package com.example.note_coders_android.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.note_coders_android.data.daos.NoteDao;
import com.example.note_coders_android.data.db.NoteDatabase;
import com.example.note_coders_android.data.entities.Note;

public class NoteRepository
{
    //member variables
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    private Executor executor;

    //for context we pass application
    public NoteRepository(Application application)
    {

        NoteDatabase database = NoteDatabase.getInstance(application);
        //usually we can not call abstract methods since they do not have body but as we constructed our database instance  with builder
        //room auto generates its necessary code
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insert(Note note) {
        executor.execute(() -> noteDao.insert(note));
    }

    public void update(Note note) {
        executor.execute(() -> noteDao.update(note));
    }

    public void delete(Note note) {
        executor.execute(() -> noteDao.delete(note));
    }

    //this method will occur in the background itself but for other methods we will use ASync Task
    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

}
