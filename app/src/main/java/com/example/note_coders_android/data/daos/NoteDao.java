package com.example.note_coders_android.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.note_coders_android.data.entities.Note;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note data);

    @Delete
    void delete(Note data);

//    @Query("Delete from note_db")
//    void deleteAllNotes();

    @Update
    void update(Note data);

    //@Query because we are going to define the database operation our-self
    // no condition like select column from table where id = because we wanna select all the entries
    //similar query like the open helper class then what is the difference- editor tells if we have typo
    //get all the notes and put in recycler view
    //also at compile time editor checks whether the columns of the table fits into the Note object given in the list below or not
    @Query("SELECT * FROM note")
    LiveData<List<Note>> getAllNotes();
    //purpose of adding the live data is any changes in the table will be directly mapped to Note
    // object in the list and activity will be notified

}