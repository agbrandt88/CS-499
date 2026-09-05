package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.content.Context;

import androidx.room.Room;

import java.util.List;

// Communicates with DAOs to support app/database capability
public class EventRepository {

    // Repository & DAO variables
    private static EventRepository eventRepository;
    private final UserDao userDao;
    private final EventDao eventDao;

    // Checks for current repository or creates new one
    public static EventRepository getInstance(Context context) {
        if (eventRepository == null) {
            eventRepository = new EventRepository(context);
        }
        return eventRepository;
    }

    // Generates database
    private EventRepository(Context context) {
        AppDatabase database = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "event_database").allowMainThreadQueries().build();

        userDao = database.userDao();
        eventDao = database.eventDao();

    }

    // Pulls username data based on input
    public UserTable getUsername(String username) {
        return userDao.getUsername(username);
    }

    // Adds new user information upon account creation
    public long insertUsername(UserTable user) {
        return userDao.insertUsername(user);
    }

    // Pulls event data for display
    public List<EventTable> getEvents(int userId) {
        return eventDao.getEvents(userId);
    }

    // Pulls specific event
    public EventTable getEventId(int id, int userId) {
        return eventDao.getEventId(id, userId);
    }

    // Adds event
    public long insertEvent(EventTable event) {
        return eventDao.insertEvent(event);
    }

    // Updates event
    public void updateEvent(EventTable event) {
        eventDao.updateEvent(event);
    }

    // Delete event
    public void deleteEvent(EventTable event) {
        eventDao.deleteEvent(event);
    }

}
