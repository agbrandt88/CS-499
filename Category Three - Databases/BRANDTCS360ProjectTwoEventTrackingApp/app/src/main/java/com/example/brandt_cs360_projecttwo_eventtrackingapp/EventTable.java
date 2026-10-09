package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// Reference: https://commonsware.com/AndroidArch/pages/chap-roommn-001.html
// Reference: https://guides.codepath.org/android/Room-Guide

// Room event entity, stores event information & uses foreign key to enforce connection to user.
// Foreign key helps prevent orphaned events via cascade if user deleted.
@Entity(tableName = "events", foreignKeys = {
        @ForeignKey(entity = UserTable.class, parentColumns = "id", childColumns = "user_id", onDelete = ForeignKey.CASCADE)
}, indices = {
        @Index(value = {"user_id"})
})

public class EventTable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "event_name")
    public String eventName;

    // Maintain start & end times using epoch milliseconds vice strings for storage.
    @ColumnInfo(name = "event_start")
    public long eventStart;

    @ColumnInfo(name = "event_end")
    public long eventEnd;

    @ColumnInfo(name = "event_notes")
    public String eventNotes;

    @ColumnInfo(name = "event_notification")
    public boolean eventNotification;

}
