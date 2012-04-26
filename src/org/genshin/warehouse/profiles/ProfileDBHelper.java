package org.genshin.warehouse.profiles;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfileDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_PROFILES = "profiles";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_SERVER = "server";
	public static final String COLUMN_PORT = "port";
	public static final String COLUMN_USER = "user";
	public static final String COLUMN_PASSWORD = "password";
	public static final String COLUMN_KEY = "key";
	public static final String DATABASE_NAME = "warehouse.db";
	public static final int DATABASE_VERSION = 2;
	
	//public static final String TABLE_PROFILE_SETTINGS = "profile_settings";
	//public static final String COLUMN_SETTINGS_DEFAULT_PROFILE = "default_profile";
	
	public static final String DATABASE_CREATE =
			//Profiles
			"create table "
			+ TABLE_PROFILES + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SERVER + " string not null, "
			+ COLUMN_PORT + " integer not null, "
			+ COLUMN_USER + " string not null, "
			+ COLUMN_PASSWORD + " string not null, "
			+ COLUMN_KEY + " string);";
	
	public ProfileDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
