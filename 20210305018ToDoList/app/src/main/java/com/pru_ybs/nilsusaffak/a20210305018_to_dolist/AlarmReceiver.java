package com.pru_ybs.nilsusaffak.a20210305018_to_dolist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "task_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskName = intent.getStringExtra("taskName");

        Log.d("AlarmReceiver", "Alarm Triggered!");
        // Bildirimi burada tetikleyin.

        // Bildirim Yöneticisini Al
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android 13 (Tiramisu) için POST_NOTIFICATIONS izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) !=
                        android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // İzin yoksa, bildirim gönderme
            return;
        }

        // Bildirim Kanalını Oluştur (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Task Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Task Reminder Notifications");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Bildirimi Oluştur ve Gönder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Task Reminder")
                .setContentText("Your duty: " + taskName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.SECOND, 10); // 10 saniye sonra tetiklenecek

    }
}



