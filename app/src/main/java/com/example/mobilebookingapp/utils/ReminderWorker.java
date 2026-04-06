package com.example.mobilebookingapp.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.mobilebookingapp.utils.NotificationHelper;

public class ReminderWorker extends Worker {
    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {
        Log.d("ReminderWorker", "Работа выполняется...");

        try {
            NotificationHelper.showReminderNotification(getApplicationContext());
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}