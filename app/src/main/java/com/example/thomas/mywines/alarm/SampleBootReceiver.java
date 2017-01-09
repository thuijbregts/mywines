package com.example.thomas.mywines.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.thomas.mywines.database.WinesDbHelper;

public class SampleBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            WinesDbHelper dbHelper = new WinesDbHelper(context);
            dbHelper.enableAllAlarms();
        }
    }
}