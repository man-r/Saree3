package com.vasa.Saree3;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.Manifest;


import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.Manifest;


import android.util.Log;
public class GetPermission extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(Constants.TAGS.TAG,"onCreate");
        super.onCreate(savedInstanceState);
        getPermissions();
    }

    void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, Constants.PERMISSION.ACCESS_NETWORK_STATE);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, Constants.PERMISSION.ACCESS_NETWORK_STATE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //get Location permission
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION.ACCESS_FINE_LOCATION);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION.ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //get Location permission
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WAKE_LOCK)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK}, Constants.PERMISSION.WAKE_LOCK);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK}, Constants.PERMISSION.WAKE_LOCK);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //get Location permission
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, Constants.PERMISSION.INTERNET);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, Constants.PERMISSION.INTERNET);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //get Location permission
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.PERMISSION.READ_PHONE_STATE);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.PERMISSION.READ_PHONE_STATE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //get Location permission
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSION.WRITE_EXTERNAL_STORAGE);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSION.WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //get Location permission
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constants.PERMISSION.CAMERA);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constants.PERMISSION.CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //get Location permission
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, Constants.PERMISSION.RECORD_AUDIO);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, Constants.PERMISSION.RECORD_AUDIO);

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

        if (requestCode == Constants.PERMISSION.ACCESS_NETWORK_STATE) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(Constants.TAGS.TAG, "Received response for ACCESS_NETWORK_STATE permission request.");

            // Check if the only required permission has been granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ACCESS_NETWORK_STATE permission has been granted, preview can be displayed
                Log.i(Constants.TAGS.TAG, "ACCESS_NETWORK_STATE permission has now been granted. Showing preview.");
                getPermissions();

            } else {
                  Log.i(Constants.TAGS.TAG, "ACCESS_NETWORK_STATE permission was NOT granted.");
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("json", "ACCESS_NETWORK_STATE permission not granted");
                  setResult(RESULT_OK,returnIntent);
                  finish();

              }
              // END_INCLUDE(permission_result)

          }

          if (requestCode == Constants.PERMISSION.ACCESS_FINE_LOCATION) {
              // BEGIN_INCLUDE(permission_result)
              // Received permission result for camera permission.
              Log.i(Constants.TAGS.TAG, "Received response for ACCESS_FINE_LOCATION permission request.");

              // Check if the only required permission has been granted
              if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  // Camera permission has been granted, preview can be displayed
                  Log.i(Constants.TAGS.TAG, "ACCESS_FINE_LOCATION permission has now been granted. Showing preview.");
                  getPermissions();

              } else {
                  Log.i(Constants.TAGS.TAG, "ACCESS_FINE_LOCATION permission was NOT granted.");
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("json", "ACCESS_FINE_LOCATION permission not granted");
                  setResult(RESULT_OK,returnIntent);
                  finish();

              }
              // END_INCLUDE(permission_result)

          }

          if (requestCode == Constants.PERMISSION.WAKE_LOCK) {
              // BEGIN_INCLUDE(permission_result)
              // Received permission result for camera permission.
              Log.i(Constants.TAGS.TAG, "Received response for WAKE_LOCK permission request.");

              // Check if the only required permission has been granted
              if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  // Camera permission has been granted, preview can be displayed
                  Log.i(Constants.TAGS.TAG, "WAKE_LOCK permission has now been granted. Showing preview.");
                  getPermissions();

              } else {
                  Log.i(Constants.TAGS.TAG, "WAKE_LOCK permission was NOT granted.");
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("json", "WAKE_LOCK permission not granted");
                  setResult(RESULT_OK,returnIntent);
                  finish();

              }
              // END_INCLUDE(permission_result)

          }

          if (requestCode == Constants.PERMISSION.INTERNET) {
              // BEGIN_INCLUDE(permission_result)
              // Received permission result for camera permission.
              Log.i(Constants.TAGS.TAG, "Received response for INTERNET permission request.");

              // Check if the only required permission has been granted
              if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  // INTERNET permission has been granted, preview can be displayed
                  Log.i(Constants.TAGS.TAG, "INTERNET permission has now been granted. Showing preview.");
                  getPermissions();

              } else {
                  Log.i(Constants.TAGS.TAG, "INTERNET permission was NOT granted.");
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("json", "INTERNET permission not granted");
                  setResult(RESULT_OK,returnIntent);
                  finish();

              }
              // END_INCLUDE(permission_result)

          }


          if (requestCode == Constants.PERMISSION.READ_PHONE_STATE) {
              // BEGIN_INCLUDE(permission_result)
              // Received permission result for camera permission.
              Log.i(Constants.TAGS.TAG, "Received response for READ_PHONE_STATE permission request.");

              // Check if the only required permission has been granted
              if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  // READ_PHONE_STATE permission has been granted, preview can be displayed
                  Log.i(Constants.TAGS.TAG, "READ_PHONE_STATE permission has now been granted. Showing preview.");
                  getPermissions();

              } else {
                  Log.i(Constants.TAGS.TAG, "READ_PHONE_STATE permission was NOT granted.");
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("json", "READ_PHONE_STATE permission not granted");
                  setResult(RESULT_OK,returnIntent);
                  finish();

              }
              // END_INCLUDE(permission_result)

          }


          if (requestCode == Constants.PERMISSION.WRITE_EXTERNAL_STORAGE) {
              // BEGIN_INCLUDE(permission_result)
              // Received permission result for camera permission.
              Log.i(Constants.TAGS.TAG, "Received response for WRITE_EXTERNAL_STORAGE permission request.");

              // Check if the only required permission has been granted
              if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  // WRITE_EXTERNAL_STORAGE permission has been granted, preview can be displayed
                  Log.i(Constants.TAGS.TAG, "WRITE_EXTERNAL_STORAGE permission has now been granted. Showing preview.");
                  getPermissions();

              } else {
                  Log.i(Constants.TAGS.TAG, "WRITE_EXTERNAL_STORAGE permission was NOT granted.");
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("json", "WRITE_EXTERNAL_STORAGE permission not granted");
                  setResult(RESULT_OK,returnIntent);
                  finish();

              }
              // END_INCLUDE(permission_result)

          }


          if (requestCode == Constants.PERMISSION.CAMERA) {
              // BEGIN_INCLUDE(permission_result)
              // Received permission result for camera permission.
              Log.i(Constants.TAGS.TAG, "Received response for CAMERA permission request.");

              // Check if the only required permission has been granted
              if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  // CAMERA permission has been granted, preview can be displayed
                  Log.i(Constants.TAGS.TAG, "CAMERA permission has now been granted. Showing preview.");
                  getPermissions();

              } else {
                  Log.i(Constants.TAGS.TAG, "CAMERA permission was NOT granted.");
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("json", "CAMERA permission not granted");
                  setResult(RESULT_OK,returnIntent);
                  finish();

              }
              // END_INCLUDE(permission_result)

          }


          if (requestCode == Constants.PERMISSION.RECORD_AUDIO) {
              // BEGIN_INCLUDE(permission_result)
              // Received permission result for camera permission.
              Log.i(Constants.TAGS.TAG, "Received response for RECORD_AUDIO permission request.");

              // Check if the only required permission has been granted
              if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  // Camera permission has been granted, preview can be displayed
                  Log.i(Constants.TAGS.TAG, "RECORD_AUDIO permission has now been granted. Showing preview.");
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("json", "all permission granted");
                  setResult(RESULT_OK,returnIntent);
                  finish();

              } else {
                  Log.i(Constants.TAGS.TAG, "RECORD_AUDIO permission was NOT granted.");
                  Intent returnIntent = new Intent();
                  returnIntent.putExtra("json", "RECORD_AUDIO permission not granted");
                  setResult(RESULT_OK,returnIntent);
                  finish();

              }
              // END_INCLUDE(permission_result)
          }
        }
    }