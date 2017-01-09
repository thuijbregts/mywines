package com.example.thomas.mywines.database;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.thomas.mywines.activities.MainActivity;
import com.example.thomas.mywines.alarm.Alarm;
import com.example.thomas.mywines.alarm.AlarmReceiver;
import com.example.thomas.mywines.database.WinesFeedContract.FeedEntry;
import com.example.thomas.mywines.informationclasses.Event;
import com.example.thomas.mywines.informationclasses.Wine;
import com.example.thomas.mywines.activities.winesActivity.WinesActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WinesDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyWines";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATETIME";
    private static final String REAL_TYPE = " REAL";

    private static final String CREATE_TABLE_MY_WINES =
            "CREATE TABLE " + FeedEntry.TABLE_MY_WINES + " (" +
                    FeedEntry.COLUMN_MY_WINES_ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_MY_WINES_WINE_ID + INT_TYPE + " )";

    private static final String CREATE_TABLE_WINES =
            "CREATE TABLE " + FeedEntry.TABLE_WINES + " (" +
                    FeedEntry.COLUMN_WINE_ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_WINE_NAME + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_WINE_APPELLATION + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_WINE_YEAR + INT_TYPE + ", " +
                    FeedEntry.COLUMN_WINE_NOTE + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_WINE_DATE + DATE_TYPE + ", " +
                    FeedEntry.COLUMN_WINE_TYPE + INT_TYPE + ", " +
                    FeedEntry.COLUMN_WINE_THUMBNAIL + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_WINE_IMAGE + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_WINE_SELLER + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_WINE_COMMENT + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_WINE_PRICE + REAL_TYPE + " )";

    private static final String CREATE_TABLE_EVENTS =
            "CREATE TABLE " + FeedEntry.TABLE_EVENTS + " (" +
                    FeedEntry.COLUMN_EVENT_ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_EVENT_NAME + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_EVENT_TYPE + INT_TYPE + ", " +
                    FeedEntry.COLUMN_EVENT_COUNTRY + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_EVENT_CITY + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_EVENT_ADDRESS + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_EVENT_SITE + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_EVENT_DATE_START + DATE_TYPE + ", " +
                    FeedEntry.COLUMN_EVENT_DURATION + INT_TYPE + " )";

    private static final String CREATE_TABLE_WINE_EVENT =
            "CREATE TABLE " + FeedEntry.TABLE_WINE_EVENT + " (" +
                    FeedEntry.COLUMN_WINE_EVENT_ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_WINE_EVENT_WINE_ID + INT_TYPE + ", " +
                    FeedEntry.COLUMN_WINE_EVENT_EVENT_ID + INT_TYPE + " )";

    private static final String CREATE_TABLE_ALARMS =
            "CREATE TABLE " + FeedEntry.TABLE_ALARMS + " (" +
                    FeedEntry.COLUMN_ALARMS_ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_ALARMS_EVENT_ID + INT_TYPE + " )";

    private static final String CREATE_TABLE_APPELLATIONS =
            "CREATE TABLE " + FeedEntry.TABLE_APPELLATIONS + " (" +
                    FeedEntry.COLUMN_APPELLATIONS_ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_APPELLATIONS_NAME + TEXT_TYPE + ", " +
                    FeedEntry.COLUMN_APPELLATIONS_COUNTRY + TEXT_TYPE + " )";

    private Context context;

    public WinesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WINES);
        db.execSQL(CREATE_TABLE_MY_WINES);
        db.execSQL(CREATE_TABLE_EVENTS);
        db.execSQL(CREATE_TABLE_WINE_EVENT);
        db.execSQL(CREATE_TABLE_ALARMS);
        db.execSQL(CREATE_TABLE_APPELLATIONS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FeedEntry.TABLE_WINES);
        db.execSQL("DROP TABLE IF EXISTS " + FeedEntry.TABLE_MY_WINES);
        db.execSQL("DROP TABLE IF EXISTS " + FeedEntry.TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + FeedEntry.TABLE_WINE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + FeedEntry.TABLE_ALARMS);
        db.execSQL("DROP TABLE IF EXISTS " + FeedEntry.TABLE_APPELLATIONS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addWine(Wine wine){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_WINE_NAME, wine.getName());
        values.put(FeedEntry.COLUMN_WINE_APPELLATION, wine.getAppellation());
        values.put(FeedEntry.COLUMN_WINE_YEAR, wine.getYear());
        values.put(FeedEntry.COLUMN_WINE_NOTE, wine.getNote());
        values.put(FeedEntry.COLUMN_WINE_IMAGE, wine.getImage());
        values.put(FeedEntry.COLUMN_WINE_DATE, wine.getDate().getTime());
        values.put(FeedEntry.COLUMN_WINE_TYPE, wine.getType());
        values.put(FeedEntry.COLUMN_WINE_SELLER, wine.getSeller());
        values.put(FeedEntry.COLUMN_WINE_COMMENT, wine.getComment());
        values.put(FeedEntry.COLUMN_WINE_PRICE, wine.getPrice());

        long id = db.insert(FeedEntry.TABLE_WINES, null, values);
        wine.setId(id);

        values = new ContentValues();
        values.put(FeedEntry.COLUMN_MY_WINES_WINE_ID, id);
        db.insert(FeedEntry.TABLE_MY_WINES, null, values);
    }

    public void removeWine(Wine wine){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(FeedEntry.TABLE_WINES, FeedEntry.COLUMN_WINE_ID + "=" + wine.getId(), null);
        db.delete(FeedEntry.TABLE_MY_WINES, FeedEntry.COLUMN_MY_WINES_WINE_ID + "=" + wine.getId(), null);
    }

    public void updateWine(Wine wine){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_WINE_NAME, wine.getName());
        values.put(FeedEntry.COLUMN_WINE_APPELLATION, wine.getAppellation());
        values.put(FeedEntry.COLUMN_WINE_YEAR, wine.getYear());
        values.put(FeedEntry.COLUMN_WINE_NOTE, wine.getNote());
        values.put(FeedEntry.COLUMN_WINE_DATE, wine.getDate().getTime());
        values.put(FeedEntry.COLUMN_WINE_TYPE, wine.getType());
        values.put(FeedEntry.COLUMN_WINE_SELLER, wine.getSeller());
        values.put(FeedEntry.COLUMN_WINE_COMMENT, wine.getComment());
        values.put(FeedEntry.COLUMN_WINE_PRICE, wine.getPrice());

        db.update(FeedEntry.TABLE_WINES, values, FeedEntry.COLUMN_WINE_ID + "=" + wine.getId(), null);
    }

    public ArrayList<Wine> getAllWinesList(){

        ArrayList<Wine> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String table1 = FeedEntry.TABLE_WINES;
        String table2 = FeedEntry.TABLE_MY_WINES;
        String selectQuery = "SELECT * FROM " + table1 + ", " + table2
                + " WHERE " + table1 + "." + FeedEntry.COLUMN_WINE_ID + " = " + table2 + "." + FeedEntry.COLUMN_MY_WINES_WINE_ID
                + " ORDER BY " + table1 + "." + FeedEntry.COLUMN_WINE_NAME + " ASC";
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Wine wine = new Wine(
                        c.getInt(c.getColumnIndex(FeedEntry.COLUMN_WINE_TYPE)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_NAME)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_APPELLATION)),
                        c.getInt(c.getColumnIndex(FeedEntry.COLUMN_WINE_YEAR)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_NOTE)),
                        new Date(c.getLong(c.getColumnIndex(FeedEntry.COLUMN_WINE_DATE))),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_COMMENT)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_SELLER)),
                        c.getFloat(c.getColumnIndex(FeedEntry.COLUMN_WINE_PRICE)));
                wine.setId(c.getLong(c.getColumnIndex(FeedEntry.COLUMN_WINE_ID)));
                wine.setImage(c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_IMAGE)));
                wine.setThumbnail(c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_THUMBNAIL)));
                list.add(wine);
            } while (c.moveToNext());
        }

        c.close();

        return list;
    }

    public ArrayList<Wine> getWineList(String column, String pattern, int order, int type){

        ArrayList<Wine> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String table1 = FeedEntry.TABLE_WINES;
        String table2 = FeedEntry.TABLE_MY_WINES;
        String selectQuery = "SELECT * FROM " + table1 + ", " + table2 +
                " WHERE " + table1 + "." + FeedEntry.COLUMN_WINE_ID + " = " + table2 + "." + FeedEntry.COLUMN_MY_WINES_WINE_ID +
                " AND cast(" + column + " as text) LIKE '%" + pattern + "%' and " + FeedEntry.COLUMN_WINE_TYPE + " = " + type + " ORDER BY ";
        switch(order){
            case WinesActivity.NAME_ASC:
                selectQuery += FeedEntry.COLUMN_WINE_NAME + " ASC";
                break;
            case WinesActivity.NAME_DESC:
                selectQuery += FeedEntry.COLUMN_WINE_NAME + " DESC";
                break;
            case WinesActivity.YEAR_ASC:
                selectQuery += FeedEntry.COLUMN_WINE_YEAR + " ASC";
                break;
            case WinesActivity.YEAR_DESC:
                selectQuery += FeedEntry.COLUMN_WINE_YEAR + " DESC";
                break;
            case WinesActivity.NOTE_ASC:
                selectQuery += FeedEntry.COLUMN_WINE_NOTE + " ASC";
                break;
            case WinesActivity.NOTE_DESC:
                selectQuery += FeedEntry.COLUMN_WINE_NOTE + " DESC";
                break;
        }
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Wine wine = new Wine(
                        c.getInt(c.getColumnIndex(FeedEntry.COLUMN_WINE_TYPE)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_NAME)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_APPELLATION)),
                        c.getInt(c.getColumnIndex(FeedEntry.COLUMN_WINE_YEAR)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_NOTE)),
                        new Date(c.getLong(c.getColumnIndex(FeedEntry.COLUMN_WINE_DATE))),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_COMMENT)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_SELLER)),
                        c.getFloat(c.getColumnIndex(FeedEntry.COLUMN_WINE_PRICE)));
                wine.setId(c.getLong(c.getColumnIndex(FeedEntry.COLUMN_WINE_ID)));
                wine.setImage(c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_IMAGE)));
                wine.setThumbnail(c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_THUMBNAIL)));
                list.add(wine);
            } while (c.moveToNext());
        }

        c.close();
        return list;
    }

    public void updateImages(long id, String image, String thumbnail){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_WINE_IMAGE, image);
        values.put(FeedEntry.COLUMN_WINE_THUMBNAIL, thumbnail);

        db.update(FeedEntry.TABLE_WINES, values, FeedEntry.COLUMN_WINE_ID + "=" + id, null);
    }

    public void addEvent(Event event){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_EVENT_NAME, event.getName());
        values.put(FeedEntry.COLUMN_EVENT_COUNTRY, event.getCountry());
        values.put(FeedEntry.COLUMN_EVENT_CITY, event.getCity());
        values.put(FeedEntry.COLUMN_EVENT_ADDRESS, event.getAddress());
        values.put(FeedEntry.COLUMN_EVENT_SITE, event.getSite());
        values.put(FeedEntry.COLUMN_EVENT_DATE_START, event.getDateStart().getTime());
        values.put(FeedEntry.COLUMN_EVENT_TYPE, event.getType());
        values.put(FeedEntry.COLUMN_EVENT_DURATION, event.getDuration());

        long id = db.insert(FeedEntry.TABLE_EVENTS, null, values);
        event.setId(id);

        if(isIncoming(event.getDateStart()))
            addAlarmForEvent(event);
    }

    public void removeEvent(Event event){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(FeedEntry.TABLE_EVENTS, FeedEntry.COLUMN_EVENT_ID + "=" + event.getId(), null);

        String table1 = FeedEntry.TABLE_WINE_EVENT;

        String selectWineEvents = "SELECT " + FeedEntry.COLUMN_WINE_EVENT_WINE_ID + " FROM " + table1 + " WHERE " + table1 + "." + FeedEntry.COLUMN_WINE_EVENT_EVENT_ID + " = " + event.getId();

        ArrayList<Long> ids = new ArrayList<>();
        Cursor c = db.rawQuery(selectWineEvents, null);
        if (c.moveToFirst()) {
            do {
                ids.add(c.getLong(c.getColumnIndex(c.getColumnName(0))));
            } while (c.moveToNext());
        }

        c.close();
        for(long id:ids)
            db.delete(FeedEntry.TABLE_WINES, FeedEntry.COLUMN_WINE_ID + "=" + id, null);

        db.delete(table1, FeedEntry.COLUMN_WINE_EVENT_EVENT_ID + "=" + event.getId(), null);

        removeAlarmForEvent(event.getId());
    }

    public void updateEvent(Event event){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_EVENT_NAME, event.getName());
        values.put(FeedEntry.COLUMN_EVENT_COUNTRY, event.getCountry());
        values.put(FeedEntry.COLUMN_EVENT_CITY, event.getCity());
        values.put(FeedEntry.COLUMN_EVENT_ADDRESS, event.getAddress());
        values.put(FeedEntry.COLUMN_EVENT_SITE, event.getSite());
        values.put(FeedEntry.COLUMN_EVENT_DATE_START, event.getDateStart().getTime());
        values.put(FeedEntry.COLUMN_EVENT_TYPE, event.getType());
        values.put(FeedEntry.COLUMN_EVENT_DURATION, event.getDuration());

        db.update(FeedEntry.TABLE_EVENTS, values, FeedEntry.COLUMN_EVENT_ID + "=" + event.getId(), null);

        removeAlarmForEvent(event.getId());
        if(isIncoming(event.getDateStart()))
            addAlarmForEvent(event);
    }

    public ArrayList<Event> getAllEventsList(){

        ArrayList<Event> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + FeedEntry.TABLE_EVENTS
                + " ORDER BY " + FeedEntry.COLUMN_EVENT_DATE_START + " DESC";
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Event event = new Event(
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_EVENT_NAME)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_EVENT_COUNTRY)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_EVENT_CITY)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_EVENT_ADDRESS)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_EVENT_SITE)),
                        new Date(c.getLong(c.getColumnIndex(FeedEntry.COLUMN_EVENT_DATE_START))),
                        c.getInt(c.getColumnIndex(FeedEntry.COLUMN_EVENT_DURATION)),
                        c.getInt(c.getColumnIndex(FeedEntry.COLUMN_EVENT_TYPE)));
                event.setId(c.getLong(c.getColumnIndex(FeedEntry.COLUMN_EVENT_ID)));
                list.add(event);
            } while (c.moveToNext());
        }

        c.close();
        return list;
    }

    public ArrayList<Wine> getAllWinesListForEvent(long eventID){

        ArrayList<Wine> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String table1 = FeedEntry.TABLE_WINES;
        String table2 = FeedEntry.TABLE_WINE_EVENT;
        String selectQuery = "SELECT * FROM " + table1 + ", " + table2
                + " WHERE " + table1 + "." + FeedEntry.COLUMN_WINE_ID + " = " + table2 + "." + FeedEntry.COLUMN_WINE_EVENT_WINE_ID
                + " AND " + table2 + "." + FeedEntry.COLUMN_WINE_EVENT_EVENT_ID + " = " + eventID
                + " ORDER BY " + table1 + "." + FeedEntry.COLUMN_WINE_NAME + " ASC";
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Wine wine = new Wine(
                        c.getInt(c.getColumnIndex(FeedEntry.COLUMN_WINE_TYPE)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_NAME)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_APPELLATION)),
                        c.getInt(c.getColumnIndex(FeedEntry.COLUMN_WINE_YEAR)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_NOTE)),
                        new Date(c.getLong(c.getColumnIndex(FeedEntry.COLUMN_WINE_DATE))),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_COMMENT)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_SELLER)),
                        c.getFloat(c.getColumnIndex(FeedEntry.COLUMN_WINE_PRICE)));
                wine.setId(c.getLong(c.getColumnIndex(FeedEntry.COLUMN_WINE_ID)));
                wine.setImage(c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_IMAGE)));
                wine.setThumbnail(c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_THUMBNAIL)));
                list.add(wine);
            } while (c.moveToNext());
        }

        c.close();

        return list;
    }

    public void addWineForEvent(Wine wine, long eventID){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_WINE_NAME, wine.getName());
        values.put(FeedEntry.COLUMN_WINE_APPELLATION, wine.getAppellation());
        values.put(FeedEntry.COLUMN_WINE_YEAR, wine.getYear());
        values.put(FeedEntry.COLUMN_WINE_NOTE, wine.getNote());
        values.put(FeedEntry.COLUMN_WINE_IMAGE, wine.getImage());
        values.put(FeedEntry.COLUMN_WINE_DATE, wine.getDate().getTime());
        values.put(FeedEntry.COLUMN_WINE_TYPE, wine.getType());
        values.put(FeedEntry.COLUMN_WINE_SELLER, wine.getSeller());
        values.put(FeedEntry.COLUMN_WINE_COMMENT, wine.getComment());
        values.put(FeedEntry.COLUMN_WINE_PRICE, wine.getPrice());

        long id = db.insert(FeedEntry.TABLE_WINES, null, values);
        wine.setId(id);

        values = new ContentValues();
        values.put(FeedEntry.COLUMN_WINE_EVENT_WINE_ID, id);
        values.put(FeedEntry.COLUMN_WINE_EVENT_EVENT_ID, eventID);

        db.insert(FeedEntry.TABLE_WINE_EVENT, null, values);
    }

    public void removeWineForEvent(long wineID, long eventID){
        SQLiteDatabase db = getReadableDatabase();

        db.delete(FeedEntry.TABLE_WINES, FeedEntry.COLUMN_WINE_ID + "=" + wineID, null);

        db.delete(FeedEntry.TABLE_WINE_EVENT, FeedEntry.COLUMN_WINE_EVENT_WINE_ID + "=" + wineID
                + " AND " + FeedEntry.COLUMN_WINE_EVENT_EVENT_ID + "=" + eventID, null);
    }

    public ArrayList<Wine> getWineListForEvent(String column, String pattern, int order, int type, long eventID){
        ArrayList<Wine> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String table1 = FeedEntry.TABLE_WINES;
        String table2 = FeedEntry.TABLE_WINE_EVENT;
        String selectQuery = "SELECT * FROM " + table1 + ", " + table2 +
                " WHERE " + table1 + "." + FeedEntry.COLUMN_WINE_ID + " = " + table2 + "." + FeedEntry.COLUMN_WINE_EVENT_WINE_ID
                + " AND " + table2 + "." + FeedEntry.COLUMN_WINE_EVENT_EVENT_ID + " = " + eventID
                + " AND cast(" + column + " as text) LIKE '%" + pattern + "%' and " + FeedEntry.COLUMN_WINE_TYPE + " = " + type + " ORDER BY ";
        switch(order){
            case WinesActivity.NAME_ASC:
                selectQuery += FeedEntry.COLUMN_WINE_NAME + " ASC";
                break;
            case WinesActivity.NAME_DESC:
                selectQuery += FeedEntry.COLUMN_WINE_NAME + " DESC";
                break;
            case WinesActivity.YEAR_ASC:
                selectQuery += FeedEntry.COLUMN_WINE_YEAR + " ASC";
                break;
            case WinesActivity.YEAR_DESC:
                selectQuery += FeedEntry.COLUMN_WINE_YEAR + " DESC";
                break;
            case WinesActivity.NOTE_ASC:
                selectQuery += FeedEntry.COLUMN_WINE_NOTE + " ASC";
                break;
            case WinesActivity.NOTE_DESC:
                selectQuery += FeedEntry.COLUMN_WINE_NOTE + " DESC";
                break;
        }
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Wine wine = new Wine(
                        c.getInt(c.getColumnIndex(FeedEntry.COLUMN_WINE_TYPE)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_NAME)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_APPELLATION)),
                        c.getInt(c.getColumnIndex(FeedEntry.COLUMN_WINE_YEAR)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_NOTE)),
                        new Date(c.getLong(c.getColumnIndex(FeedEntry.COLUMN_WINE_DATE))),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_COMMENT)),
                        c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_SELLER)),
                        c.getFloat(c.getColumnIndex(FeedEntry.COLUMN_WINE_PRICE)));
                wine.setId(c.getLong(c.getColumnIndex(FeedEntry.COLUMN_WINE_ID)));
                wine.setImage(c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_IMAGE)));
                wine.setThumbnail(c.getString(c.getColumnIndex(FeedEntry.COLUMN_WINE_THUMBNAIL)));
                list.add(wine);
            } while (c.moveToNext());
        }

        c.close();
        return list;
    }

    public void addAlarmForEvent(Event event){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_ALARMS_EVENT_ID, event.getId());
        long id = db.insert(FeedEntry.TABLE_ALARMS, null, values);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if(prefs.getBoolean("notification_enabled", true))
            new Alarm(context, (int) id, event.getName(), event.getDateStart());
    }

    public void removeAlarmForEvent(long eventId){
        SQLiteDatabase db = getWritableDatabase();

        disableAlarmWithId(getAlarmForEvent(eventId));

        db.delete(FeedEntry.TABLE_ALARMS, FeedEntry.COLUMN_ALARMS_EVENT_ID + "=" + eventId, null);
    }

    public void removeAlarmWithId(int alarmId){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FeedEntry.TABLE_ALARMS, FeedEntry.COLUMN_ALARMS_ID + "=" + alarmId, null);
    }

    public int getAlarmForEvent(long eventId){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT " + FeedEntry.COLUMN_ALARMS_ID + " FROM " + FeedEntry.TABLE_ALARMS
                + " WHERE " + FeedEntry.COLUMN_ALARMS_EVENT_ID + " = " + eventId;
        Cursor c = db.rawQuery(selectQuery, null);

        int ret = 0;
        if (c.moveToFirst()) {
            do {
                ret = c.getInt(c.getColumnIndex(FeedEntry.COLUMN_ALARMS_ID));
                break;
            } while (c.moveToNext());
        }

        c.close();
        return ret;
    }

    public void disableAlarmWithId(int id){
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(alarmIntent);
    }

    public void disableAllAlarms(){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT " + FeedEntry.COLUMN_ALARMS_ID + " FROM " + FeedEntry.TABLE_ALARMS;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            do {
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, c.getInt(c.getColumnIndex(FeedEntry.COLUMN_ALARMS_ID)), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmMgr.cancel(alarmIntent);
                break;
            } while (c.moveToNext());
        }

        c.close();
    }

    public void enableAllAlarms(){
        SQLiteDatabase db = getReadableDatabase();
        String table1 = FeedEntry.TABLE_EVENTS;
        String table2 = FeedEntry.TABLE_ALARMS;
        String selectQuery = "SELECT " + FeedEntry.COLUMN_ALARMS_ID + " as col1, " + FeedEntry.COLUMN_EVENT_NAME + " as col2, " + FeedEntry.COLUMN_EVENT_DATE_START + " as col3 FROM " + table1 + ", " + table2
                + " WHERE " + table1 + "." + FeedEntry.COLUMN_EVENT_ID + " = " + table2 + "." + FeedEntry.COLUMN_ALARMS_EVENT_ID;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Date date = new Date(c.getLong(c.getColumnIndex("col3")));
                int alarmId = c.getInt(c.getColumnIndex("col1"));
                if(isIncoming(date))
                    new Alarm(context, alarmId, c.getString(c.getColumnIndex("col2")), date);
                else{
                    removeAlarmWithId(alarmId);
                }
                break;
            } while (c.moveToNext());
        }

        c.close();
    }

    private boolean isIncoming(Date dateStart){
        Calendar today = Calendar.getInstance();
        Calendar compare = Calendar.getInstance();
        compare.setTime(dateStart);
        return (today.get(Calendar.YEAR) > compare.get(Calendar.YEAR) || today.get(Calendar.DAY_OF_YEAR) < compare.get(Calendar.DAY_OF_YEAR));
    }

    public void populateAppellationsTable(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("frenchwines.txt")));
            ContentValues values;

            String line;
            while((line = reader.readLine()) != null){
                values = new ContentValues();
                values.put(FeedEntry.COLUMN_APPELLATIONS_NAME, line);
                values.put(FeedEntry.COLUMN_APPELLATIONS_COUNTRY, "fr");
                db.insert(FeedEntry.TABLE_APPELLATIONS, null, values);
            }

            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("americanwines.txt")));
            while((line = reader.readLine()) != null){
                values = new ContentValues();
                values.put(FeedEntry.COLUMN_APPELLATIONS_NAME, line);
                values.put(FeedEntry.COLUMN_APPELLATIONS_COUNTRY, "us");
                db.insert(FeedEntry.TABLE_APPELLATIONS, null, values);
            }

            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("spanishwines.txt")));
            while((line = reader.readLine()) != null){
                values = new ContentValues();
                values.put(FeedEntry.COLUMN_APPELLATIONS_NAME, line);
                values.put(FeedEntry.COLUMN_APPELLATIONS_COUNTRY, "es");
                db.insert(FeedEntry.TABLE_APPELLATIONS, null, values);
            }

            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("italianwines.txt")));
            while((line = reader.readLine()) != null){
                values = new ContentValues();
                values.put(FeedEntry.COLUMN_APPELLATIONS_NAME, line);
                values.put(FeedEntry.COLUMN_APPELLATIONS_COUNTRY, "it");
                db.insert(FeedEntry.TABLE_APPELLATIONS, null, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String[]> getAllAppellations(){
        ArrayList<String[]> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT * FROM " + FeedEntry.TABLE_APPELLATIONS + " ORDER BY " + FeedEntry.COLUMN_APPELLATIONS_COUNTRY + ", " + FeedEntry.COLUMN_APPELLATIONS_NAME + " ASC";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                list.add(new String[]{c.getString(c.getColumnIndex(FeedEntry.COLUMN_APPELLATIONS_NAME)), c.getString(c.getColumnIndex(FeedEntry.COLUMN_APPELLATIONS_COUNTRY))});
            } while (c.moveToNext());
        }

        c.close();
        return list;
    }

    public ArrayList<String[]> getAllAppellationsQuery(String pattern){
        ArrayList<String[]> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT * FROM " + FeedEntry.TABLE_APPELLATIONS + " WHERE " + FeedEntry.COLUMN_APPELLATIONS_NAME
                + " LIKE '%" + pattern + "%' ORDER BY " + FeedEntry.COLUMN_APPELLATIONS_COUNTRY + ", " + FeedEntry.COLUMN_APPELLATIONS_NAME + " ASC";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                list.add(new String[]{c.getString(c.getColumnIndex(FeedEntry.COLUMN_APPELLATIONS_NAME)), c.getString(c.getColumnIndex(FeedEntry.COLUMN_APPELLATIONS_COUNTRY))});
            } while (c.moveToNext());
        }

        c.close();
        return list;
    }

    public void addAppellation(String[] appellation){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_APPELLATIONS_NAME, appellation[0]);
        values.put(FeedEntry.COLUMN_APPELLATIONS_COUNTRY, appellation[1]);
        db.insert(FeedEntry.TABLE_APPELLATIONS, null, values);
    }
}