package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

// Primary node for event CRUD and SMS notifications
public class AddEventActivity extends AppCompatActivity {

    // References: https://developer.android.com/training/permissions/requesting

    private EditText editFabName;
    private EditText editFabDate;
    private EditText editFabTime;
    private EditText editFabNotes;
    private Button buttonFabSave;
    private Button buttonFabCancel;
    private CheckBox checkBoxNotification;

    // Database variable
    private EventRepository eventRepository;

    // Variable that changes value when event is created
    private int eId = -1;

    // Variable added to ensure data stays unique to user accounts
    private int userId;

    // Generates SMS requests
    private final ActivityResultLauncher<String> smsNotificationLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {

                // Sends notifications if permission granted, otherwise disables SMS but saves event.
                @Override
                public void onActivityResult(Boolean userPermission) {
                    if (userPermission) {
                        sendNotification();
                    } else {
                        Toast.makeText(AddEventActivity.this, "Notification disabled",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

    // Correlates class to activity_add_events layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_events);

        // Variables provide database connectivity and correlate to xml layout
        eventRepository = EventRepository.getInstance(this);
        editFabName = findViewById(R.id.editFabName);
        editFabDate = findViewById(R.id.editFabDate);
        editFabTime = findViewById(R.id.editFabTime);
        editFabNotes = findViewById(R.id.editFabNotes);
        buttonFabSave = findViewById(R.id.buttonFabSave);
        buttonFabCancel = findViewById(R.id.buttonFabCancel);
        checkBoxNotification = findViewById(R.id.checkBoxNotification);

        // Event ID value changes once event created or clicked
        eId = getIntent().getIntExtra("pickEvent", -1);

        // Added to support unique account information
        userId = getIntent().getIntExtra("userId", -1);
        EventTable event = null;

        // Check for existing event
        if (eId != -1) {
            event = eventRepository.getEventId(eId, userId);
        }

        // Pull event data if event exists
        if (event != null) {
            editFabName.setText(event.eventName);
            editFabDate.setText(event.eventDate);
            editFabTime.setText(event.eventTime);
            editFabNotes.setText(event.eventNotes);
            checkBoxNotification.setChecked(event.eventNotification);

        }

        // Provides save button functionality
        buttonFabSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = editFabName.getText().toString();
                String date = editFabDate.getText().toString();
                String time = editFabTime.getText().toString();
                String notes = editFabNotes.getText().toString();
                boolean notification = checkBoxNotification.isChecked();

                // Validates input data
                if (name.isEmpty() || date.isEmpty() || time.isEmpty()) {
                    Toast.makeText(AddEventActivity.this,
                            "Complete input fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                EventTable addEvent;

                // Adds event if ID is placeholder value, otherwise loads existing event
                if (eId == -1) {
                    addEvent = new EventTable();
                    addEvent.userId = userId;
                } else {
                    addEvent = eventRepository.getEventId(eId, userId);
                    if (addEvent == null) {
                        return;
                    }
                }

                // Adds event information into card
                addEvent.eventName = name;
                addEvent.eventDate = date;
                addEvent.eventTime = time;
                addEvent.eventNotes = notes;
                addEvent.eventNotification = notification;

                // Insert new or update existing event
                if (eId == -1) {
                    eventRepository.insertEvent(addEvent);
                } else {
                    eventRepository.updateEvent(addEvent);
                }

                // Checks if notification box is checked and requests permission. Linked to boolean.
                if (notification) {
                    if (ContextCompat.checkSelfPermission(
                            AddEventActivity.this,
                            Manifest.permission.SEND_SMS) ==
                            PackageManager.PERMISSION_GRANTED) {
                        sendNotification();
                    } else {
                        smsNotificationLauncher.launch(Manifest.permission.SEND_SMS);
                    }
                }

                // Destroys activity, returns to homescreen
                finish();

            }

        });

        // Cancels event creation, returns to homescreen
        buttonFabCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // Method for notification message
    private void sendNotification() {
        String smsNotification = "Event today, check app for details!";
        SmsManager smsManager = getSystemService(SmsManager.class);
        smsManager.sendTextMessage("123456789", null,
                smsNotification, null, null);
    }

}
