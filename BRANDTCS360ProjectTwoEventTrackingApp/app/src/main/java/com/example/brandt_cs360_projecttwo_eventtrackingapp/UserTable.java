package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// User entity using Room
@Entity(tableName = "users")
public class UserTable {

    // Stores id, username, password for each user
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "username")
    public String username;

    @NonNull
    @ColumnInfo(name = "password")
    public String password;

}
