package com.example.mobilebookingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

public class ReminderManager {

    private static final String PREF_NAME = "reminder_prefs";
    private static final String KEY_LAST_ACTIVE = "last_active_time";
    private static final String REMINDER_WORK_NAME = "reminder_work";

    private final Context context;
    private final SharedPreferences prefs;

    public ReminderManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void updateLastActiveTime() {
        long currentTime = System.currentTimeMillis();
        prefs.edit().putLong(KEY_LAST_ACTIVE, currentTime).apply();
        scheduleReminder();
    }

    private void scheduleReminder() {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(REMINDER_WORK_NAME);
        OneTimeWorkRequest reminderWork = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build();
        workManager.enqueueUniqueWork(
                REMINDER_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                reminderWork
        );
        android.util.Log.d("ReminderManager", "Уведомление запланировано через 10 секунд");
    }

    public void cancelReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(REMINDER_WORK_NAME);
        android.util.Log.d("ReminderManager", "Уведомление отменено");
    }

    public long getLastActiveTime() {
        return prefs.getLong(KEY_LAST_ACTIVE, 0);
    }
}