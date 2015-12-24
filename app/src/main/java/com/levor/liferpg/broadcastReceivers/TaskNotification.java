package com.levor.liferpg.broadcastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.levor.liferpg.R;
import com.levor.liferpg.View.Activities.MainActivity;
import com.levor.liferpg.View.Fragments.Tasks.AddTaskFragment;

public class TaskNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
        String taskTitle = intent.getExtras().getString(AddTaskFragment.TASK_TITLE_TAG);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra(AddTaskFragment.TASK_TITLE_TAG, taskTitle);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, 0);
        Notification n  = new Notification.Builder(context)
                .setContentTitle(taskTitle)
                .setContentText("Task need to be performed")
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
        notificationManager.notify(0, n);
    }
}
