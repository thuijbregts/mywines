package com.example.thomas.mywines.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.thomas.mywines.database.WinesDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new NotificationMessage(context, intent.getStringExtra("name"));
        WinesDbHelper dbHelper = new WinesDbHelper(context);
        dbHelper.removeAlarmWithId(intent.getIntExtra("id", -1));
    }
}