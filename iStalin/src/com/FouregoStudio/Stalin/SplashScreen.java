package com.FouregoStudio.Stalin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

public class SplashScreen extends Activity {
	private Thread cSplashThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
			
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        if (settings.getBoolean("show_splash", true)) {
        	
			cSplashThread = new Thread() {
				@Override
				public void run() {
					try {
						synchronized(this) {
							wait(5000);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
									
					startMainActivity();
					//stop();
				}
			};
			
			cSplashThread.start();
        } else {
        	// ���� �������� ���� ���� ���, �� �������� splash ��� ���������� �������
        	if (settings.getBoolean("one_hide_splash", true)) {
        		SharedPreferences.Editor editor = settings.edit();
	    		editor.putBoolean("show_splash", true);
	    		editor.commit();
        	}
        	startMainActivity();
        }
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (cSplashThread) {
				cSplashThread.notifyAll();				
			}
		}
		return true;
	}
	
	private void startMainActivity() {
		finish();
		Intent intent = new Intent();
		intent.setClass(this, ListOfCollectionsActivity.class);
		startActivity(intent);
	}

}
