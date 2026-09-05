package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

// Allows access to Room user data
@Dao
public interface UserDao {

    // Enables SQL search for matching username
    @Query("SELECT * FROM users WHERE username = :username")
    UserTable getUsername(String username);

    // Enables SQL insert for new username
    @Insert
    long insertUsername(UserTable user);

}
