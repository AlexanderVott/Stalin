package com.FouregoStudio.Stalin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.SimpleExpandableListAdapter;

public class ExpListAdapterHelper {
	
	final String ATTR_VOLUME_NAME = "volume";
	final String ATTR_CHAPTER_NAME = "chapter";
	
	SimpleExpandableListAdapter simpleAdapter;
	
	ArrayList<Map<String, String>> groupData;
	ArrayList<ArrayList<Map<String, String>>> childData;
	
	Map<String, String> m;
	Map<String, String> h;
	
	ArrayList<Map<String, String>> childDataItem;
	
	JSONObject jsonObj;
	
	public SimpleExpandableListAdapter getAdapter(Context context, AssetManager am) {
		
		JSONArray json = null;
        try {
        	InputStream is = am.open("list.json");
        	json = new JSONArray(Utils.readTextFile(context, is));
        	is.close();
        } catch (JSONException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        groupData = new ArrayList<Map<String, String>>();
        childData = new ArrayList<ArrayList<Map<String, String>>>(); 
              
        jsonObj = null;
        try {
        	jsonObj = json.getJSONObject(0);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
        
		if (jsonObj != null) {
	        for (int i = 1; i < jsonObj.length() + 1; i++) {
	        	m = new HashMap<String, String>();
	        	JSONObject vol = null;
	        	try {
					vol = jsonObj.getJSONObject("vol" + (i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (vol != null) {
					try {
						m.put(ATTR_VOLUME_NAME, i + ". " + vol.getString("name"));
						
						childDataItem = new ArrayList<Map<String, String>>();
						for (int j = 1; j < vol.length() + 1; j++) {
							if (!vol.isNull(Integer.toString(j))) {
								h = new HashMap<String, String>();
								h.put(ATTR_CHAPTER_NAME, vol.getString(Integer.toString(j)));
								childDataItem.add(h);
							}//if
				 		}//for
						childData.add(childDataItem);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//try
				}//if
		        groupData.add(m);
	        }//for
		}//if
        simpleAdapter = new SimpleExpandableListAdapter(context, 
        											groupData, 
        											android.R.layout.simple_expandable_list_item_1, 
        											new String[] {ATTR_VOLUME_NAME}, 
        											new int[] {android.R.id.text1}, 
        											childData, 
        											android.R.layout.simple_list_item_1, 
        											new String[] {ATTR_CHAPTER_NAME}, 
        											new int[] {android.R.id.text1});
		
		return simpleAdapter;
	}
	
	@SuppressWarnings("unchecked")
	String getGroupText(int groupPos) {
		Map<String, String> map = (Map<String, String>)(simpleAdapter.getGroup(groupPos));
		String groupText = map.get(ATTR_VOLUME_NAME);
		return groupText.substring(groupText.lastIndexOf((groupPos + 1) + ". ", 0) + ((groupPos + 1) + ". ").length(), groupText.length()).trim();
	}
	
	@SuppressWarnings("unchecked")
	String getChildText(int groupPos, int childPos) {
		Map<String, String> map = (Map<String, String>)(simpleAdapter.getChild(groupPos, childPos));
		//String groupText = map.get(ATTR_CHAPTER_NAME);
		//return groupText.substring(groupText.lastIndexOf(".", 1) + 2, groupText.length());
		return map.get(ATTR_CHAPTER_NAME);
	}
	
	String getCompoundText(int groupPos, int childPos) {
		return getGroupText(groupPos) + " " + getChildText(groupPos, childPos);
	}
	
	int getGroupNum(String groupText) throws JSONException {
		// �������� �����, ����� � ������ (+2)
		if (jsonObj != null) {
	        for (int i = 1; i < jsonObj.length() + 1; i++) {
	        	JSONObject vol = null;
	        	try {
					vol = jsonObj.getJSONObject("vol" + i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if ((vol != null) && (vol.isNull("name") == false) && (vol.getString("name").compareToIgnoreCase(groupText) == 0)) {
					return i;
				}//if
	        }//for
		}//if
		
		return -1;
	}
	
	int getChildNum(String groupText, String childText) throws JSONException {
		int groupId = getGroupNum(groupText);
		
		if (groupId > -1) {
			JSONObject vol = jsonObj.getJSONObject("vol" + (groupId));
			if (vol != null) {
				for (int i = 1; i < vol.length() + 1; i++) {
					if ((vol.isNull(Integer.toString(i)) == false) && (vol.getString(Integer.toString(i)).compareToIgnoreCase(childText) == 0)) {
						return i;
					}//if
				}//for
			}//if
		}//if
		
		return -1;
	}
	
	int getChildNum(int groupId, String childText) throws JSONException {	
		if (groupId > -1) {
			JSONObject vol = jsonObj.getJSONObject("vol" + (groupId));
			if (vol != null) {
				for (int i = 1; i < vol.length() + 1; i++) {
					if ((vol.isNull(Integer.toString(i)) == false) && (vol.getString(Integer.toString(i)).compareToIgnoreCase(childText) == 0)) {
						return i;
					}//if
				}//for
			}//if
		}//if
		
		return -1;
	} 

}
