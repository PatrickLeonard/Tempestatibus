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
import java.util.Locale;

/**
 * Class to handle CRUD operations on the database
 * Created by Patrick Leonard on 2/9/2016.
 */
public class CachedLocationDataSource {

    private static final String TAG = CachedLocationDataSource.class.getSimpleName();
    private CachedDataSQLiteHelper mCachedDataSqlLiteHelper;

    public CachedLocationDataSource(Context context) {
        mCachedDataSqlLiteHelper = new CachedDataSQLiteHelper(context);
    }

    private SQLiteDatabase open() {
        return mCachedDataSqlLiteHelper.getWritableDatabase();
    }

    private void close(SQLiteDatabase db) {
        db.close();
    }

    public void createLocation(Location location, String name, String standardAddress, String shortenedAddress) {
        SQLiteDatabase db = open();
        db.beginTransaction();
        ContentValues locationValues = new ContentValues();
        locationValues.put(CachedDataSQLiteHelper.COLUMN_LOCATIONS_NAME, name);
        locationValues.put(CachedDataSQLiteHelper.COLUMN_LOCATIONS_STANDARD_ADDRESS, standardAddress);
        locationValues.put(CachedDataSQLiteHelper.COLUMN_LOCATIONS_SHORTENED_ADDRESS, shortenedAddress);
        locationValues.put(CachedDataSQLiteHelper.COLUMN_LOCATIONS_LATITUDE, location.getLatitude());
        locationValues.put(CachedDataSQLiteHelper.COLUMN_LOCATIONS_LONGITUDE, location.getLongitude());
        db.insert(CachedDataSQLiteHelper.LOCATIONS_TABLE, null, locationValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        close(db);
    }

    public void deleteLocation(int id) {
        Log.v(CachedLocationDataSource.TAG, "Deleting id: " + id);
        SQLiteDatabase db = open();
        db.beginTransaction();
        db.delete(CachedDataSQLiteHelper.LOCATIONS_TABLE,
                String.format("%s=%s", BaseColumns._ID, String.valueOf(id))
                , null);
        db.setTransactionSuccessful();
        db.endTransaction();
        close(db);
        Log.v(CachedLocationDataSource.TAG, "Db is closed");
    }

    public void updateLocation(int id, String name) {
        SQLiteDatabase db = open();
        db.beginTransaction();

        ContentValues updateLocationValues = new ContentValues();

        updateLocationValues.put(CachedDataSQLiteHelper.COLUMN_LOCATIONS_NAME, name);
        db.update(CachedDataSQLiteHelper.LOCATIONS_TABLE,
                updateLocationValues,
                String.format(Locale.getDefault(),"%s=%d", BaseColumns._ID, id)
                , null);
        db.setTransactionSuccessful();
        db.endTransaction();
        close(db);
    }

    //This needs to have the model class created and used. It also should implement Parcelable
    public ArrayList<SavedLocationModel> readLocationsWrapper() {
        return readLocations();
    }

    //This needs to have the model class created and used. It also should implement Parcelable
    public ArrayList<SavedLocationModel> readLastKnownLocationWrapper() {
        return readLastKnownLocation();
    }

    //This needs to have the model class created and used. It also should implement Parcelable
    private ArrayList<SavedLocationModel> readLocations() {
        SQLiteDatabase db = open();
        Cursor cursor = db.query(CachedDataSQLiteHelper.LOCATIONS_TABLE,
                new String[]{CachedDataSQLiteHelper.COLUMN_LOCATIONS_NAME,
                        BaseColumns._ID,
                        CachedDataSQLiteHelper.COLUMN_LOCATIONS_STANDARD_ADDRESS,
                        CachedDataSQLiteHelper.COLUMN_LOCATIONS_SHORTENED_ADDRESS,
                        CachedDataSQLiteHelper.COLUMN_LOCATIONS_LATITUDE,
                        CachedDataSQLiteHelper.COLUMN_LOCATIONS_LONGITUDE},
                String.format(Locale.getDefault(),"%s <> %d",BaseColumns._ID, CachedDataSQLiteHelper.DEFAULT_LAST_KNOWN_ID),  //Selection
                null,  //Selection args
                null,  //Group By
                null,  //Having
                BaseColumns._ID + " ASC",  //Order
                null); //Limit

        Location location;
        int id;
        String name;
        String standardAddress;
        String shortenedAddress;
        ArrayList<SavedLocationModel> savedLocationModels = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do{
                id = getIntFromColumnName(cursor,BaseColumns._ID);
                name = getStringFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_LOCATIONS_NAME);
                standardAddress = getStringFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_LOCATIONS_STANDARD_ADDRESS);
                shortenedAddress = getStringFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_LOCATIONS_SHORTENED_ADDRESS);
                location = new Location(LocationManager.PASSIVE_PROVIDER);
                location.setLatitude(getRealFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_LOCATIONS_LATITUDE));
                location.setLongitude(getRealFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_LOCATIONS_LONGITUDE));
                Log.v(CachedLocationDataSource.TAG, "Reading: id: " + id + " name: " + name + " standard address: " + standardAddress + " shortened address: " + shortenedAddress);
                savedLocationModels.add(new SavedLocationModel(location,id,name,standardAddress,shortenedAddress));
            }while(cursor.moveToNext());
        }
        cursor.close();
        close(db);
        return savedLocationModels;
    }

    //This needs to have the model class created and used. It also should implement Parcelable
    private ArrayList<SavedLocationModel> readLastKnownLocation() {
        SQLiteDatabase db = open();
        Cursor cursor = db.query(CachedDataSQLiteHelper.LOCATIONS_TABLE,
                new String[]{CachedDataSQLiteHelper.COLUMN_LOCATIONS_NAME,
                        BaseColumns._ID,
                        CachedDataSQLiteHelper.COLUMN_LOCATIONS_STANDARD_ADDRESS,
                        CachedDataSQLiteHelper.COLUMN_LOCATIONS_SHORTENED_ADDRESS,
                        CachedDataSQLiteHelper.COLUMN_LOCATIONS_LATITUDE,
                        CachedDataSQLiteHelper.COLUMN_LOCATIONS_LONGITUDE},
                String.format(Locale.getDefault(),"%s = %d",BaseColumns._ID, CachedDataSQLiteHelper.DEFAULT_LAST_KNOWN_ID),  //Selection
                null,  //Selection args
                null,  //Group By
                null,  //Having
                BaseColumns._ID + " ASC",  //Order
                null); //Limit

        Location lastKnown;
        int id;
        String name;
        String standardAddress;
        String shortenedAddress;
        ArrayList<SavedLocationModel> savedLastKnown = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do{
                id = getIntFromColumnName(cursor,BaseColumns._ID);
                name = getStringFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_LOCATIONS_NAME);
                standardAddress = getStringFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_LOCATIONS_STANDARD_ADDRESS);
                shortenedAddress = getStringFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_LOCATIONS_SHORTENED_ADDRESS);
                lastKnown = new Location(LocationManager.PASSIVE_PROVIDER);
                lastKnown.setLatitude(getRealFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_LOCATIONS_LATITUDE));
                lastKnown.setLongitude(getRealFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_LOCATIONS_LONGITUDE));
                Log.v(CachedLocationDataSource.TAG, "Reading: id: " + id + " name: " + name + " standard address: " + standardAddress + " shortened address: " + shortenedAddress);
                savedLastKnown.add(new SavedLocationModel(lastKnown,id,name,standardAddress,shortenedAddress));
            }while(cursor.moveToNext());
        }
        cursor.close();
        close(db);
        return savedLastKnown;
    }

    public void updateLastKnownLocation(String standardAddress, String shortenedAddress, double latitude, double longitude) {
        SQLiteDatabase db = open();
        db.beginTransaction();

        ContentValues updateLocationValues = new ContentValues();

        updateLocationValues.put(CachedDataSQLiteHelper.COLUMN_LOCATIONS_STANDARD_ADDRESS, standardAddress);
        updateLocationValues.put(CachedDataSQLiteHelper.COLUMN_LOCATIONS_SHORTENED_ADDRESS, shortenedAddress);
        updateLocationValues.put(CachedDataSQLiteHelper.COLUMN_LOCATIONS_LATITUDE, latitude);
        updateLocationValues.put(CachedDataSQLiteHelper.COLUMN_LOCATIONS_LONGITUDE, longitude);
        db.update(CachedDataSQLiteHelper.LOCATIONS_TABLE,
                updateLocationValues,
                String.format(Locale.getDefault(),"%s=%d", BaseColumns._ID, CachedDataSQLiteHelper.DEFAULT_LAST_KNOWN_ID)
                , null);
        db.setTransactionSuccessful();
        db.endTransaction();
        close(db);
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
