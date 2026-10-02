package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Room database operations for user events.
@Dao
public interface EventDao {

    // Loads events tied to user ID.
    @Query("SELECT * FROM events WHERE user_id = :userId")
    List<EventTable> getEvents(int userId);

    // Loads one event via event ID correlating to user ID.
    @Query("SELECT * FROM events WHERE id = :id AND user_id = :userId")
    EventTable getEventId(int id, int userId);

    @Insert
    long insertEvent(EventTable event);

    @Update
    void updateEvent(EventTable event);

    @Delete
    void deleteEvent(EventTable event);

}
