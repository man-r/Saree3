package com.vasa.Saree3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;

public class GeoViewActivity extends AppCompatActivity {

	private ListView geoListView = null;
	private SimpleCursorAdapter listViewDataAdapter = null;

    private final String fromColumnArr[] = {
    	"playerid",
    	"latitude", 
    	"longitude",
    	"altitude",
        "timestamp",
        "speed"};

    private final int toViewIdArr[] = {
    	R.id.geo_list_item_playerid,
    	R.id.geo_list_item_latitude, 
    	R.id.geo_list_item_longitude,
    	R.id.geo_list_item_altitude,
        R.id.geo_list_item_timestamp,
        R.id.geo_list_item_speed };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geo_view);

        geoListView = (ListView)findViewById(R.id.geo_list_view);

        GeoReaderDbHelper mDbHelper = new GeoReaderDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery ("SELECT * FROM geo",null);

		// Create a new SimpleCursorAdapter.
        listViewDataAdapter = new SimpleCursorAdapter(this, R.layout.geo_item_view, cursor,
        	fromColumnArr,
        	toViewIdArr,
        	CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //listViewDataAdapter.notifyDataSetChanged();

        // Set simple cursor adapter to list view.
        geoListView.setAdapter(listViewDataAdapter);

    }
}
