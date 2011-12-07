package com.android.accidentmanager;



import android.app.Activity;
import android.os.Bundle;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AccidentManagerMain extends Activity 
{
	private static final int CAMERA_PIC_REQUEST = 1337;
	Uri uri;
	ContentValues values;
	String fileName = "new-photo.jpg";
	private Cursor mCursorImages;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
    }
    
    public void onFlashlightClick(View v) 
    {
    	try 
    	{
			DroidLED dl = new DroidLED();
			dl.enable(!dl.isEnabled());
		} 
    	catch (Exception e) 
    	{
			Log.e("SimpleLED", e.getMessage());
		}
    }
    public void onCameraButtonClick(View v)
    {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		uri = cameraIntent.getData();
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST); 
		//Toast.makeText(this, (CharSequence) uri, Toast.LENGTH_SHORT).show();
    }
    public void onAddNewClick(View v)
    {
    	
    	Intent addNewDashboard = new Intent(AccidentManagerMain.this,AddNewDashboard.class);
    	startActivity(addNewDashboard);
    }
}