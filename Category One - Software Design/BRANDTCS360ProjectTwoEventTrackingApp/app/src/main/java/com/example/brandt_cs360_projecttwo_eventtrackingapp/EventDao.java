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

    // Uses event date & time to load/sort events based on one user ID.
    @Query("SELECT * FROM events WHERE user_id = :userId ORDER BY event_date, event_time")
    List<EventTable> getEvents(int userId);

    // SQL query, load one event correlating to one user
    @Query("SELECT * FROM events WHERE id = :id AND user_id = :userId")
    EventTable getEventId(int id, int userId);

    @Insert
    long insertEvent(EventTable event);

    @Update
    void updateEvent(EventTable event);

    @Delete
    void deleteEvent(EventTable event);

}
