package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

// ViewModel for login & account creation logic. Uses LiveData to push results to MainActivity.
public class AccountViewModel extends AndroidViewModel {

    private EventRepository eventRepository;
    private MutableLiveData<UserTable> userAccessData = new MutableLiveData<>();
    private MutableLiveData<String> errorData = new MutableLiveData<>();

    public AccountViewModel(Application application) {
        super(application);

        eventRepository = EventRepository.getInstance(application);
    }

    public LiveData<UserTable> getUserAccessData() {
        return userAccessData;
    }

    public LiveData<String> getErrorData() {
        return errorData;
    }

    public void userLogin(String username, String password) {

        if (username.isEmpty()) {
            errorData.setValue("Enter username");
            return;
        }

        if (password.isEmpty()) {
            errorData.setValue("Enter password");
            return;
        }

        // Async repository communication, enables database functionality without hindering UI
        eventRepository.getUsernameAsync(username, new EventRepository.RepositoryCallback<UserTable>() {

            @Override
            public void onSuccess(UserTable user) {
                if (user == null) {
                    errorData.setValue("Invalid username.");
                    return;
                }

                if (!user.password.equals(password)) {
                    errorData.setValue("Invalid password.");
                    return;
                }

                userAccessData.setValue(user);
            }

            @Override
            public void onError(Exception exception) {
                errorData.setValue("Login error.");

            }
        });
    }

    public void userCreateAccount(String username, String password, String reenterPassword) {

        if (username.isEmpty()) {
            errorData.setValue("Enter username");
            return;
        }

        if (password.isEmpty()) {
            errorData.setValue("Enter password");
            return;
        }

        if (!password.equals(reenterPassword)) {
            errorData.setValue("Passwords must match");
            return;
        }

        // Async repository communication, enables database functionality without hindering UI
        eventRepository.getUsernameAsync(username, new EventRepository.RepositoryCallback<UserTable>() {

            @Override
            public void onSuccess(UserTable user) {
                if (user != null) {
                    errorData.setValue("Username already exists.");
                    return;
                }

                UserTable addUser = new UserTable();
                addUser.username = username;
                addUser.password = password;
                eventRepository.insertUsernameAsync(addUser, new EventRepository.RepositoryCallback<Long>() {

                    @Override
                    public void onSuccess(Long userId) {
                        addUser.id = userId.intValue();
                        userAccessData.setValue(addUser);
                    }

                    @Override
                    public void onError(Exception exception) {
                        errorData.setValue("Account creation error.");
                    }
                });
            }

            @Override
            public void onError(Exception exception) {
                errorData.setValue("Account creation error.");
            }
        });
    }
}
