package com.example.thomas.mywines.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.thomas.mywines.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Alarm {

    public Alarm(Context context, int id, String eventName, Date eventDate){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String alarm_time = preferences.getString("notification_time", "12:00");
        int hour = Integer.parseInt(alarm_time.substring(0, 2));
        int minute = Integer.parseInt(alarm_time.substring(3));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(eventDate);

        calendar.add(Calendar.DAY_OF_MONTH, -1);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("name", eventName);
        intent.putExtra("id", id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

        ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

}