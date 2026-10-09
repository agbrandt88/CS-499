package com.example.brandt_cs360_projecttwo_eventtrackingapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

// Reference: https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started#java
// Reference: https://www.geeksforgeeks.org/kotlin/android-jetpack-workmanager-with-example/
// Reference: https://medium.com/@iampranshu2003/mastering-workmanager-in-android-a-comprehensive-guide-2-f529f362ad28

// Worker for background event notifications, communicates with SmsNotificationManager for SMS
// functionality.
public class EventWorker extends Worker {

    public EventWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {

            SmsNotificationManager.smsEventReminder();

            return Result.success();

        } catch (Exception exception) {

            return Result.failure();
        }
    }
}
