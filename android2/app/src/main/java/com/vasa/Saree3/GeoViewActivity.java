package com.vasa.Saree3;

import android.content.ContextWrapper;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeoViewActivity extends AppCompatActivity {

	private ListView geoListView = null;
	private SimpleCursorAdapter listViewDataAdapter = null;
	private Button download_button = null;
    private ProgressBar pb = null;
    int progress_i = 0;
    private Handler hdlr = new Handler();
    private final String fromColumnArr[] = {
    	"_id",
        "playerid",
        "accuracy",
    	"latitude", 
    	"longitude",
    	"altitude",
        "timestamp",
        "speed"};

    private final int toViewIdArr[] = {
    	R.id.geo_list_item_geoid,
        R.id.geo_list_item_playerid,
        R.id.geo_list_item_accuracy,
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
        pb = (ProgressBar) findViewById(R.id.pbLoading);

		download_button = (Button)findViewById(R.id.button_id);
		download_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                pb.setVisibility(ProgressBar.VISIBLE);
                progress_i = pb.getProgress();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            GeoReaderDbHelper mDbHelper = new GeoReaderDbHelper(getApplicationContext());
                            SQLiteDatabase db = mDbHelper.getReadableDatabase();

                            Cursor cursor = db.rawQuery ("SELECT * FROM geo", null);
                            String content = "";
                            cursor.moveToFirst();
                            pb.setMax(cursor.getCount());
                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                content = content + cursor.getColumnName(i) + ",";
                            }
                            content = content + "\n";

                            for (int i = 0; i < cursor.getCount(); i++) {

                                cursor.moveToPosition(i);
                                pb.setProgress(i);
                                for (int j = 0; j < cursor.getColumnCount(); j++) {
                                    Log.d("manar", "i = " + i + " - j = " + j);
                                    content = content + cursor.getString(j) + ",";
                                }
                                content = content + "\n";
                            }

                            Log.d("manar", content);

                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());



                            Log.d("manar", getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());
                            OutputStreamWriter outputStreamWriter =
                                    new OutputStreamWriter(
                                            new FileOutputStream(
                                                    new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), timeStamp + "_" + ".csv")));

                            outputStreamWriter.append(content);
                            outputStreamWriter.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("manar", e.getMessage());
                            Log.e("manar", e.toString());
                        } finally {
                            // run a background job and once complete
                            pb.setVisibility(ProgressBar.INVISIBLE);
                        }
                    }
                }).start();

			}
		});
        GeoReaderDbHelper mDbHelper = new GeoReaderDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery ("SELECT * FROM geo", null);

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
