package com.codeoregonapp.patrickleonard.tempestatibus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.codeoregonapp.patrickleonard.tempestatibus.weather.WeatherAlert;

import java.util.ArrayList;

/**
 * Cached Notifications Data Source, enter and extract the current severe weather alerts
 * Created by Patrick Leonard on 7/15/2016.
 */
public class CachedNotificationDataSource {

    private static final String TAG = CachedNotificationDataSource.class.getSimpleName();
    private CachedDataSQLiteHelper mCachedDataSqlLiteHelper;

    public CachedNotificationDataSource(Context context) {
        mCachedDataSqlLiteHelper = new CachedDataSQLiteHelper(context);
    }

    private SQLiteDatabase open() {
        return mCachedDataSqlLiteHelper.getWritableDatabase();
    }

    private void close(SQLiteDatabase db) {
        db.close();
    }

    public void createNotificationRecord(WeatherAlert alert) {
        SQLiteDatabase db = open();
        db.beginTransaction();
        ContentValues alertValues = new ContentValues();
        alertValues.put(CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_TITLE, alert.getTitle());
        alertValues.put(CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_TIME, alert.getTime());
        alertValues.put(CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_EXPIRES, alert.getExpires());
        alertValues.put(CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_DESCRIPTION, alert.getDescription());
        alertValues.put(CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_URI, alert.getUriString());
        alertValues.put(CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_UUID, alert.getUUID());
        alertValues.put(CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_ID, alert.getID());
        db.insert(CachedDataSQLiteHelper.NOTIFICATIONS_TABLE, null, alertValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        close(db);
    }

    public void deleteNotificationRecord(String UUID, int ID) {
        SQLiteDatabase db = open();
        db.beginTransaction();
        db.delete(CachedDataSQLiteHelper.NOTIFICATIONS_TABLE,
                String.format("%s='%s' AND %s=%s", CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_UUID, UUID, CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_ID, String.valueOf(ID))
                , null);
        db.setTransactionSuccessful();
        db.endTransaction();
        close(db);
    }

    //This needs to have the model class created and used. It also should implement Parcelable
    public ArrayList<WeatherAlert> readNotifications() {
        SQLiteDatabase db = open();
        Cursor cursor = db.query(CachedDataSQLiteHelper.NOTIFICATIONS_TABLE,
                new String[]{BaseColumns._ID,
                        CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_TITLE,
                        CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_TIME,
                        CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_EXPIRES,
                        CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_DESCRIPTION,
                        CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_URI,
                        CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_UUID,
                        CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_ID},
                null,  //Selection
                null,  //Selection args
                null,  //Group By
                null,  //Having
                BaseColumns._ID + " ASC",  //Order
                null); //Limit
        WeatherAlert alert;
        int id;
        String title;
        long time;
        long expires;
        String description;
        String uri;
        String UUID;
        int ID;
        ArrayList<WeatherAlert> weatherAlerts = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do{
                id = getIntFromColumnName(cursor,BaseColumns._ID);
                title = getStringFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_TITLE);
                time = getIntFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_TIME);
                expires = getIntFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_EXPIRES);
                description = getStringFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_DESCRIPTION);
                uri = getStringFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_URI);
                UUID = getStringFromColumnName(cursor, CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_UUID);
                ID = getIntFromColumnName(cursor,CachedDataSQLiteHelper.COLUMN_NOTIFICATIONS_ID);
                alert = new WeatherAlert(title,time,expires,description,uri);
                alert.setUUID(UUID);
                alert.setID(ID);
                weatherAlerts.add(alert);
            }while(cursor.moveToNext());
        }
        cursor.close();
        close(db);
        return weatherAlerts;
    }


    private int getIntFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }
}
