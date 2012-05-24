package com.FouregoStudio.Stalin;

import org.json.JSONException;

import com.FouregoStudio.Stalin.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.SimpleExpandableListAdapter;

public class ListOfCollectionsActivity extends Activity {
	
	public static final int RES_READER = 1;
	public static final int RES_PREF = 2;
	public static final String APP_PACK = "com.FouregoStudio.Stalin";
	
	ExpListAdapterHelper adapterHelper;
	SimpleExpandableListAdapter adapter;
	
	ExpandableListView list;
	
	SharedPreferences settings;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        if (settings.getBoolean("dark_theme", false)) {
			this.setTheme(R.style.Theme_EOT_Black_NoTitleBar_FullScreen);
			setContentView(R.layout.list_dark);
        } else {
			this.setTheme(R.style.Theme_EOT_Light_NoTitleBar_FullScreen);
			setContentView(R.layout.list);
        } 
        
        // если наличествует imageView_BG - обрабатываем настройки (в тёмной версии layout нет такого объекта)
        if (findViewById(R.id.imageView_BG) != null)
	        if (settings.getBoolean("show_bg", true))
				((ImageView) findViewById(R.id.imageView_BG)).setVisibility(View.VISIBLE);
			else
				((ImageView) findViewById(R.id.imageView_BG)).setVisibility(View.INVISIBLE);
               
        AssetManager am = this.getAssets();
        
        adapterHelper = new  ExpListAdapterHelper();
        adapter = adapterHelper.getAdapter(this, am);
        
        (list = ((ExpandableListView) findViewById(R.id.expandableListView1))).setAdapter(adapter);
        list.setOnGroupClickListener(new OnGroupClickListener() {
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return false;
			}
		});
        
        list.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				SharedPreferences.Editor editor = settings.edit();
				// нажали на элемент
				int groupId = 0;
				try {
					groupId = adapterHelper.getGroupNum(adapterHelper.getGroupText(groupPosition));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				int childId = 0;
				try {
					childId = adapterHelper.getChildNum(groupId, adapterHelper.getChildText(groupPosition, childPosition));
				} catch (JSONException e) {
					e.printStackTrace();
				}				
		        if ((groupId > -1) && (childId > -1)) {
					editor.putInt("childId", childId);
					editor.putInt("groupId", groupId);
					editor.commit();
					StartIntentReader(groupId, childId, 0, adapterHelper.getGroupText(groupId - 1));
				}
				return false;
			}
		});
        
        list.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			public void onGroupCollapse(int groupPosition) {
			}
		});
        
        list.setOnGroupExpandListener(new OnGroupExpandListener() {
			public void onGroupExpand(int groupPosition) {
			}
		});
        
        if ( ((settings.getBoolean("open_vol", false)) && (settings.getInt("childId", -1) != -1)) || (settings.getBoolean("restore", false)) ) {
        	// сбрасываем "восстановление"
        	SharedPreferences.Editor editor = settings.edit();
    		editor.putBoolean("restore", false);
    		editor.commit();
    		
        	StartIntentReader(settings.getInt("groupId", 0), settings.getInt("childId", 0), settings.getInt("scroll", 0), adapterHelper.getGroupText(settings.getInt("groupId", 1) - 1));
        }
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
    		startActivityForResult(intent, RES_PREF);
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
		    	case RES_READER: {
		    		// сохраняем позицию прокрутки
		    		SharedPreferences.Editor editor = settings.edit();
		    		// сохраняем позицию скролла
		    		editor.putInt("scroll", data.getExtras().getInt("scroll"));
		    		
		    		if (data.getExtras().getBoolean("restart")) {
			    		// выключаем splash, указываем последующий возврат параметра в исходное состояние
			    		editor.putBoolean("show_splash", false);
			    		if (!settings.getBoolean("show_splash", false)) {
			    			editor.putBoolean("one_hide_splash", true);
			    		}
			    		// параметр восстановления открытой главы
			    		editor.putBoolean("restore", true);
			    		
			    		editor.commit();
			    		
			    		// перезапускаем приложение
			    		Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
			    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			    		startActivity(intent);
			    		// закрываем уже устаревшее приложение, т.к. запущено новое
			    		System.exit(0);
		    		} else 
		    			editor.commit();
		    		break;
		    	}
		    	case RES_PREF: {
		    		finish();
		    		Intent intent = new Intent(getApplicationContext(), ListOfCollectionsActivity.class);
		    		startActivity(intent);
		    		break;
		    	}
	    	}
    	} else {
    		Toast.makeText(this, R.string.toast_dont_save_scroll, Toast.LENGTH_LONG).show();
    	}
    }
    
    private void StartIntentReader(int groupId, int childId, int scroll, String groupText) {
		Intent intent = new Intent(getApplicationContext(), ReaderDisplay.class);
		intent.putExtra("groupId", groupId);
		intent.putExtra("childId", childId);
		intent.putExtra("scroll", scroll);
		intent.putExtra("groupText", groupText);
		startActivityForResult(intent, RES_READER);
    }
}