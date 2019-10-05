package com.vasa.Saree3;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CameraActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;

    private Camera mCamera;
    private CameraPreview mPreview;

    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private TextView ocrInfo;
    private TextView faceInfo;

    BarcodeDetector barcodeDetector;
    FaceDetector faceDetector;
    CameraSource cameraSource;

	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    switch(menuItem.getItemId()){
                        case R.id.close:

                            break;
                            
                        case R.id.GPSSwitch:

                            break;
                            
                        case R.id.share:

                            break;
                            
                        case R.id.chalange:

                            break;
                            
                        case R.id.pip:
                            //Intent topIntent = new Intent(getApplicationContext(), TopTen.class);
                            //startActivityForResult(topIntent, 0);
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                enterPictureInPictureMode();
                            } else {
                                // Toast.makeText(getApplicationContext(), "feature avialble in android oreo and above", Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case R.id.map:
                            Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivityForResult(mapIntent, 0);
                            break;
                        case R.id.camera:
                            Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                            startActivityForResult(cameraIntent, 0);
                            break;
                            
                        default:
                            break;
                            
                        }
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();

                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here

                    return true;
                }
            }
        );
        // check for permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            getPermissions();
        } else {
            startCamera();
        }
    }

    void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constants.PERMISSION.CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            // Check if the only required permission has been granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                startCamera();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else {
                Log.i(Constants.TAGS.TAG, "CAMERA permission was NOT granted.");
                finish();
              }
        }
    }
    void startCamera(){
        // Create an instance of Camera
        mCamera = getCameraInstance();

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        //parameters.setFocusAreas(Lists.newArrayList(new Camera.Area(rect, 500)));

        mCamera.setParameters(parameters);
        // Create our Preview view and set it as the content of our activity.
        // mPreview = new CameraPreview(this, mCamera);
        // FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        // preview.addView(mPreview);


        cameraView = (SurfaceView)findViewById(R.id.camera_view);
        barcodeInfo = (TextView)findViewById(R.id.code_info);
        ocrInfo = (TextView)findViewById(R.id.ocr_info);
       	faceInfo = (TextView)findViewById(R.id.face_info);

        barcodeDetector = new BarcodeDetector.Builder(this).build();
        barcodeDetector.setProcessor(new Detector.Processor() {
        	@Override 
        	public void release() { }

			@Override
			public void receiveDetections(Detector.Detections detections) {
				final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {

                    barcodeInfo.post(new Runnable() {
                        // Use the post method of the TextView
                        public void run() {
                        	for(int i=0; i<barcodes.size(); i++){
		                    	int key = barcodes.keyAt(i);
		                    	Log.d("Element at "+key, " is "+barcodes.get(key));
		                    }
                            barcodeInfo.setText(barcodes.valueAt(0).displayValue);
                            
                        }
                    });
                }
			}
        });
        
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        textRecognizer.setProcessor(new Detector.Processor() {
        	@Override 
        	public void release() { }

			@Override
			public void receiveDetections(Detector.Detections detections) {
				final SparseArray<TextBlock> textBlock = detections.getDetectedItems();
				if (textBlock.size() != 0) {

                    ocrInfo.post(new Runnable() {
                        // Use the post method of the TextView
                        public void run() {
                        	for(int i=0; i<textBlock.size(); i++){
                        		TextBlock item = textBlock.valueAt(i);
		                    	if (item != null && item.getValue() != null) {
		                    		ocrInfo.setText(textBlock.valueAt(i).getValue());
            					}
		                    }
                        }
                    });
                }
			}
        });

        faceDetector = new FaceDetector.Builder(this).build();
        faceDetector.setProcessor(new Detector.Processor() {
        	@Override 
        	public void release() { }

			@Override
			public void receiveDetections(Detector.Detections detections) {
				final SparseArray<Face> faces = detections.getDetectedItems();
				if (faces.size() != 0) {

                    faceInfo.post(new Runnable() {
                        // Use the post method of the TextView
                        public void run() {
                        	for(int i=0; i<faces.size(); i++){
                        		Face item = faces.valueAt(i);
		                    	if (item != null) {
		                    		faceInfo.setText(faces.valueAt(i).toString());
            					}
		                    }
                        }
                    });
                }
			}
        });
        MultiDetector multiDetector = new MultiDetector.Builder()
        	.add(barcodeDetector)
        	.add(textRecognizer)
        	.add(faceDetector)
        	.build();
        cameraSource = new CameraSource.Builder(this, multiDetector)
        		.setRequestedPreviewSize(640, 480)
        		.setAutoFocusEnabled(true)
        		.build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() { 
        	
        	@Override 
        	public void surfaceCreated(SurfaceHolder holder) {
        		try { 
        			cameraSource.start(cameraView.getHolder()); 
        		} catch (IOException ie) {
        			Log.e("CAMERA SOURCE", ie.getMessage());
        		}
        	}

        	@Override
        	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

        	@Override
        	public void surfaceDestroyed(SurfaceHolder holder) {
        		cameraSource.stop();
        	} 
        });
    }
}
