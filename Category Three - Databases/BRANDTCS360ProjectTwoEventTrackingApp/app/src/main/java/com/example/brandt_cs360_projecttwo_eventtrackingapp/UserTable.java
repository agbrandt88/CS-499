package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// Reference: https://commonsware.com/AndroidArch/pages/chap-roommn-001.html

// Room user entity, stores user account information while enforcing unique username inputs.
@Entity(tableName = "users", indices = {
        @Index(value = {"username"}, unique = true)
})
public class UserTable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "username")
    public String username;

    // Maintain salt/hash password capability vice String password.
    @NonNull
    @ColumnInfo(name = "password_salt")
    public String passwordSalt;

    @NonNull
    @ColumnInfo(name = "password_hash")
    public String passwordHash;

}
