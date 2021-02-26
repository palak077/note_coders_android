package com.example.note_coders_android.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.example.note_coders_android.data.entities.Category;

@Dao
public interface CategoryDao {

    @Insert
    void insert(Category data);

    @Insert
    void insertAll(Category... categories);

    @Delete
    void delete(Category data);

    @Update
    void update(Category data);

    @Query("SELECT * FROM category")
    LiveData<List<Category>> getAllCategories();

}
