package com.codeoregonapp.patrickleonard.tempestatibus.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.codeoregonapp.patrickleonard.tempestatibus.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class that extends SQLiteOpenHelper to create a database with a table to hold onto location data
 * for the user to reuse.
 * Created by Patrick Leonard on 2/9/2016.
 */
public class CachedDataSQLiteHelper extends SQLiteOpenHelper {

    public static final String TAG = CachedDataSQLiteHelper.class.getSimpleName();
    private static final String DB_NAME = "locations.db";
    private static final int DB_VERSION = 4;
    private final String mDefaultData;
    private Context mContext;

    //Locations table code
    public static final String LOCATIONS_TABLE = "LOCATIONS";
    public static final String COLUMN_LOCATIONS_STANDARD_ADDRESS = "STANDARD_ADDRESS";
    public static final String COLUMN_LOCATIONS_SHORTENED_ADDRESS = "SHORTENED_ADDRESS";
    public static final String COLUMN_LOCATIONS_NAME = "NAME";
    public static final String COLUMN_LOCATIONS_LATITUDE = "LATITUDE";
    public static final String COLUMN_LOCATIONS_LONGITUDE = "LONGITUDE";

    //Default Last Known Location
    public static final int DEFAULT_LAST_KNOWN_ID = -1;
    public static final String DEFAULT_LAST_KNOWN_STANDARD_ADDRESS = "Portland, OR";
    public static final String DEFAULT_LAST_KNOWN_SHORTENED_ADDRESS = "Portland, OR";
    public static final String DEFAULT_LAST_KNOWN_NAME = "LAST_KNOWN";
    public static final Double DEFAULT_LAST_KNOWN_LATITUDE = 45.5231;
    public static final Double DEFAULT_LAST_KNOWN_LONGITUDE = -122.6765;

    private static final String DROP_OLD_LOCATIONS_TABLE =
            "DROP TABLE IF EXISTS " + LOCATIONS_TABLE + " ;";

    private static final String CREATE_LOCATIONS_TABLE =
            "CREATE TABLE " + LOCATIONS_TABLE + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY," +
                    COLUMN_LOCATIONS_STANDARD_ADDRESS + " TEXT, " +
                    COLUMN_LOCATIONS_SHORTENED_ADDRESS + " TEXT, " +
                    COLUMN_LOCATIONS_NAME + " TEXT, " +
                    COLUMN_LOCATIONS_LATITUDE + " REAL, " +
                    COLUMN_LOCATIONS_LONGITUDE + " REAL );";

    private static final String INSERT_LAST_KNOWN_LOCATION =
            "INSERT OR REPLACE INTO " + LOCATIONS_TABLE + " (" +
                    BaseColumns._ID + "," +
                    COLUMN_LOCATIONS_STANDARD_ADDRESS + ", " +
                    COLUMN_LOCATIONS_SHORTENED_ADDRESS + ", " +
                    COLUMN_LOCATIONS_NAME + "," +
                    COLUMN_LOCATIONS_LATITUDE + "," +
                    COLUMN_LOCATIONS_LONGITUDE + ") " +
            "VALUES (" + DEFAULT_LAST_KNOWN_ID + ",'" +
                    DEFAULT_LAST_KNOWN_STANDARD_ADDRESS + "','" +
                    DEFAULT_LAST_KNOWN_SHORTENED_ADDRESS + "','" +
                    DEFAULT_LAST_KNOWN_NAME + "'," +
                    DEFAULT_LAST_KNOWN_LATITUDE + "," +
                    DEFAULT_LAST_KNOWN_LONGITUDE + ");";

    //Forecast Data table code
    public static final String LAST_FORECAST_DATA_TABLE = "FORECAST";
    public static final String COLUMN_FORECAST_DATA = "FORECAST_DATA";

    private static final String CREATE_LAST_FORECAST_DATA_TABLE =
            "CREATE TABLE " + LAST_FORECAST_DATA_TABLE + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY," +
                    COLUMN_FORECAST_DATA + " TEXT);";
    //Insert row using format string
    private static final String INSERT_LAST_FORECAST_DATA =
            "INSERT OR REPLACE INTO " + LAST_FORECAST_DATA_TABLE + " (" +
                    BaseColumns._ID + "," +
                    COLUMN_FORECAST_DATA + ") " +
                    "VALUES (" + DEFAULT_LAST_KNOWN_ID + ",'%s');";

    //Weather Alert Notifications table code
    public static final String NOTIFICATIONS_TABLE = "NOTIFICATIONS";
    public static final String COLUMN_NOTIFICATIONS_TITLE = "TITLE";
    public static final String COLUMN_NOTIFICATIONS_TIME = "TIME";
    public static final String COLUMN_NOTIFICATIONS_EXPIRES = "EXPIRES";
    public static final String COLUMN_NOTIFICATIONS_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_NOTIFICATIONS_URI = "URI";
    public static final String COLUMN_NOTIFICATIONS_UUID = "UUID";
    public static final String COLUMN_NOTIFICATIONS_ID = "ID";


    private static final String CREATE_NOTIFICATIONS_TABLE =
            "CREATE TABLE " + NOTIFICATIONS_TABLE + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NOTIFICATIONS_TITLE + " TEXT, " +
                    COLUMN_NOTIFICATIONS_TIME + " INTEGER, " +
                    COLUMN_NOTIFICATIONS_EXPIRES + " INTEGER, " +
                    COLUMN_NOTIFICATIONS_DESCRIPTION + " TEXT, " +
                    COLUMN_NOTIFICATIONS_URI + " TEXT, " +
                    COLUMN_NOTIFICATIONS_UUID + " TEXT, " +
                    COLUMN_NOTIFICATIONS_ID + " INTEGER);";

    public CachedDataSQLiteHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        mContext = context;
        mDefaultData = getDefaultData();
    }

    private String getDefaultData() {
        // The InputStream opens the resourceId and sends it to the buffer
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.default_data);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String defaultData = "";
        String line;
        try {
            // While the BufferedReader line is not null
            while ((line = bufferedReader.readLine()) != null) {
                defaultData = defaultData.concat(line);
            }
            // Close the InputStream and BufferedReader
            inputStream.close();
            bufferedReader.close();

        } catch (IOException e) {
            Log.e(CachedDataSQLiteHelper.TAG,mContext.getString(R.string.error_message),e);
        }

        return defaultData;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATIONS_TABLE);
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
        db.execSQL(INSERT_LAST_KNOWN_LOCATION);
        db.execSQL(CREATE_LAST_FORECAST_DATA_TABLE);
        db.execSQL(String.format(INSERT_LAST_FORECAST_DATA,mDefaultData));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2)  {
            db.execSQL(DROP_OLD_LOCATIONS_TABLE);
            db.execSQL(CREATE_LOCATIONS_TABLE);
            db.execSQL(INSERT_LAST_KNOWN_LOCATION);
        }
        if(oldVersion < 3) {
            db.execSQL(CREATE_LAST_FORECAST_DATA_TABLE);
            db.execSQL(String.format(INSERT_LAST_FORECAST_DATA,mDefaultData));
        }
        if(oldVersion < 4) {
            db.execSQL(CREATE_NOTIFICATIONS_TABLE);
        }
    }
}
