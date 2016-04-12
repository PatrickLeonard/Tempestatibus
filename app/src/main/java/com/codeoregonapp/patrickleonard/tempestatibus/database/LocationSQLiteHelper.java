package com.codeoregonapp.patrickleonard.tempestatibus.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Class that extends SQLiteOpenHelper to create a database with a table to hold onto location data
 * for the user to reuse.
 * Created by Patrick Leonard on 2/9/2016.
 */
public class LocationSQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "locations.db";
    private static final int DB_VERSION = 1;

    //Meme table code
    public static final String LOCATIONS_TABLE = "LOCATIONS";
    public static final String COLUMN_LOCATIONS_ADDRESS = "ADDRESS";
    public static final String COLUMN_LOCATIONS_NAME = "NAME";
    public static final String COLUMN_LOCATIONS_LATITUDE = "LATITUDE";
    public static final String COLUMN_LOCATIONS_LONGITUDE = "LONGITUDE";

    private static final String CREATE_LOCATIONS__TABLE =
            "CREATE TABLE " + LOCATIONS_TABLE + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_LOCATIONS_ADDRESS + " TEXT, " +
                    COLUMN_LOCATIONS_NAME + " TEXT, " +
                    COLUMN_LOCATIONS_LATITUDE + " REAL, " +
                    COLUMN_LOCATIONS_LONGITUDE + " REAL )";


    public LocationSQLiteHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATIONS__TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
