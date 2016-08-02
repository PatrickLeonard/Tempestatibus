package com.codeoregonapp.patrickleonard.tempestatibus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Class to handle CRUD operations on the database
 * Created by Patrick Leonard on 6/14/2016.
 */
public class CachedForecastDataSource {

    private static final String TAG = CachedForecastDataSource.class.getSimpleName();
    private CachedDataSQLiteHelper mCachedDataSqlLiteHelper;

    public CachedForecastDataSource(Context context) {
        mCachedDataSqlLiteHelper = new CachedDataSQLiteHelper(context);
    }

    private SQLiteDatabase open() {
        return mCachedDataSqlLiteHelper.getWritableDatabase();
    }

    private void close(SQLiteDatabase db) {
        db.close();
    }

    //This needs to have the model class created and used. It also should implement Parcelable
    public ArrayList<SavedForecastModel> readLastKnownForecast() {
        SQLiteDatabase db = open();
        Cursor cursor = db.query(CachedDataSQLiteHelper.LAST_FORECAST_DATA_TABLE,
                new String[]{CachedDataSQLiteHelper.COLUMN_FORECAST_DATA, BaseColumns._ID},
                String.format(Locale.getDefault(),"%s = %d",BaseColumns._ID, CachedDataSQLiteHelper.DEFAULT_LAST_KNOWN_ID),  //Selection
                null,  //Selection args
                null,  //Group By
                null,  //Having
                BaseColumns._ID + " ASC",  //Order
                null); //Limit
        int id;
        String data;
        ArrayList<SavedForecastModel> savedLastKnown = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do{
                id = getIntFromColumnName(cursor,BaseColumns._ID);
                data = getStringFromColumnName(cursor,CachedDataSQLiteHelper.COLUMN_FORECAST_DATA);
                Log.v(CachedForecastDataSource.TAG, "Reading: id: " + id + " data: " + data);
                savedLastKnown.add(new SavedForecastModel(id,data));
            }while(cursor.moveToNext());
        }
        cursor.close();
        close(db);
        return savedLastKnown;
    }

    public void updateLastKnownForecast(String data) {
        SQLiteDatabase db = open();
        db.beginTransaction();

        ContentValues updateForecastValues = new ContentValues();

        updateForecastValues.put(CachedDataSQLiteHelper.COLUMN_FORECAST_DATA, data);
        db.update(CachedDataSQLiteHelper.LAST_FORECAST_DATA_TABLE,
                updateForecastValues,
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

    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

}
