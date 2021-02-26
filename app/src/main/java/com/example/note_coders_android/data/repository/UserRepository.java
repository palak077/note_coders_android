package com.example.note_coders_android.data.repository;

import android.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import note.coders.android.data.daos.UserDao;
import note.coders.android.data.db.NoteDatabase;
import note.coders.android.data.entities.User;

public class UserRepository {

    private UserDao userDao;
    private Executor executor;

    public UserRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        userDao = database.userDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public User login(String email, String password) {
       return userDao.findByName(email, password);
    }

    public void signUp(User user) {
        executor.execute(() -> userDao.insertAll(user));
    }

    public void delete(User user) {
        executor.execute(() -> userDao.delete(user));
    }

}
