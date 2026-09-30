package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkManager;

import java.util.List;

// Communicates between HomeActivity and event repository for loading & deleting events.
public class LoadEventViewModel extends AndroidViewModel {

    private EventRepository eventRepository;
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

    public void loadEventData(int userId) {

        eventRepository.getEventsAsync(userId, new EventRepository.RepositoryCallback<List<EventTable>>() {

            @Override
            public void onSuccess(List<EventTable> eventList) {

                eventData.setValue(eventList);
            }

            @Override
            public void onError(Exception exception) {

                errorData.setValue("Error loading events.");

            }
        });
    }

    public void deleteEventData(EventTable event, int userId) {
        eventRepository.deleteEventAsync(event, new EventRepository.RepositoryCallback<Void>() {

            @Override
            public void onSuccess(Void result) {

                // Cancels scheduled notification when corresponding event deleted prior to reload.
                WorkManager.getInstance(getApplication()).cancelUniqueWork("event_reminder_" + event.id);
                loadEventData(userId);
            }

            @Override
            public void onError(Exception exception) {

                errorData.setValue("Error deleting event.");

            }
        });
    }
}
