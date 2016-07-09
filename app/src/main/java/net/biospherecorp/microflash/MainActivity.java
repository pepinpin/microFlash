package net.biospherecorp.microflash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import net.biospherecorp.microflash.mCamera.FlashLight;

public class MainActivity extends AppCompatActivity {

	private ImageButton _switchButton;
	private RelativeLayout _bkg;

	private mCamera _camera;
	private FlashLight _flashLight;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		_switchButton = (ImageButton) findViewById(R.id.btnSwitch);
		_bkg = (RelativeLayout) findViewById(R.id.background);

		// All the code related to the camera / flashlight has been deported
		// in another class for easier maintenance / better upgrade
		_camera = new mCamera(this);
		_flashLight = _camera.new FlashLight();

		_switchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (_camera.isFlashOn){
					_flashLight.lightOff();
				}else{
					_flashLight.lightOn();
				}
			}
		});

		// turns the screen brightness to the max
		// to allow the use of the screen as a small light torch
		_lightScreenOn();
	}


	// turn the screen brightness up to 100%
	private void _lightScreenOn(){
		WindowManager.LayoutParams layout = getWindow().getAttributes();
		layout.screenBrightness = 1F;
		getWindow().setAttributes(layout);
	}

	// dim the screen brightness down to 20%
	private void _lightScreenOff(){
		WindowManager.LayoutParams layout = getWindow().getAttributes();
		layout.screenBrightness = 0.2F;
		getWindow().setAttributes(layout);
	}

	// deals with the button state change
	void toggleSwitchButton(){

		// if the light torch has been turned on
		if (_camera.isFlashOn){
			// change the button image
			_switchButton.setImageResource(R.drawable.btn_switch_on);
			// turn the screen background to black
			_bkg.setBackgroundColor(getResources().getColor(R.color.colorBlack));
			// dim the screen down
			_lightScreenOff();
		}else{
			// change the button image
			_switchButton.setImageResource(R.drawable.btn_switch_off);
			// turn the screen background to white
			_bkg.setBackgroundColor(getResources().getColor(R.color.colorWhite));
			// turn the screen brightness up
			_lightScreenOn();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		_camera.initCamera();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (_camera.hasFlash){
			_flashLight.lightOff();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (_camera.hasFlash){
			_flashLight.lightOff();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		_camera.releaseCamera();

	}
}
