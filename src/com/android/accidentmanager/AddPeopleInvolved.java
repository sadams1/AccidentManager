package com.android.accidentmanager;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class AddPeopleInvolved extends Activity
{
	private static final int CAMERA_PIC_REQUEST = 1337;
	Uri uri;
	ContentValues values;
	String fileName = "new-photo.jpg";
	private Cursor mCursorImages;
    private EditText mFirstNameText;
    private EditText mLastNameText;
    private EditText mEmailText;
    private EditText mPhoneText;
    private EditText mTravelerText;
    private String traveler;
    private RadioButton radioButton2;
    private RadioButton radioButton1;
    
    
	private Button cancelButton;
	private boolean changesPending;
	private AlertDialog unsavedChangesDialog;
    
    private Long mRowId;
    private ContactsDbAdapter mDbHelper;
	
	

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addpeopleinvolved);
		
		mDbHelper = new ContactsDbAdapter(this);
        mDbHelper.open();
        
        fillGallery();

       
        //setContentView(R.layout.involved_info);
       // setTitle(R.string.edit_note);

   //     mTitleText = (EditText) findViewById(R.id.title);
   //     mBodyText = (EditText) findViewById(R.id.body);
        
        //Text Fields
        mFirstNameText = (EditText) findViewById(R.id.first);
        mLastNameText = (EditText) findViewById(R.id.last);
        mEmailText = (EditText) findViewById(R.id.email);
        mPhoneText = (EditText) findViewById(R.id.phone);
        
        //Radio buttons
		//final RadioButton radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
		//final RadioButton radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
		
		radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
		radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
		
		radioButton2.setOnClickListener(radio_listener);
		radioButton1.setOnClickListener(radio_listener);
		
        //Submit button
        Button confirmButton = (Button) findViewById(R.id.confirm);
        cancelButton = (Button)findViewById(R.id.cancel_button);
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancel();
			}
		});
		
		//put listeners on each field - for use with cancel  to see if fields have values
		mFirstNameText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				changesPending = true;
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) { }		
		});
		
		mLastNameText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				changesPending = true;
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) { }
		});
		
		mEmailText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				changesPending = true;
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) { }
		});
		mPhoneText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				changesPending = true;
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void afterTextChanged(Editable s) { }
		});	
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(ContactsDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(ContactsDbAdapter.KEY_ROWID)
									: null;
		}

		populateFields();
		

		  
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        
	}
        
        private void populateFields() {
            if (mRowId != null) {
                Cursor note = mDbHelper.fetchNote(mRowId);
                startManagingCursor(note);
       //         mTitleText.setText(note.getString(
       //                 note.getColumnIndexOrThrow(ContactsDbAdapter.KEY_TITLE)));
       //         mBodyText.setText(note.getString(
       //                 note.getColumnIndexOrThrow(ContactsDbAdapter.KEY_BODY)));
                mFirstNameText.setText(note.getString(
                       note.getColumnIndexOrThrow(ContactsDbAdapter.KEY_FIRST)));
                mLastNameText.setText(note.getString(
                        note.getColumnIndexOrThrow(ContactsDbAdapter.KEY_LAST)));
                mEmailText.setText(note.getString(
                        note.getColumnIndexOrThrow(ContactsDbAdapter.KEY_EMAIL)));
                mPhoneText.setText(note.getString(
                        note.getColumnIndexOrThrow(ContactsDbAdapter.KEY_PHONE)));

                String traveler1 = note.getString(note.getColumnIndexOrThrow(ContactsDbAdapter.KEY_TRAVELER));
         
                if(traveler1.equalsIgnoreCase("Driver"))radioButton2.setChecked(true);          
                if(traveler1.equalsIgnoreCase("Passenger"))radioButton1.setChecked(true);
            }
        }
        
    	protected void cancel() {
    		if (changesPending) {
    			unsavedChangesDialog = new AlertDialog.Builder(this)
    				.setTitle(R.string.unsaved_changes_title)
    				.setMessage(R.string.unsaved_changes_message)
    				.setPositiveButton(R.string.add_contact, new AlertDialog.OnClickListener() {
    					public void onClick(DialogInterface dialog, int which) {
    						//addTask();
    		                setResult(RESULT_OK);
    		                finish();
    					}
    				})
    				.setNeutralButton(R.string.discard, new AlertDialog.OnClickListener() {
    					public void onClick(DialogInterface dialog, int which) {
    						finish();
    					}
    				})
    				.setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
    					public void onClick(DialogInterface dialog, int which) {
    						unsavedChangesDialog.cancel();
    					}
    				})
    				.create();
    			unsavedChangesDialog.show();
    		} else {
    			finish();
    		}
    	}
        
        
        public OnClickListener radio_listener = new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                RadioButton rb = (RadioButton) v;
               // mTravelerText = rb.getText().toString();
                 traveler = rb.getText().toString();
                Toast.makeText(AddPeopleInvolved.this, rb.getText(), Toast.LENGTH_SHORT).show();
            }
        };

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            saveState();
            outState.putSerializable(ContactsDbAdapter.KEY_ROWID, mRowId);
        }

        @Override
        protected void onPause() {
            super.onPause();
            saveState();
        }

        @Override
        protected void onResume() {
            super.onResume();
            populateFields();
        }

        private void saveState() {
            //String title = mTitleText.getText().toString();
            //String body = mBodyText.getText().toString();
            String first = mFirstNameText.getText().toString();
            String last = mLastNameText.getText().toString();
            String email = mEmailText.getText().toString();
            String phone = mPhoneText.getText().toString();
            
            //String traveler = rb.getText().toString();

            if (mRowId == null) 
            {
            	try
            	{   		
            		long id = mDbHelper.createNote(first, last, email, phone, traveler);
            		Toast.makeText(AddPeopleInvolved.this, traveler, Toast.LENGTH_SHORT).show();
    		            if (id > 0) 
    		            {
    		                mRowId = id;
    		            }
    	        }
            	catch(SQLiteException ex)
            	{
    	        	Log.v("Insert into database exception caught",ex.getMessage());
    	        }	
    	    }
            else 
            {
            	try
            	{
            		Toast.makeText(AddPeopleInvolved.this, traveler, Toast.LENGTH_SHORT).show();
            		mDbHelper.updateNote(mRowId, first, last, email, phone, traveler);
            	}
            	catch(SQLiteException ex)
            	{
            		Log.v("Update database exception caught", ex.getMessage());
            	}
            }
        }
		
	

private void fillGallery() 
	{
		
		// An array specifying which columns to return.
		String[] projection = new String[] { Media._ID, Media.TITLE }; // we aren't actually using the title...

		// The base URI for SD Card content
		Uri mMedia = Media.EXTERNAL_CONTENT_URI;

		// GET ALL IMAGE MEDIA ON THE SD CARD (Content Provider Query)
		// Best way to retrieve a query; returns a managed cursor, etc.
		mCursorImages = managedQuery(mMedia, projection, 				
				null, null, 
				Media.DATE_TAKEN + " ASC"); // Order-by clause.

		// Fill our custom ImageUriAdapter from the Cursor
		ImageUriAdapter iAdapter = new ImageUriAdapter(this, mCursorImages, mMedia); // We'll limit to just SD card images
		
		// Assign the adapter to our Gallery to display the images
		final Gallery pictureGal = (Gallery) findViewById(R.id.GalleryOfPics);
		pictureGal.setAdapter(iAdapter);
	}

	public class ImageUriAdapter extends BaseAdapter 
	{
		int mGalleryItemBackground;
		private GalleryRecord[] mPics;
		private Context mContext;

		public ImageUriAdapter(Context c, Cursor curs, Uri baseUri) 
		{
			mContext = c;
	
			int iNumPics = curs.getCount();
			mPics = new GalleryRecord[iNumPics];
	
			curs.moveToFirst();
			for (int i = 0; i < iNumPics; i++) {				
				final String strUri = baseUri.toString();
				final long iId = curs.getLong(curs.getColumnIndex(Media._ID));	
				mPics[i] = new GalleryRecord(strUri, iId);
				curs.moveToNext();
			}
		
			TypedArray a = obtainStyledAttributes(R.styleable.default_gallery);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.default_gallery_android_galleryItemBackground, 0);
			a.recycle();
		}

		public int getCount() {
			return mPics.length;
		}

		public Object getItem(int position) {
			return mPics[position].getImageId(); 
		}

		public long getItemId(int position) {
			return mPics[position].getImageId();
		}

		// Returns the View corresponding to each Gallery child 
		// Each Gallery item is simply an ImageView
		// Tip: We set the Tag property of the ImageView to the GalleryRecord object to store the metadata
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
	
		    Uri baseUri = Uri.parse(mPics[position].getImageUriPath());
			Uri myImageUri = ContentUris.withAppendedId(
					baseUri, mPics[position].getImageId());
			i.setImageURI(myImageUri);
			
			// Very useful trick, setting the Tag to store our metadata about this image for later use
			i.setTag(mPics[position]);
			
			// Constrain the images so they all look the same size/ratio
			i.setMaxHeight(50);
			i.setMaxWidth(50);
			i.setAdjustViewBounds(true);
	
			// The preferred Gallery item background
			i.setBackgroundResource(mGalleryItemBackground);
	
			return i;	
		}
	}
	
}
