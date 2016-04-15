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

    public static final int DEFAULT_LAST_KNOWN_ID = -1;
    public static final String DEFAULT_LAST_KNOWN_ADDRESS = "Portland, OR";
    public static final String DEFAULT_LAST_KNOWN_NAME = "LAST_KNOWN";
    public static final Double DEFAULT_LAST_KNOWN_LATITUDE = 45.5231;
    public static final Double DEFAULT_LAST_KNOWN_LONGITUDE = -122.6765;


    private static final String CREATE_LOCATIONS__TABLE =
            "CREATE TABLE " + LOCATIONS_TABLE + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY," +
                    COLUMN_LOCATIONS_ADDRESS + " TEXT, " +
                    COLUMN_LOCATIONS_NAME + " TEXT, " +
                    COLUMN_LOCATIONS_LATITUDE + " REAL, " +
                    COLUMN_LOCATIONS_LONGITUDE + " REAL );";

    private static final String INSERT_LAST_KNOWN =
            "INSERT OR REPLACE INTO " + LOCATIONS_TABLE + " (" +
                    BaseColumns._ID + "," +
                    COLUMN_LOCATIONS_ADDRESS + "," +
                    COLUMN_LOCATIONS_NAME + "," +
                    COLUMN_LOCATIONS_LATITUDE + "," +
                    COLUMN_LOCATIONS_LONGITUDE + ") " +
            "VALUES (" + DEFAULT_LAST_KNOWN_ID + ",'" +
                    DEFAULT_LAST_KNOWN_ADDRESS + "','" +
                    DEFAULT_LAST_KNOWN_NAME + "'," +
                    DEFAULT_LAST_KNOWN_LATITUDE + "," +
                    DEFAULT_LAST_KNOWN_LONGITUDE + ");";

    public LocationSQLiteHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATIONS__TABLE);
        db.execSQL(INSERT_LAST_KNOWN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2)  {
                db.execSQL(INSERT_LAST_KNOWN);
            }
    }
}
