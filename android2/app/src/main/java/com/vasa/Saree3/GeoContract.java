package com.vasa.Saree3;

import android.provider.BaseColumns;

public final class GeoContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private GeoContract() {}

    /* Inner class that defines the table contents */
    public static class GeoEntry implements BaseColumns {
        public static final String TABLE_NAME = "geo";
        public static final String COLUMN_NAME_PLAYERID = "playerid";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LONG = "long";
        public static final String COLUMN_NAME_ALT = "alt";
        public static final String COLUMN_NAME_SPEED = "speed";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";

    }
}
