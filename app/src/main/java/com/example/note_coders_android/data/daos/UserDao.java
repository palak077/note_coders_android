package com.example.note_coders_android.data.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import com.example.note_coders_android.data.entities.User;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<User> getAll();

    //You can also pass multiple parameters or reference the same parameter multiple times in a query
    @Query("SELECT * FROM user where email LIKE  :email AND password LIKE :password")
    User findByName(String email, String password);


    @Query("SELECT * FROM user where email LIKE  :email")
    User findByEmail(String email);

    @Query("SELECT COUNT(*) from user")
    int countUsers();

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}