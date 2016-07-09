package net.biospherecorp.microflash;


import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.support.v7.app.AlertDialog;
import android.util.Log;


class mCamera {

	private MainActivity _main;

	// deprecated but still the best way
	// to get the camera as Camera2 is only available
	// from API 21
	private Camera _camera;
	private Parameters _parameters;

	boolean hasFlash = false;
	boolean isFlashOn = false;

	private static final String TAG = "mCamera";

	mCamera(MainActivity activity){
		_main = activity;
	}


	// checks to make sure the device has a flash,
	// displays an AlertDialog and quit the app if not
	private void _checkIfCameraHasFlash(){
		hasFlash = _main.getApplicationContext()
				.getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		if(!hasFlash){
			AlertDialog alertDialog = new AlertDialog.Builder(_main).create();
			alertDialog.setTitle("Error");
			alertDialog.setMessage("Your device doesn't support FlashLight");
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Dismiss", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					// close the app gracefully
					_main.finish();
				}
			});

			alertDialog.show();
		}
	}

	// get the camera device and the parameters
	private void _getCamera(){
		if (_camera == null){
			try {
				_camera = Camera.open();
			}catch (RuntimeException e){
				e.printStackTrace();
				Log.e(TAG, "Camera Error : Couldn't get the camera, it may be used by another app !");
				Log.e(TAG, "Camera Error : " + e.getMessage());
			}
		}
	}

	// checks for flash and gets the camera device
	void initCamera(){
		_checkIfCameraHasFlash();
		_getCamera();
	}

	// releases the camera and resets the
	// camera & parameters fields to null
	void releaseCamera(){
		if (_camera != null){
			_camera.release();
			_camera = null;
			_parameters = null;
		}
	}

	// inner class representing just the light torch
	class FlashLight{

		// turn the light ON
		void lightOn(){

			// if light is off and there is a camera object
			if(!isFlashOn && _camera != null){

				_parameters = _camera.getParameters();
				_parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);

				_camera.setParameters(_parameters);
				_camera.startPreview();

				isFlashOn = true;

				// change button state/img
				_main.toggleSwitchButton();
			}
		}


		// turn the light OFF
		void lightOff(){

			// if light is on and there is a camera object
			if (isFlashOn && _camera != null){

				_parameters = _camera.getParameters();
				_parameters.setFlashMode(Parameters.FLASH_MODE_OFF);

				_camera.setParameters(_parameters);
				_camera.stopPreview();

				isFlashOn = false;

				// change button state/img
				_main.toggleSwitchButton();
			}
		}
	}

}
