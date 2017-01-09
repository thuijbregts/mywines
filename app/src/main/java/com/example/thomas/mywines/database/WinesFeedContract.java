package com.example.thomas.mywines.database;

import android.provider.BaseColumns;

public final class WinesFeedContract {

    public WinesFeedContract() {}

    public static abstract class FeedEntry implements BaseColumns {

        public static final String TABLE_WINES = "wines";
        public static final String COLUMN_WINE_ID = "wines_id";
        public static final String COLUMN_WINE_NAME = "wines_name";
        public static final String COLUMN_WINE_APPELLATION = "wines_appellation";
        public static final String COLUMN_WINE_YEAR = "wines_year";
        public static final String COLUMN_WINE_NOTE = "wines_note";
        public static final String COLUMN_WINE_THUMBNAIL = "wines_thumbnail";
        public static final String COLUMN_WINE_IMAGE = "wines_image";
        public static final String COLUMN_WINE_SELLER = "wines_seller";
        public static final String COLUMN_WINE_COMMENT = "wines_comment";
        public static final String COLUMN_WINE_DATE = "wines_date_taste";
        public static final String COLUMN_WINE_TYPE = "wines_type";
        public static final String COLUMN_WINE_PRICE= "wines_price";

        public static final String TABLE_MY_WINES = "my_wines";
        public static final String COLUMN_MY_WINES_ID = "my_wines_id";
        public static final String COLUMN_MY_WINES_WINE_ID = "my_wines_wine_id";

        public static final String TABLE_EVENTS = "events";
        public static final String COLUMN_EVENT_ID = "events_id";
        public static final String COLUMN_EVENT_NAME = "events_name";
        public static final String COLUMN_EVENT_COUNTRY = "events_country";
        public static final String COLUMN_EVENT_CITY = "events_city";
        public static final String COLUMN_EVENT_ADDRESS = "events_address";
        public static final String COLUMN_EVENT_SITE = "events_site";
        public static final String COLUMN_EVENT_DATE_START = "events_date_start";
        public static final String COLUMN_EVENT_DURATION = "events_duration";
        public static final String COLUMN_EVENT_TYPE = "events_type";

        public static final String TABLE_WINE_EVENT = "wine_event";
        public static final String COLUMN_WINE_EVENT_ID = "wine_event_id";
        public static final String COLUMN_WINE_EVENT_WINE_ID = "wine_event_wine_id";
        public static final String COLUMN_WINE_EVENT_EVENT_ID = "wine_event_event_id";

        public static final String TABLE_ALARMS = "alarms";
        public static final String COLUMN_ALARMS_ID = "alarms_id";
        public static final String COLUMN_ALARMS_EVENT_ID = "alarms_event_id";

        public static final String TABLE_APPELLATIONS = "appellations";
        public static final String COLUMN_APPELLATIONS_ID = "appellations_id";
        public static final String COLUMN_APPELLATIONS_NAME = "appellations_appellation";
        public static final String COLUMN_APPELLATIONS_COUNTRY = "appellations_country";
    }
}