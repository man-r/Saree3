package com.vasa.Saree3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GeoReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Saree3.db";

    public static final String TABLE_NAME = "geo";
    public static final String COLUMN_NAME_PLAYERID = "playerid";
    public static final String COLUMN_NAME_LAT = "latitude";
    public static final String COLUMN_NAME_LONG = "longitude";
    public static final String COLUMN_NAME_ALT = "altitude";
    public static final String COLUMN_NAME_SPEED = "speed";
    public static final String COLUMN_NAME_TIMESTAMP = "timestamp";

    private static final String SQL_CREATE_GEO =
        "CREATE TABLE geo (playerid TEXT,latitude TEXT, longitude TEXT, altitude TEXT, speed TEXT, timestamp TIMESTAMP,createtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private static final String SQL_DELETE_GEO =
        "DROP TABLE IF EXISTS geo";
    
    private static final String SQL_CREATE_ACTIVITY =
        "CREATE TABLE activity (playerid TEXT,act_type TEXT, transition_type TEXT, elapsed_realtime TEXT, createtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private static final String SQL_DELETE_ACTIVITY =
        "DROP TABLE IF EXISTS activity";
     

    public GeoReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_GEO);
        db.execSQL(SQL_CREATE_ACTIVITY);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_GEO);
        db.execSQL(SQL_DELETE_ACTIVITY);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}