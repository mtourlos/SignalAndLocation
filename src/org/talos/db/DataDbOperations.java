package org.talos.db;

import org.talos.beans.DataBean;
import org.talos.db.DataContract.DataEntry;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataDbOperations {
	DataDbHelper mDbHelper;
	Cursor cursor;
	
	
	
	public DataDbOperations(final Context context) {
		mDbHelper = new DataDbHelper(context.getApplicationContext());
	}

	public void initRead(){
    	SQLiteDatabase db = mDbHelper.getReadableDatabase();
    	String[] projection = {
    		    DataEntry.TIME_STAMP,
    		    DataEntry.USER,
    		    DataEntry.OPERATOR,
    		    DataEntry.CINR,
    		    DataEntry.LATITUDE,
    		    DataEntry.LONGTITUDE
    		    };
    	String sortOrder =
    		    DataEntry.TIME_STAMP + " DESC";
    	
    	cursor = db.query(
    		    DataEntry.TABLE_NAME,  // The table to query
    		    projection,                               // The columns to return
    		    null,                                // The columns for the WHERE clause
    		    null,                            // The values for the WHERE clause
    		    null,                                     // don't group the rows
    		    null,                                     // don't filter by row groups
    		    sortOrder                                 // The sort order
    		    );
	}
	
	public DataBean getData(){
		DataBean result = new DataBean();
    	result.setTimeStamp(cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.TIME_STAMP)));
    	result.setUser(cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.USER)));
    	result.setOperator(cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.OPERATOR)));
    	result.setCinr(cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.CINR)));
    	result.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.CINR)));
    	result.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow(DataEntry.CINR)));
    	
    	return result;
    }
	
	public boolean moveCursorToFirst(){
		return cursor.moveToFirst();
	}
	
	public boolean moveCursorNext(){
		return cursor.moveToNext();
	}
	
	public boolean moveCursorToLast(){
		return cursor.moveToLast();
	}

}
