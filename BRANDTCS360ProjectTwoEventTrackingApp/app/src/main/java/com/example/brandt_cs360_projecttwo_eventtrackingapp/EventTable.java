package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Event entity using Room
@Entity(tableName = "events")
public class EventTable {

    // Stores id, name, date, time, notes, and notification status for each event
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "event_name")
    public String eventName;

    @ColumnInfo(name = "event_date")
    public String eventDate;

    @ColumnInfo(name = "event_time")
    public String eventTime;

    @ColumnInfo(name = "event_notes")
    public String eventNotes;

    @ColumnInfo(name = "event_notification")
    public boolean eventNotification;

}
