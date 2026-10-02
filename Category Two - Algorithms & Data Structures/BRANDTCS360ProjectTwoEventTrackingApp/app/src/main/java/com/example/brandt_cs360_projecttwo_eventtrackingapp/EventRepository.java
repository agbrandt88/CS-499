package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Manages database functions and access to Room
public class EventRepository {

    private static EventRepository eventRepository;
    private final UserDao userDao;
    private final EventDao eventDao;

    private final ExecutorService databaseExecutorService;
    private final Executor activityExecutor;

    // reference: https://developer.android.com/develop/background-work/background-tasks/asynchronous/java-threads

    // Supports asynchronous database communication to main thread ViewModels.
    public interface RepositoryCallback<T> {

        void onSuccess(T result);
        void onError(Exception exception);
    }

    // Ensures app uses one event repository instance.
    public static EventRepository getInstance(Context context) {
        if (eventRepository == null) {
            eventRepository = new EventRepository(context);
        }
        return eventRepository;
    }

    // Generates Room database & creates background executors.
    private EventRepository(Context context) {

        Context applicationContext = context.getApplicationContext();

        AppDatabase database = Room.databaseBuilder(applicationContext,
                AppDatabase.class, "event_database").build();

        userDao = database.userDao();
        eventDao = database.eventDao();

        databaseExecutorService = Executors.newSingleThreadExecutor();
        activityExecutor = ContextCompat.getMainExecutor(applicationContext);

    }

    // Database operates in background via executors, communicates with main thread via callback.
    public void getUsernameAsync(String username, RepositoryCallback<UserTable> callback) {
        databaseExecutorService.execute(() -> {
            try {
                UserTable user = userDao.getUsername(username);
                activityExecutor.execute(() -> callback.onSuccess(user));

            } catch (Exception exception) {
                activityExecutor.execute(() -> callback.onError(exception));
            }
        });
    }

    public void insertUsernameAsync(UserTable user, RepositoryCallback<Long> callback) {
        databaseExecutorService.execute(() -> {
            try {
                long userId = userDao.insertUsername(user);
                activityExecutor.execute(() -> callback.onSuccess(userId));

            } catch (Exception exception) {
                activityExecutor.execute(() -> callback.onError(exception));
            }
        });
    }

    public void getEventsAsync(int userId, RepositoryCallback<List<EventTable>> callback) {
        databaseExecutorService.execute(() -> {
            try {
                List<EventTable> events = eventDao.getEvents(userId);
                activityExecutor.execute(() -> callback.onSuccess(events));

            } catch (Exception exception) {
                activityExecutor.execute(() -> callback.onError(exception));
            }
        });
    }

    public void getEventIdAsync(int id, int userId, RepositoryCallback<EventTable> callback) {
        databaseExecutorService.execute(() -> {
            try {
                EventTable event = eventDao.getEventId(id, userId);
                activityExecutor.execute(() -> callback.onSuccess(event));

            } catch (Exception exception) {
                activityExecutor.execute(() -> callback.onError(exception));
            }
        });
    }

    public void insertEventAsync(EventTable event, RepositoryCallback<Long> callback) {
        databaseExecutorService.execute(() -> {
            try {
                long eventId = eventDao.insertEvent(event);
                activityExecutor.execute(() -> callback.onSuccess(eventId));

            } catch (Exception exception) {
                activityExecutor.execute(() -> callback.onError(exception));
            }
        });
    }

    public void updateEventAsync(EventTable event, RepositoryCallback<Void> callback) {
        databaseExecutorService.execute(() -> {
            try {
                eventDao.updateEvent(event);
                activityExecutor.execute(() -> callback.onSuccess(null));

            } catch (Exception exception) {
                activityExecutor.execute(() -> callback.onError(exception));
            }
        });
    }

    public void deleteEventAsync(EventTable event, RepositoryCallback<Void> callback) {
        databaseExecutorService.execute(() -> {
            try {
                eventDao.deleteEvent(event);
                activityExecutor.execute(() -> callback.onSuccess(null));

            } catch (Exception exception) {
                activityExecutor.execute(() -> callback.onError(exception));
            }
        });
    }

}
