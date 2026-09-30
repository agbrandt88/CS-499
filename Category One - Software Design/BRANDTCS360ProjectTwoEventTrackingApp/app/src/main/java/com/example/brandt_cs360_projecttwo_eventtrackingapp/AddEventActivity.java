package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

// Manages event card UI, date/time pickers, setting up notifications.
public class AddEventActivity extends AppCompatActivity {

    // References: https://developer.android.com/training/permissions/requesting

    private EditText editFabName;
    private EditText editFabDate;
    private EditText editFabTime;
    private EditText editFabNotes;
    private Button buttonFabSave;
    private Button buttonFabCancel;
    private CheckBox checkBoxNotification;

    private EditEventViewModel editEventViewModel;

    // Event ID variable for pulling up event/correlated WorkManager reminder
    private int eId = -1;

    // User ID connecting event to user during create, update
    private int userId;

    private boolean notificationSet = false;

    // Request SMS permission before scheduling notification
    private final ActivityResultLauncher<String> smsNotificationLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {

                // Schedule notifications if permission granted, otherwise disables SMS but saves event.
                @Override
                public void onActivityResult(Boolean userPermission) {
                    if (Boolean.TRUE.equals(userPermission)) {

                        scheduleNotification();

                    } else {
                        Toast.makeText(AddEventActivity.this, "Notification disabled",
                                Toast.LENGTH_SHORT).show();
                    }

                    finish();
                }
            });

    // Reference: https://medium.com/@anandgaur2207/livedata-and-viewmodel-31a5fe812447
    // Reference: https://developer.android.com/topic/libraries/architecture/livedata

    // Reference: https://www.geeksforgeeks.org/android/how-to-popup-datepicker-while-clicking-on-edittext-in-android/
    // Reference: https://www.geeksforgeeks.org/android/datepickerdialog-in-android/
    // Reference: https://developer.android.com/reference/java/time/format/DateTimeFormatter
    // Reference: https://medium.com/@AlexanderObregon/javas-duration-between-method-explained-a15e2cc54c8b

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_events);

        bindViews();

        editFabDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Date picker dialog employed for format enforcement.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        editFabDate.setText((monthOfYear +1) + "/" + dayOfMonth + "/" + year);

                    }
                },
                year, month, day);
                datePickerDialog.show();
            }
        });

        editFabTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Time picker dialog employed for format enforcement.
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        editFabTime.setText(hourOfDay + ":" + minute);

                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        eId = getIntent().getIntExtra("pickEvent", -1);

        userId = getIntent().getIntExtra("userId", -1);

        if (userId < 0) {
            Toast.makeText(this, "User ID error.", Toast.LENGTH_SHORT).show();

            finish();
            return;
        }

        editEventViewModel = new ViewModelProvider(this).get(EditEventViewModel.class);

        observeEventViewModel();



        // ViewModel loads existing event before enabling Save
        if (eId != -1) {

            buttonFabSave.setEnabled(false);

            editEventViewModel.loadEvent(eId, userId);
        }

        buttonFabSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });


        buttonFabCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void bindViews() {
        editFabName = findViewById(R.id.editFabName);
        editFabDate = findViewById(R.id.editFabDate);
        editFabTime = findViewById(R.id.editFabTime);
        editFabNotes = findViewById(R.id.editFabNotes);
        buttonFabSave = findViewById(R.id.buttonFabSave);
        buttonFabCancel = findViewById(R.id.buttonFabCancel);
        checkBoxNotification = findViewById(R.id.checkBoxNotification);
    }

    // Changes to event data & saves observed for UI - ViewModel communication.
    private void observeEventViewModel() {
        editEventViewModel.getEvent().observe(this, event -> {
            if (event == null) {
                return;
            }

            eId = event.id;

            showEvent(event);

            buttonFabSave.setEnabled(true);
        });

        editEventViewModel.getError().observe(this, message -> {

            if (message == null) {
                return;
            }

            Toast.makeText(AddEventActivity.this, message, Toast.LENGTH_SHORT).show();

            buttonFabSave.setEnabled(true);

        });

        editEventViewModel.getSaveData().observe(this, save -> {

            if (!Boolean.TRUE.equals(save)) {
                return;
            }

            notificationSaved();
        });
    }

    private void showEvent(EventTable event) {
        editFabName.setText(event.eventName);
        editFabDate.setText(event.eventDate);
        editFabTime.setText(event.eventTime);
        editFabNotes.setText(event.eventNotes);
        checkBoxNotification.setChecked(event.eventNotification);
    }

    private void saveEvent() {
        String name = editFabName.getText().toString();
        String date = editFabDate.getText().toString();
        String time = editFabTime.getText().toString();
        String notes = editFabNotes.getText().toString();
        boolean notification = checkBoxNotification.isChecked();

        notificationSet = notification;

        buttonFabSave.setEnabled(false);

        editEventViewModel.saveEvent(eId, userId, name, date, time, notes, notification);
    }

    // Schedule/cancel event notification after user Saves
    private void notificationSaved() {

        if (!notificationSet) {

            WorkManager.getInstance(this).cancelUniqueWork("event_reminder_" + eId);
            finish();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

            scheduleNotification();
            finish();

        } else {

            smsNotificationLauncher.launch(Manifest.permission.SEND_SMS);
        }
    }

    // Calculates time between desired event time and actual time, sets up WorkManager Notification
    private void scheduleNotification() {

        LocalDate eventDate = LocalDate.parse(editFabDate.getText().toString(), DateTimeFormatter.ofPattern("M/d/yyyy"));
        LocalTime eventTime = LocalTime.parse(editFabTime.getText().toString(), DateTimeFormatter.ofPattern("H:m"));
        LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);

        long timeBetween = Duration.between(LocalDateTime.now(), eventDateTime).toMillis();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(
                EventWorker.class).setInitialDelay(timeBetween, TimeUnit.MILLISECONDS).build();

        // Uses event ID so changes to event notifications are updated vice copied
        WorkManager.getInstance(this).enqueueUniqueWork(
                "event_reminder_" + eId, ExistingWorkPolicy.REPLACE, workRequest);

        Toast.makeText(this, "Notification enabled.", Toast.LENGTH_SHORT).show();

    }
}
