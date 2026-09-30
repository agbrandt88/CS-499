package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// Room database, connects user/event entities to DAOs
@Database(entities = {UserTable.class, EventTable.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract EventDao eventDao();

}
