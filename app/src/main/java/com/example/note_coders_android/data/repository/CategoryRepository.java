package com.example.note_coders_android.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.note_coders_android.data.daos.CategoryDao;
import com.example.note_coders_android.data.db.NoteDatabase;
import com.example.note_coders_android.data.entities.Category;

public class CategoryRepository {

    private CategoryDao categoryDao;
    private LiveData<List<Category>> allCategories;
    private Executor executor;

    public CategoryRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        categoryDao = database.categoryDao();
        allCategories = categoryDao.getAllCategories();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insert(Category category) {
        executor.execute(() -> categoryDao.insert(category));
    }

    public void update(Category category) {
        executor.execute(() -> categoryDao.update(category));
    }

    public void delete(Category category) {
        executor.execute(() -> categoryDao.delete(category));
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }
}
