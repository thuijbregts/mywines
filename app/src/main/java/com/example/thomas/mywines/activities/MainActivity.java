package com.example.thomas.mywines.activities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.thomas.mywines.R;
import com.example.thomas.mywines.activities.calendarActivity.CalendarActivity;
import com.example.thomas.mywines.activities.eventsActivity.EventsActivity;
import com.example.thomas.mywines.activities.settingsActivity.SettingsActivity;
import com.example.thomas.mywines.activities.winesActivity.WinesActivity;
import com.example.thomas.mywines.database.WinesDbHelper;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {

    public static final String TITLE_FONT = "Cardinal.ttf";
    public static final String GENERAL_FONT = "morris.ttf";
    public static final String CALENDAR_FONT = "morris.ttf";
    public static final String MAIN_FONT = "Prince Valiant.ttf";

    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    public static int NOTIFICATION_ID = 0;
    public static File directory;
    public static WinesDbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        directory = cw.getDir("images", Context.MODE_PRIVATE);
        File output = new File(directory, ".nomedia");
        if(!output.exists())
            try {
                output.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        dbHelper = new WinesDbHelper(this);

        if(!preferences.getBoolean("isTableAppellationsFilled", false)){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dbHelper.populateAppellationsTable();
                }
            });
            t.start();
            editor.putBoolean("isTableAppellationsFilled", true);
            editor.apply();
        }

        Button buttonWines = (Button) findViewById(R.id.buttonWines);
        buttonWines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WinesActivity.class);
                startActivity(intent);
            }
        });

        Button buttonEvents = (Button) findViewById(R.id.buttonEvents);
        buttonEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventsActivity.class);
                startActivity(intent);
            }
        });

        Button buttonCalendar = (Button) findViewById(R.id.buttonCalendar);

        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        Button buttonSettings = (Button) findViewById(R.id.buttonSettings);

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    public static int convertDpToPixel(float dp, Context context){
        Resources r = context.getResources();

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();

        return (int) (px / (metrics.densityDpi / 160f));
    }

    public static int getScreenHeightDp(Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return convertPixelsToDp(metrics.heightPixels, activity);
    }
}
