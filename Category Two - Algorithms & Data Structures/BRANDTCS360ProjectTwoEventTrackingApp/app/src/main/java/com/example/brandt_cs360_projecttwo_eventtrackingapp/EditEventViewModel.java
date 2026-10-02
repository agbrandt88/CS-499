package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

// Reference: https://developer.android.com/topic/libraries/architecture/livedata
// Reference: https://medium.com/@appdevinsights/differences-between-livedata-and-mutablelivedata-in-android-0b5f300f5491
// Reference: https://www.geeksforgeeks.org/android/viewmodel-in-android-architecture-components/
// Reference: https://developer.android.com/reference/java/time/format/DateTimeFormatter

// ViewModel that communicates with AddEventActivity for loading & saving events. Manages event
// validation and checks for event overlap.
public class EditEventViewModel extends AndroidViewModel {

    private EventRepository eventRepository;
    private EventTable currentEvent;
    private EventScheduler eventScheduler = new EventScheduler();
    private MutableLiveData<EventTable> eventData = new MutableLiveData<>();
    private MutableLiveData<String> errorData = new MutableLiveData<>();
    private MutableLiveData<Boolean> saveData = new MutableLiveData<>();

    public EditEventViewModel(Application application) {
        super(application);

        eventRepository = EventRepository.getInstance(application);
    }

    public LiveData<EventTable> getEvent() {
        return eventData;
    }

    public LiveData<String> getError() {
        return errorData;
    }

    public LiveData<Boolean> getSaveData() {
        return saveData;
    }

    public void loadEvent(int eventId, int userId) {

        if (eventId == -1) {
            return;
        }

        if (userId < 0) {
            errorData.setValue("User ID error.");
            return;
        }

        eventRepository.getEventIdAsync(eventId, userId, new EventRepository.RepositoryCallback<EventTable>() {

            @Override
            public void onSuccess(EventTable event) {

                if (event == null) {
                    errorData.setValue("Event missing.");
                    return;
                }

                currentEvent = event;
                eventData.setValue(event);
            }

            @Override
            public void onError(Exception exception) {

                errorData.setValue("Event loading error.");
            }
        });
    }

    public void saveEvent(
            int eventId,
            int userId,
            String name,
            String date,
            String time,
            String endTime,
            String notes,
            boolean notification) {

        if (userId < 0) {
            errorData.setValue("User ID error.");
            return;
        }

        EventValidator.Validation validation = EventValidator.validation(name, date, time, endTime, notes);

        if (!validation.isValid()) {
            errorData.setValue(validation.getResponse());
            return;
        }

        String trimName = name.trim();
        String trimDate = date.trim();
        String trimTime = time.trim();
        String trimEndTime = endTime.trim();

        // Time validation, enforces scheduling of start and end times. Ensures end is post start time.
        try {

            LocalTime eventStartTime = LocalTime.parse(trimTime, DateTimeFormatter.ofPattern("H:m"));
            LocalTime eventEndTime = LocalTime.parse(trimEndTime, DateTimeFormatter.ofPattern("H:m"));

            if (!eventEndTime.isAfter(eventStartTime)) {

                errorData.setValue("Invalid end time entry.");
                return;
            }

            // Enforces notification events starting after current date/time.
            if (notification) {

                LocalDate eventDate = LocalDate.parse(trimDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
                LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventStartTime);

                if (!eventDateTime.isAfter(LocalDateTime.now())) {
                    errorData.setValue("Event date has already passed.");
                    return;
                }

            }

        } catch (DateTimeParseException exception) {
            errorData.setValue("Invalid event.");
            return;
        }

        EventTable pendingEvent = new EventTable();

        // Ensures that event ID doesn't change when event is being updated & prevents new events
        // from being saved with -1 as ID.
        if (eventId != -1) {
            pendingEvent.id = eventId;
        }

        pendingEvent.userId = userId;
        pendingEvent.eventName = trimName;
        pendingEvent.eventDate = trimDate;
        pendingEvent.eventTime = trimTime;
        pendingEvent.eventEndTime = trimEndTime;
        pendingEvent.eventNotes = notes;
        pendingEvent.eventNotification = notification;

        // Pulls event structure before overlap check.
        eventRepository.getEventsAsync(userId, new EventRepository.RepositoryCallback<List<EventTable>>() {
            @Override
            public void onSuccess(List<EventTable> eventList) {

                eventScheduler.setEventList(eventList);

                // Enforces overlap algorithm so overlapping events not saved.
                if (eventScheduler.overlaps(pendingEvent)) {
                    errorData.setValue("Event overlap error.");
                    return;
                }

                if (eventId == -1) {

                    // Sets up save order by database, followed by data structures via addEvent(). Ensures
                    // event ID is the same across app.
                    eventRepository.insertEventAsync(
                            pendingEvent, new EventRepository.RepositoryCallback<Long>() {

                                @Override
                                public void onSuccess(Long addEventId) {

                                    if (addEventId != null) {
                                        pendingEvent.id = addEventId.intValue();
                                    }

                                    eventScheduler.addEvent(pendingEvent);

                                    currentEvent = pendingEvent;

                                    // Updating eventData to new database ID.
                                    eventData.setValue(pendingEvent);
                                    saveData.setValue(true);
                                }

                                @Override
                                public void onError(Exception exception) {

                                    errorData.setValue("Error saving event.");

                                }
                            }
                    );

                    return;

                }

                currentEvent.eventName = trimName;
                currentEvent.eventDate = trimDate;
                currentEvent.eventTime = trimTime;
                currentEvent.eventEndTime = trimEndTime;
                currentEvent.eventNotes = notes;
                currentEvent.eventNotification = notification;

                // Removes & adds updated event again to ensure order reflects date/time changes.
                eventRepository.updateEventAsync(
                        currentEvent, new EventRepository.RepositoryCallback<Void>() {

                            @Override
                            public void onSuccess(Void result) {

                                eventScheduler.removeEvent(currentEvent.id);
                                eventScheduler.addEvent(currentEvent);
                                saveData.setValue(true);
                            }

                            @Override
                            public void onError(Exception exception) {

                                errorData.setValue("Error updating event.");

                            }
                        }
                );

            }

            @Override
            public void onError(Exception exception) {

                errorData.setValue("Error scheduling event.");

            }
        });

    }

}
