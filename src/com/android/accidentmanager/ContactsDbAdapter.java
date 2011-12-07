package com.android.accidentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class ContactsDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";

    public static final String KEY_ROWID = "_id";
    
    //ny adds
    public static final String KEY_FIRST = "first";
    public static final String KEY_LAST = "last";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_DRIVER = "driver";
    public static final String KEY_PASSENGER = "passenger";
    public static final String KEY_TRAVELER = "traveler";
    

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    //private static final String DATABASE_CREATE =
    //    "create table notes (_id integer primary key autoincrement, "
    //    + "title text not null, body text not null, first text not null, last text not null, email text not null,phone text not null);";

    private static final String DATABASE_CREATE =
        "create table notes (_id integer primary key autoincrement, "
        + "first text not null, last text not null, email text not null,phone text not null,traveler text not null);";
    
    private static final String CREATE_VEHICLE_TABLE = "create table " +
    Constants.DATABASE_TABLE_VEHICLES+" ("+
    Constants.KEY_ROWID_VEHICLE+" integer primary key autoincrement, "+
    Constants.KEY_MAKE+" text not null, "+
    Constants.KEY_MODEL+" text not null, "+
    Constants.KEY_LICENSE+" text not null, "+
    Constants.KEY_INSURANCE_CARRIER+" text not null, "+
    Constants.KEY_INSURANCE_POLICY_NUMBER+" text not null);";
    
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 5;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	try	{
        		Log.v("MyDBHelper onCreate","Creating all the tables");
        		db.execSQL(DATABASE_CREATE);
        		db.execSQL(CREATE_VEHICLE_TABLE);
        	} catch(SQLiteException ex){
        		Log.v("Create table exception",ex.getMessage());
        	}
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //TODO update to just add new columns instead of dropping table and recreating
        	Log.w(TAG, "Upgrading database from version " + oldVersion 
            			+ " to " + newVersion 
            			+ ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public ContactsDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ContactsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @param first the first name of the contact
     * @param last the last name of the contact
     * @param email the email of the contact
     * @param phone the phone number of the contact
     * @param traveler the passenger or driver
     * @return rowId or -1 if failed
     */
   // public long createNote(String title, String body, String first, String last, String email, String phone) 
    public long createNote(String first, String last, String email, String phone, String traveler)
    {
    	try
    	{
    		ContentValues initialValues = new ContentValues();
	       // initialValues.put(KEY_TITLE, title);
	       // initialValues.put(KEY_BODY, body);
	        //my add
	        initialValues.put(KEY_FIRST, first);
	        initialValues.put(KEY_LAST, last);
	        initialValues.put(KEY_EMAIL, email);
	        initialValues.put(KEY_PHONE, phone);
	        initialValues.put(KEY_TRAVELER, traveler);
	 
	        return mDb.insert(DATABASE_TABLE, null, initialValues);
    	}
    	catch(SQLiteException ex)
    	{
    		Log.v("Insert into database exception caught",ex.getMessage());
    		return -1;
    	}
    	
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_FIRST,
                KEY_LAST,KEY_EMAIL,KEY_PHONE,KEY_TRAVELER}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

          //  mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
           //         KEY_TITLE, KEY_BODY,KEY_FIRST}, KEY_ROWID + "=" + rowId, null,
            //        null, null, null, null);
        
        mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                KEY_FIRST, KEY_LAST, KEY_PHONE, KEY_EMAIL, KEY_TRAVELER}, KEY_ROWID + "=" + rowId, null,
                null, null, null, null);
        
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
   // public boolean updateNote(long rowId, String title, String body, String first) 
    public boolean updateNote(long rowId, String first, String last, String email, String phone, String traveler) 
    {
        ContentValues args = new ContentValues();
        //args.put(KEY_TITLE, title);
        //args.put(KEY_BODY, body);
        args.put(KEY_FIRST, first);
        args.put(KEY_LAST, last);
        args.put(KEY_EMAIL, email);
        args.put(KEY_PHONE, phone);
        args.put(KEY_TRAVELER, traveler);
     
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
