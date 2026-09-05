package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Allows CRUD capability for events
@Dao
public interface EventDao {

    // SQL query that displays cards in order of date & time.
    @Query("SELECT * FROM events WHERE user_id = :userId ORDER BY event_date, event_time")
    List<EventTable> getEvents(int userId);

    // SQL query that pulls specific card
    @Query("SELECT * FROM events WHERE id = :id AND user_id = :userId")
    EventTable getEventId(int id, int userId);

    // Enables SQL event creation
    @Insert
    long insertEvent(EventTable event);

    // Enables SQL event update
    @Update
    void updateEvent(EventTable event);

    // Enables SQL event deletion
    @Delete
    void deleteEvent(EventTable event);

}
