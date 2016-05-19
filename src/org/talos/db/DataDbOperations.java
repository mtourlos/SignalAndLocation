package org.talos.db;

import org.talos.beans.DataBean;
import org.talos.db.DataContract.DataEntry;
import org.talos.services.TalosService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class DataDbOperations {
	DataDbHelper mDbHelper;
	Cursor cursor;
	SQLiteDatabase db;
	Context context;

	public DataDbOperations(final Context context) {
		this.context = context;
		mDbHelper = new DataDbHelper(context.getApplicationContext());
	}

	public void initWrite() {
		db = mDbHelper.getWritableDatabase();
	}

	public void initRead() {
		db = mDbHelper.getReadableDatabase();
		String[] projection = { DataEntry.TIME_STAMP, DataEntry.USER,
				DataEntry.OPERATOR, DataEntry.NETWORK_TYPE, DataEntry.CINR,
				DataEntry.LATITUDE, DataEntry.LONGITUDE };
		String sortOrder = DataEntry.TIME_STAMP + " DESC";

		cursor = db.query(DataEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);
	}

	public DataBean getData() {
		DataBean result = new DataBean();
		result.setTimeStamp(cursor.getString(cursor
				.getColumnIndexOrThrow(DataEntry.TIME_STAMP)));
		result.setUser(cursor.getString(cursor
				.getColumnIndexOrThrow(DataEntry.USER)));
		result.setOperator(cursor.getString(cursor
				.getColumnIndexOrThrow(DataEntry.OPERATOR)));
		result.setNetworkType(cursor.getString(cursor
				.getColumnIndexOrThrow(DataEntry.NETWORK_TYPE)));
		result.setCinr(cursor.getString(cursor
				.getColumnIndexOrThrow(DataEntry.CINR)));
		result.setLatitude(cursor.getString(cursor
				.getColumnIndexOrThrow(DataEntry.LATITUDE)));
		result.setLongitude(cursor.getString(cursor
				.getColumnIndexOrThrow(DataEntry.LONGITUDE)));

		// System.out.println(cursor.getCount());
		return result;
	}

	public void storeData() {
		ContentValues values = new ContentValues();
		values.put(DataEntry.TIME_STAMP, TalosService.getCurrentTimeStamp());
		values.put(DataEntry.USER, TalosService.activeUser);
		values.put(DataEntry.OPERATOR, TalosService.operatorName);
		values.put(DataEntry.CINR, TalosService.signalStrength);
		values.put(DataEntry.NETWORK_TYPE, TalosService.networkType);
		values.put(DataEntry.LATITUDE, TalosService.latitude);
		values.put(DataEntry.LONGITUDE, TalosService.longitude);
		db.insert(DataEntry.TABLE_NAME, null, values);
		Toast.makeText(context,
				"Data Stored in local db " + TalosService.getCurrentTimeStamp(),
				Toast.LENGTH_SHORT).show();
	}

	public boolean moveCursorToFirst() {
		return cursor.moveToFirst();
	}

	public boolean moveCursorNext() {
		return cursor.moveToNext();
	}

	public boolean isCursorLast() {
		return cursor.isLast();
	}

	public boolean moveCursorToLast() {
		return cursor.moveToLast();
	}

	public boolean dataExists() {
		initRead();
		return cursor.getCount() != 0 ? true : false;
	}

	public void clearData() {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.execSQL(DataDbHelper.SQL_DELETE_ENTRIES);
		db.execSQL(DataDbHelper.SQL_CREATE_ENTRIES);
	}
	
	public void close(){
		db.close();
	}

}
