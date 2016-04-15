package com.codeoregonapp.patrickleonard.tempestatibus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

/**
 * Class to handle CRUD operations on the database
 * Created by Patrick Leonard on 2/9/2016.
 */
public class LocationDataSource {

    private static final String TAG = LocationDataSource.class.getSimpleName();
    private LocationSQLiteHelper mLocationSqlLiteHelper;

    public LocationDataSource(Context context) {
        mLocationSqlLiteHelper = new LocationSQLiteHelper(context);
    }

    private SQLiteDatabase open() {
        return mLocationSqlLiteHelper.getWritableDatabase();
    }

    private void close(SQLiteDatabase db) {
        db.close();
    }

    public void create(Location location, String name, String address) {
        SQLiteDatabase db = open();
        db.beginTransaction();
        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationSQLiteHelper.COLUMN_LOCATIONS_NAME, name);
        locationValues.put(LocationSQLiteHelper.COLUMN_LOCATIONS_ADDRESS, address);
        locationValues.put(LocationSQLiteHelper.COLUMN_LOCATIONS_LATITUDE, location.getLatitude());
        locationValues.put(LocationSQLiteHelper.COLUMN_LOCATIONS_LONGITUDE, location.getLongitude());
        db.insert(LocationSQLiteHelper.LOCATIONS_TABLE, null, locationValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        close(db);
    }

    public void delete(int id) {
        Log.v(LocationDataSource.TAG, "Deleting id: " + id);
        SQLiteDatabase db = open();
        db.beginTransaction();
        db.delete(LocationSQLiteHelper.LOCATIONS_TABLE,
                String.format("%s=%s", BaseColumns._ID, String.valueOf(id))
                , null);
        db.setTransactionSuccessful();
        db.endTransaction();
        close(db);
        Log.v(LocationDataSource.TAG, "Db is closed");
    }

    public void update(int id, String name) {
        SQLiteDatabase db = open();
        db.beginTransaction();

        ContentValues updateLocationValues = new ContentValues();

        updateLocationValues.put(LocationSQLiteHelper.COLUMN_LOCATIONS_NAME, name);
        db.update(LocationSQLiteHelper.LOCATIONS_TABLE,
                updateLocationValues,
                String.format("%s=%d", BaseColumns._ID, id)
                , null);
        db.setTransactionSuccessful();
        db.endTransaction();
        close(db);
    }

    //This needs to have the model class created and used. It also should implement Parcelable
    public ArrayList<SavedLocationModel> read() {
        return readLocations();
    }

    //This needs to have the model class created and used. It also should implement Parcelable
    private ArrayList<SavedLocationModel> readLocations() {
        SQLiteDatabase db = open();
        Cursor cursor = db.query(LocationSQLiteHelper.LOCATIONS_TABLE,
                new String[]{LocationSQLiteHelper.COLUMN_LOCATIONS_NAME,
                        BaseColumns._ID,
                        LocationSQLiteHelper.COLUMN_LOCATIONS_ADDRESS,
                        LocationSQLiteHelper.COLUMN_LOCATIONS_LATITUDE,
                        LocationSQLiteHelper.COLUMN_LOCATIONS_LONGITUDE},
                BaseColumns._ID + " <> " + LocationSQLiteHelper.DEFAULT_LAST_KNOWN_ID,  //Selection
                null,  //Selection args
                null,  //Group By
                null,  //Having
                BaseColumns._ID + " ASC",  //Order
                null); //Limit

        Location location;
        int id;
        String name;
        String address;
        ArrayList<SavedLocationModel> savedLocationModels = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do{
                id = getIntFromColumnName(cursor,BaseColumns._ID);
                name = getStringFromColumnName(cursor, LocationSQLiteHelper.COLUMN_LOCATIONS_NAME);
                address = getStringFromColumnName(cursor, LocationSQLiteHelper.COLUMN_LOCATIONS_ADDRESS);
                location = new Location(LocationManager.PASSIVE_PROVIDER);
                location.setLatitude(getRealFromColumnName(cursor, LocationSQLiteHelper.COLUMN_LOCATIONS_LATITUDE));
                location.setLongitude(getRealFromColumnName(cursor, LocationSQLiteHelper.COLUMN_LOCATIONS_LONGITUDE));
                Log.v(LocationDataSource.TAG, "Reading: id: " + id + " name: " + name + " address: " + address);
                savedLocationModels.add(new SavedLocationModel(location,id,name,address));
            }while(cursor.moveToNext());
        }
        cursor.close();
        close(db);
        return savedLocationModels;
    }

    private int getIntFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private double getRealFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getDouble(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

}
