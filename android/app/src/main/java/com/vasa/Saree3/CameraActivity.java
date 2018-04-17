package com.vasa.Saree3;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;

    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private TextView ocrInfo;

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
		                    		Log.d("OcrDetectorProcessor", "Text detected! " + item.getValue());
		                    		ocrInfo.setText(textBlock.valueAt(i).getValue());
            					}
		                    }
                        }
                    });
                }
			}
        });

        faceDetector = new FaceDetector.Builder(this).build();

        MultiDetector multiDetector = new MultiDetector.Builder()
        	.add(barcodeDetector)
        	.add(textRecognizer)
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
