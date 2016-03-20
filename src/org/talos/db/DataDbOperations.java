package org.talos.db;

import org.talos.beans.DataBean;
import org.talos.db.DataContract.DataEntry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataDbOperations {
	DataDbHelper mDbHelper;
	Cursor cursor;

	public DataDbOperations(final Context context) {
		mDbHelper = new DataDbHelper(context.getApplicationContext());
	}

	public void initRead() {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] projection = { DataEntry.TIME_STAMP, DataEntry.USER,
				DataEntry.OPERATOR, DataEntry.NETWORK_TYPE, DataEntry.CINR, DataEntry.LATITUDE,
				DataEntry.LONGITUDE };
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

		System.out.println(cursor.getCount());
		return result;
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

	public void clearData() {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.execSQL(DataDbHelper.SQL_DELETE_ENTRIES);
		db.execSQL(DataDbHelper.SQL_CREATE_ENTRIES);
	}

}
