package com.vasa.Saree3;

public class Constants {
	public interface ACTION {
		public static String MAIN_ACTION = "com.truiton.foregroundservice.action.main";
 		public static String ENABLEGPS_ACTION = "com.truiton.foregroundservice.action.prev";
 		public static String PLAY_ACTION = "com.truiton.foregroundservice.action.play";
 		public static String NEXT_ACTION = "com.truiton.foregroundservice.action.next";
 		public static String STARTFOREGROUND_ACTION = "com.truiton.foregroundservice.action.startforeground";
 		public static String STOPFOREGROUND_ACTION = "com.truiton.foregroundservice.action.stopforeground";
 		public static String LOCATION_CHANGED_ACTION = "LOCATION_CHANGED_ACTION";
 	}
 
 	public interface NOTIFICATION_ID {
 		public static int FOREGROUND_SERVICE = 101;
 	}

 	public interface PERMISSION {
	 	public static final int ACCESS_NETWORK_STATE = 2;
		public static final int ACCESS_FINE_LOCATION = 3;
		public static final int WAKE_LOCK = 4;
		public static final int INTERNET = 5;
		public static final int READ_PHONE_STATE = 6;
		public static final int WRITE_EXTERNAL_STORAGE = 7;
		public static final int CAMERA = 8;
		public static final int RECORD_AUDIO = 9;
	}

	public interface NOTIFICATION {
		public static final String CHANNEL_ID = "my_channel_01";
	}

	public interface LOCATION {
		public static final int MIN_DISTANCE = 1000;
		public static final int UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
	}

	public interface SQLLITE {
		public static final int DATABASE_VERSION = 1;
    	public static final String DATABASE_NAME = "FeedReader.db";

    	public static final String TABLE_NAME = "geo";
        public static final String COLUMN_NAME_PLAYERID = "playerid";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LONG = "long";
        public static final String COLUMN_NAME_ALT = "alt";
        public static final String COLUMN_NAME_SPEED = "speed";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";

        public static final String SQL_CREATE_GEO =
        	"CREATE TABLE geo (playerid TEXT,lat TEXT, long TEXT, alt TEXT, speed TEXT, timestamp TEXT)";

		public static final String SQL_DELETE_GEO =
    		"DROP TABLE IF EXISTS geo";
	}

	public interface TAGS {
 		public static final String TAG = "manar";
 	}
}