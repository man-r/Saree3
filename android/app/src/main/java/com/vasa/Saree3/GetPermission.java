package com.vasa.Saree3;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.Manifest;


import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.Manifest;


import android.util.Log;
public class GetPermission extends Activity  implements ActivityCompat.OnRequestPermissionsResultCallback {

public static final String TAG = "permission";
private static final int REQUEST_LOCATION = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(TAG,"onCreate");
    super.onCreate(savedInstanceState);

    getPermissions();


  }

  void getPermissions(){

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }


    else {
      Intent returnIntent = new Intent();
      returnIntent.putExtra("json", "all permission granted");
      setResult(RESULT_OK,returnIntent);
      finish();
    }

  }
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

      if (requestCode == REQUEST_LOCATION) {
          // BEGIN_INCLUDE(permission_result)
          // Received permission result for camera permission.
          Log.i(TAG, "Received response for LOCATION permission request.");

          // Check if the only required permission has been granted
          if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              // Camera permission has been granted, preview can be displayed
              Log.i(TAG, "LOCATION permission has now been granted. Showing preview.");
              Intent returnIntent = new Intent();
              returnIntent.putExtra("json", "all permission granted");
              setResult(RESULT_OK,returnIntent);
              finish();

          } else {
              Log.i(TAG, "LOCATION permission was NOT granted.");
              Intent returnIntent = new Intent();
              returnIntent.putExtra("json", "location permission not granted");
              setResult(RESULT_OK,returnIntent);
              finish();

          }
          // END_INCLUDE(permission_result)

      }
  }
}
