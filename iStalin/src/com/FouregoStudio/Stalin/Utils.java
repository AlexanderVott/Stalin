package com.FouregoStudio.Stalin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.Display;

public class Utils {
	private Utils() {
	}
	
	public static Bitmap loadBitmapFromFile1(String filename) throws IOException {
		Bitmap result = null;
		
		FileInputStream fInput = null;
		BufferedInputStream bufInput = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options(); 
            options.inJustDecodeBounds = true; 
            options = new BitmapFactory.Options(); 
            options.inJustDecodeBounds = false; 
			
			fInput = new FileInputStream(filename);
			bufInput = new BufferedInputStream(fInput);
			result = BitmapFactory.decodeStream(fInput, null, options);
			try {
				//result = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/mobile_designer/modules/modul00.png");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i("test", filename);
		} finally {
			if (fInput != null) {
				fInput.close();
			}
			if (bufInput != null) {
				bufInput.close();
			}
		}
		return result;
	}
	
	public static List<File> getBitmapFileList(Context context, String Path) throws IOException {
		List<File> result = new ArrayList<File>();
		
		File dir = new File(Path);
		if (!dir.exists()) {
			throw new IOException(String.format("Path is not exists (%s)", Path));
		}
		if (!dir.canRead()) {
			throw new IOException(String.format("Directory don't can read (%s)", Path));
		}
		
		File[] files = dir.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String filename) {
				// compressFormat
				return filename.matches(".+\\.(PNG|png|JPEG|jpeg|JPG|jpg)$");
			}
		});
		if (files != null && files.length > 0) {
			for (File item: files) {
				result.add(item);
			}
		}
		return result;
	}
	
	public static Boolean CopyFromAsset(Context context, String AssetName, String finalFile) {
		try {
			InputStream input = context.getAssets().open(AssetName);
			OutputStream output = new FileOutputStream(finalFile);
			
			byte[] buffer = new byte[1024];
			int length;
			while ( (length = input.read(buffer)) > 0 ) {
				output.write(buffer, 0, length);
			}
			
			output.flush();
			output.close();
			input.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static Boolean copyFile(InputStream in, OutputStream out) {
		try {
			byte[] buffer = new byte[1024];
			int length;
			while ( (length = in.read(buffer)) > 0 ) {
				out.write(buffer, 0, length);
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static Boolean CopyFromAssets(Context context, String finishPath) {
		try {
			AssetManager aManager = context.getAssets();
			String[] files = null;
			try {
				files = aManager.list("");
			} catch (IOException e) {
				Log.e("dev", e.getMessage());
				e.printStackTrace();
			}
			for (String filename : files) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = aManager.open(filename);
					out = new FileOutputStream(finishPath + filename);
					
					copyFile(in, out);
					
					in.close();
					out.flush();
					out.close();
				} catch (IOException e) {
					Log.e("dev", e.getMessage());
					e.printStackTrace();
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static Boolean CopyFile(String startFile, String finishFile) {
		try {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new FileInputStream(startFile);
				out = new FileOutputStream(finishFile);
					
				copyFile(in, out);
					
				in.close();
				out.flush();
				out.close();
			} catch (IOException e) {
				Log.e("dev", e.getMessage());
				e.printStackTrace();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static Bitmap readBitmap(Context context, Uri selectedImage) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2; //reduce quality 
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(selectedImage, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally{
            try {
                bm = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                fileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }
	
	public static String readTextFile(Context context, String path) {
		File file = new File(path);
		StringBuilder text = new StringBuilder();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				text.append(line);
			}
		} catch (IOException e) {
			Log.e("dev", "Failed Utils.readTextFile(Context context, String path): " + path);
			return "";
		}
		return text.toString();
	}
	
	public static String readTextFile(Context context, InputStream is) throws IOException {
		int BUFFER_SIZE = 8192;
		InputStreamReader inputReader = new InputStreamReader(is);
		StringBuilder text = new StringBuilder();
		
		try {
			BufferedReader br = new BufferedReader(inputReader, BUFFER_SIZE);
			String line;
			while ((line = br.readLine()) != null) {
				text.append(line);
			}
		} catch (IOException e) {
			Log.e("dev", "Failed Utils.readTextFile(Context context, String path): " + is.toString());
			return "";
		}
		
		return text.toString();
	}
	
	public static String readTextFile(Context context, InputStream is, boolean toUTF) throws IOException {
		String UTF8 = "UTF-8";
		String WIN1251 = "windows-1251";
		int BUFFER_SIZE = 8192;
		InputStreamReader inputReader = null;
		if (toUTF)
			inputReader = new InputStreamReader(is, WIN1251);
		else
			inputReader = new InputStreamReader(is);
		StringBuilder text = new StringBuilder();
		
		try {
			BufferedReader br = new BufferedReader(inputReader, BUFFER_SIZE);
			String line;
			while ((line = br.readLine()) != null) {
				text.append(line);
			}
		} catch (IOException e) {
			Log.e("dev", "Failed Utils.readTextFile(Context context, String path): " + is.toString());
			return "";
		}
	
		return text.toString();
	}
	
	public static int getCountLinesOfTextFile(Context context, String path) {
		File file = new File(path);
		int counter = 0;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				counter++;
			}
			br.close();
		} catch (IOException e) {
			Log.e("dev", "Failed Utils.readTextFile(Context context, String path): " + path);
			return 0;
		}
		return counter; 
	}
	
//	public static int getScrOrientation(Context context)
//	{
//		Display getOrient = getWindowManager().getDefaultDisplay();
//	
//		int orientation = getOrient.getOrientation();
//	
//		// Sometimes you may get undefined orientation Value is 0
//		// simple logic solves the problem compare the screen
//		// X,Y Co-ordinates and determine the Orientation in such cases
//		if(orientation == Configuration.ORIENTATION_UNDEFINED) {
//	
//			Configuration config = getResources().getConfiguration();
//			orientation = config.orientation;
//		
//			if(orientation == Configuration.ORIENTATION_UNDEFINED) {
//				//if height and widht of screen are equal then
//				// it is square orientation
//				if(getOrient.getWidth() == getOrient.getHeight()) {
//					orientation = Configuration.ORIENTATION_SQUARE;
//				}else{ //if widht is less than height than it is portrait
//					if(getOrient.getWidth() < getOrient.getHeight()){
//						orientation = Configuration.ORIENTATION_PORTRAIT;
//					}else{ // if it is not any of the above it will defineitly be landscape
//						orientation = Configuration.ORIENTATION_LANDSCAPE;
//					}
//				}
//			}
//		}
//		return orientation; // return value 1 is portrait and 2 is Landscape Mode
//	}
}
