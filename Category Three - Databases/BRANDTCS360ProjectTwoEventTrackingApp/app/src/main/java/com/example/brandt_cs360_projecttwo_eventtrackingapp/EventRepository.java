package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.room.Room;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// Manages asynchronous access to Room & user account password hashing.
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

    // Runs database lookup in background, communicates result with main thread.
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

    // Reference: https://www.baeldung.com/java-password-hashing
    // Reference: https://medium.com/@rohteemie/understanding-password-hashing-and-salt-e52738ddb3dc
    // Reference: https://medium.com/@mbanaee61/password-encryption-hashing-and-salting-in-java-eaa2d46b06e5

    // Generates salt and hash before new user storage.
    public void insertUsernameAsync(UserTable user, String password, RepositoryCallback<Long> callback) {
        databaseExecutorService.execute(() -> {
            try {
                SecureRandom random = new SecureRandom();

                byte[] salt = new byte[16];

                random.nextBytes(salt);

                KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);

                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

                byte[] hash = factory.generateSecret(spec).getEncoded();

                // Use encode to string for salt/hash storage.
                user.passwordSalt = Base64.getEncoder().encodeToString(salt);
                user.passwordHash = Base64.getEncoder().encodeToString(hash);

                long userId = userDao.insertUsername(user);
                activityExecutor.execute(() -> callback.onSuccess(userId));

            } catch (Exception exception) {
                activityExecutor.execute(() -> callback.onError(exception));
            }
        });
    }

    // Uses stored salt to rebuild hash verifying login access.
    public void passwordVerificationAsync(UserTable user, String password, RepositoryCallback<Boolean> callback) {
        databaseExecutorService.execute(() -> {
            try {
                byte[] salt = Base64.getDecoder().decode(user.passwordSalt);
                byte[] databaseHash = Base64.getDecoder().decode(user.passwordHash);

                KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);

                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                byte[] inputHash = factory.generateSecret(spec).getEncoded();

                // Checks for match between stored hash and new hash.
                boolean isMatch = MessageDigest.isEqual(databaseHash, inputHash);

                activityExecutor.execute(() -> callback.onSuccess(isMatch));

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
