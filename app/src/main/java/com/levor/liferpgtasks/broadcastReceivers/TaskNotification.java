package com.levor.liferpgtasks.broadcastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.view.activities.MainActivity;

public class TaskNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getExtras().getString(LifeController.TASK_TITLE_NOTIFICATION_TAG);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.putExtra(LifeController.TASK_TITLE_NOTIFICATION_TAG, taskTitle);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, 0);
        Notification n  = new Notification.Builder(context)
                .setContentTitle(taskTitle)
                .setContentText(context.getString(R.string.notification_text))
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
        notificationManager.notify(0, n);
    }
}
