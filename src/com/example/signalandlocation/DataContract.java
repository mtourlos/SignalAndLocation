package com.example.signalandlocation;

import android.provider.BaseColumns;
	


public final class DataContract {
	
	public DataContract(){}
	
	public static abstract class DataEntry implements BaseColumns{
		public static final String TABLE_NAME="data";
		public static final String TIME_STAMP="TimeStamp";
		public static final String USER="user";
		public static final String OPERATOR="operator";
		public static final String CINR="cinr";
		public static final String LATITUDE="latidute";
		public static final String LONGTITUDE="longtidute";
	}
	
}
