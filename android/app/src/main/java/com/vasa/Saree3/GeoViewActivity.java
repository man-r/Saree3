package com.vasa.Saree3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ListView;

import android.support.v4.widget.SimpleCursorAdapter;

public class GeoViewActivity extends AppCompatActivity {

	private ListView geoListView = null;
	private SimpleCursorAdapter listViewDataAdapter = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geo_view);

        geoListView = (ListView)findViewById(R.id.geo_list_view);
        // Create a new SimpleCursorAdapter.
        listViewDataAdapter = new SimpleCursorAdapter(this, R.layout.geo_item_view, cursor, fromColumnArr, toViewIdArr, CursorAdapter.FLAG_AUTO_REQUERY);
    }
}
