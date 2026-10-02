package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.telephony.SmsManager;

// Reference: https://www.geeksforgeeks.org/android/sending-a-text-message-over-the-phone-using-smsmanager-in-android/

// Forwards SMS notification, supports EventWorker for background operations.
public class SmsNotificationManager {

    public static void smsEventReminder() {

        SmsManager smsManager = SmsManager.getDefault();

        smsManager.sendTextMessage(
                "123456789",
                null,
                "Reminder: ",
                null,
                null
        );
    }
}
