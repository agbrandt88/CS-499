package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkManager;

import java.util.List;

// Communicates between HomeActivity, event repository, & event scheduler for loading & deleting events,
// in addition to event organization.
public class LoadEventViewModel extends AndroidViewModel {

    private EventRepository eventRepository;
    private EventScheduler eventScheduler = new EventScheduler();
    private MutableLiveData<List<EventTable>> eventData = new MutableLiveData<>();
    private MutableLiveData<String> errorData = new MutableLiveData<>();

    public LoadEventViewModel(Application application) {
        super(application);

        eventRepository = EventRepository.getInstance(application);
    }

    public LiveData<List<EventTable>> getEventData() {
        return eventData;
    }

    public LiveData<String> getErrorData() {
        return errorData;
    }

    // Ensures events are ordered when pulled vice relying solely on database.
    public void loadEventData(int userId) {

        eventRepository.getEventsAsync(userId, new EventRepository.RepositoryCallback<List<EventTable>>() {

            @Override
            public void onSuccess(List<EventTable> eventList) {

                eventScheduler.setEventList(eventList);

                eventData.setValue(eventScheduler.getEventList());
            }

            @Override
            public void onError(Exception exception) {

                errorData.setValue("Error loading events.");

            }
        });
    }

    // Uses HashMap to find events by ID.
    public void deleteEventData(int eventId, int userId) {

        EventTable event = eventScheduler.getEvent(eventId);

        if (event == null) {

            errorData.setValue("No event found");
            return;
        }

        // Cancels scheduled notification & deletes event from list, updates UI.
        eventRepository.deleteEventAsync(event, new EventRepository.RepositoryCallback<Void>() {

            @Override
            public void onSuccess(Void result) {

                WorkManager.getInstance(getApplication()).cancelUniqueWork("event_reminder_" + event.id);
                eventScheduler.removeEvent(event.id);
                eventData.setValue(eventScheduler.getEventList());
            }

            @Override
            public void onError(Exception exception) {

                errorData.setValue("Error deleting event.");

            }
        });
    }
}
