package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

// Room database operations for finding & creating user accounts.
@Dao
public interface UserDao {

    // Locate user account via username
    @Query("SELECT * FROM users WHERE username = :username")
    UserTable getUsername(String username);

    @Insert
    long insertUsername(UserTable user);

}
