package com.example.note_coders_android.ui.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import com.example.note_coders_android.data.entities.Category;
import com.example.note_coders_android.data.entities.Note;
import com.example.note_coders_android.data.repository.CategoryRepository;
import com.example.note_coders_android.data.repository.NoteRepository;

public class NoteViewModel extends AndroidViewModel {
    /// ViewModel for observing data and realtime update to UI

    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;
    private MutableLiveData<Note> selectedNote = new MutableLiveData<>();

    private CategoryRepository categoryRepository;
    private LiveData<List<Category>> allCategories;


    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        categoryRepository = new CategoryRepository(application);
        allNotes = repository.getAllNotes();
        allCategories = categoryRepository.getAllCategories();
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        Log.d("AYAN", "Note list " + allNotes.getValue().size());
        repository.delete(note);
        Log.d("AYAN", "Note list " + allNotes.getValue().size());
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }


    public void setSelectedNote(Note updated) {
        selectedNote.setValue(updated);
    }

    public LiveData<Note> getSelectedNote() {
        return selectedNote;
    }


    public void insertCategory(Category category) {
        categoryRepository.insert(category);
    }

    public void updateCategory(Category category) {
        categoryRepository.update(category);
    }

    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

}
