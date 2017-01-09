package com.example.thomas.mywines.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.MainActivity;
import com.example.thomas.mywines.activities.eventsActivity.EventsActivity;


public class NotificationMessage {
    private Context context;
    private String eventName;

    public NotificationMessage(Context context, String eventName){
        this.context = context;
        this.eventName = eventName;
        createNotification();
    }

    public void createNotification(){
        Intent resultIntent = new Intent(context, EventsActivity.class);
        resultIntent.putExtra("fromNotification", true);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(eventName)
                .setContentText(context.getResources().getString(R.string.notification_title))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(MainActivity.NOTIFICATION_ID, mBuilder.build());
    }

}
