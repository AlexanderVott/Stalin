package com.FouregoStudio.Stalin;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.StaticLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ReaderDisplay extends Activity {

	TextView textView;
	ScrollView scrollView;
	
	float oldX = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		// меняем тему в зависимости от настроек
		if (settings.getBoolean("dark_theme", false)) {
			setTheme(R.style.Theme_EOT_Black_NoTitleBar_FullScreen);
			setContentView(R.layout.reader_dark);
		} else {
			setTheme(R.style.Theme_EOT_Light_NoTitleBar_FullScreen);
			setContentView(R.layout.reader);
		}
		
		// если наличествует imageView_BG - обрабатываем настройки (в тёмной версии layout нет такого объекта)
        if (findViewById(R.id.imageView_BG) != null)
	        if (settings.getBoolean("show_bg", true))
				((ImageView) findViewById(R.id.imageView_BG)).setVisibility(View.VISIBLE);
			else
				((ImageView) findViewById(R.id.imageView_BG)).setVisibility(View.INVISIBLE);
		
    	AssetManager am = this.getAssets();
    	
    	textView = (TextView) findViewById(R.id.textView2);
    	
    	try {
			if (getIntent().getExtras().size() > 0) {
    			String groupId = Integer.toString(getIntent().getExtras().getInt("groupId"));
    			String childId = Integer.toString(getIntent().getExtras().getInt("childId"));
    			textView.setText(Html.fromHtml(Utils.readTextFile(this, am.open("stalin_sobr/" + groupId + "/" + groupId + "-" + childId + ".htm"), true)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		((TextView) findViewById(R.id.textView_Vol_Title)).setText(getIntent().getExtras().getString("groupText"));
		
		scrollView = (ScrollView) findViewById(R.id.scrollView1);
		// перематываем на последнюю сохранённую при закрытии
		if (getIntent().getExtras().getInt("scroll") > -1) 
			scrollView.post(new Runnable() {
			    public void run() {
			    	scrollView.scrollTo(0, getIntent().getExtras().getInt("scroll"));
			    } 
			});
		else {
			saveScrollPos();
		}
				
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveScrollPos();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = null;
    	switch (item.getItemId()) {
    	case R.id.item1:
    		Toast.makeText(this, "settings", Toast.LENGTH_SHORT);
    		intent = new Intent(this, Preferences.class);
    		startActivityForResult(intent, ListOfCollectionsActivity.RES_PREF);
    		break;
    	case R.id.item2:
    		intent = new Intent(this, About.class);
    		startActivity(intent);
    		break;
    	}
    	return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
	    	switch (requestCode) {
		    	case ListOfCollectionsActivity.RES_READER: {
		    		break;
		    	}
		    	case ListOfCollectionsActivity.RES_PREF: {
	    		
		    		Intent intent = new Intent();
		        	intent.putExtra("scroll", scrollView.getScrollY());
		        	intent.putExtra("restart", true);
		        	setResult(RESULT_OK, intent);
		        	
		    		finish();
		    		break;
		    	}
	    	}
    	} else {
    		Toast.makeText(this, R.string.toast_dont_save_scroll, Toast.LENGTH_LONG).show();
    	}
    }
       
    private void saveScrollPos() {
    	Intent intent = new Intent();
    	intent.putExtra("scroll", scrollView.getScrollY());
    	setResult(RESULT_OK, intent);
    }
}
