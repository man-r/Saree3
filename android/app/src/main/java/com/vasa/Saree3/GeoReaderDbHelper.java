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
    public static final String COLUMN_NAME_LAT = "lat";
    public static final String COLUMN_NAME_LONG = "long";
    public static final String COLUMN_NAME_ALT = "alt";
    public static final String COLUMN_NAME_SPEED = "speed";
    public static final String COLUMN_NAME_TIMESTAMP = "timestamp";

    private static final String SQL_CREATE_GEO =
        "CREATE TABLE geo (playerid TEXT,lat TEXT, long TEXT, alt TEXT, speed TEXT, timestamp TEXT)";

    private static final String SQL_DELETE_GEO =
        "DROP TABLE IF EXISTS geo";
        
    public GeoReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_GEO);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_GEO);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}