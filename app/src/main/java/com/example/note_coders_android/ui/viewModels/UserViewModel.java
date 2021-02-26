package com.example.note_coders_android.ui.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.note_coders_android.data.entities.User;
import com.example.note_coders_android.data.repository.UserRepository;

public class UserViewModel extends AndroidViewModel
{
    /// ViewModel for observing data and realtime update to UI

    private UserRepository repository;

    public UserViewModel(@NonNull Application application)
    {
        super(application);
        repository = new UserRepository(application);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public User login(String email, String password) {
        return repository.login(email, password);
    }

    public void signUp(User user) {
        repository.signUp(user);
    }

    public void delete(User user) {
        repository.delete(user);
    }

}
