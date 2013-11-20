package com.example.activecolor;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
//	Camera _camera = getCameraInstance();

	private Activity _activity;
    private SurfaceHolder mHolder;
    private static Camera mCamera; 
    private static boolean firstPass = true;

    public CameraPreview(Context context, Camera camera, Activity activity) {
        super(context);
        mCamera = camera;
        _activity = activity;
        firstPass = true;
        
        
        System.out.println("Camera Hardware check: " + checkCameraHardware(context));
        System.out.println("Camera check: " + mCamera == null ? false : true);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    public int getCameraID(){
    	return 0;
    }
    
    public Camera getCurrentCamera(){
    	return mCamera;
    }


    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        try{
        camera.setDisplayOrientation(result);
        }catch(Exception ex){
        	System.out.println("NO Camera detected!  Try Restarting device if device has camera");
        }
    }

   
    

	
	
	
	
	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
//	/** A safe way to get an instance of the Camera object. */
//	public  void getCameraInstance(){
//		new Thread(new Runnable() {
//		        public void run() {
//		            Camera c = null;
//		    	    try {
//		    	    	mCamera = Camera.open(); // attempt to get a Camera instance
//		    	    }
//		    	    catch (Exception e){
//		    	        // Camera is not available (in use or does not exist)
//		    	    }
//		    	    if(c == null){
//		    	    	 mCamera = Camera.open(0);
//		    	    } 
//		        }
//		    }).start();
//	}

//	
//	public static void releaseCamera(){
//		System.out.println("Closing camera");
//		mCamera.release();
//		
//	}

	@Override
	 public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }
		if(firstPass){
		CameraPreview.setCameraDisplayOrientation(_activity, getCameraID(), getCurrentCamera());
		firstPass = false;
		}
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
//            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

	@Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
//            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }catch(NullPointerException e){
        	
        }catch(RuntimeException e){
        	System.out.println("Crap camera not ready");
        }
    }

	@Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
//		releaseCamera();
    }
}
