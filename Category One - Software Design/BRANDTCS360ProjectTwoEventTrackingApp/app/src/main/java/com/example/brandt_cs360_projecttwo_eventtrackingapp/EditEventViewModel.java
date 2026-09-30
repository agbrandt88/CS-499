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

// Reference: https://developer.android.com/topic/libraries/architecture/livedata
// Reference: https://medium.com/@appdevinsights/differences-between-livedata-and-mutablelivedata-in-android-0b5f300f5491
// Reference: https://www.geeksforgeeks.org/android/viewmodel-in-android-architecture-components/
// Reference: https://developer.android.com/reference/java/time/format/DateTimeFormatter

// ViewModel that communicates with AddEventActivity for loading & saving events. Manages event
// validation.
public class EditEventViewModel extends AndroidViewModel {

    private EventRepository eventRepository;
    private EventTable currentEvent;
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

    public void saveEvent(int eventId, int userId, String name, String date, String time, String notes, boolean notification) {

        if (userId < 0) {
            errorData.setValue("User ID error.");
            return;
        }

        EventValidator.Validation validation = EventValidator.validation(name, date, time, notes);

        if (!validation.isValid()) {
            errorData.setValue(validation.getResponse());
            return;
        }

        String trimName = name.trim();
        String trimDate = date.trim();
        String trimTime = time.trim();

        // Date & time validation, enforces scheduling of future events only.
        if (notification) {
            try {
                LocalDate eventDate = LocalDate.parse(trimDate, DateTimeFormatter.ofPattern("M/d/yyyy"));
                LocalTime eventTime = LocalTime.parse(trimTime, DateTimeFormatter.ofPattern("H:m"));
                LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);

                if (!eventDateTime.isAfter(LocalDateTime.now())) {
                    errorData.setValue("Event date has already passed.");
                    return;
                }
            } catch (DateTimeParseException exception) {
                errorData.setValue("Invalid event.");
                return;
            }
        }

        if (eventId == -1) {

            EventTable addEvent = new EventTable();

            addEvent.userId = userId;
            addEvent.eventName = trimName;
            addEvent.eventDate = trimDate;
            addEvent.eventTime = trimTime;
            addEvent.eventNotes = notes;
            addEvent.eventNotification = notification;

            eventRepository.insertEventAsync(
                    addEvent, new EventRepository.RepositoryCallback<Long>() {

                        @Override
                        public void onSuccess(Long addEventId) {

                            if (addEventId != null) {
                                addEvent.id = addEventId.intValue();
                            }

                            currentEvent = addEvent;

                            // Updating eventData to new database ID.
                            eventData.setValue(addEvent);
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
        currentEvent.eventNotes = notes;
        currentEvent.eventNotification = notification;

        eventRepository.updateEventAsync(
                currentEvent, new EventRepository.RepositoryCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {

                        saveData.setValue(true);
                    }

                    @Override
                    public void onError(Exception exception) {

                        errorData.setValue("Error updating event.");

                    }
                }
        );

    }

}
