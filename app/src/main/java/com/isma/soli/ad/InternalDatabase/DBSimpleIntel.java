package com.isma.soli.ad.InternalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.isma.soli.ad.Util.StaticValues;

import static com.isma.soli.ad.InternalDatabase.DBSimpleIntel.FeedEntry.TABLE_NAME;


public class DBSimpleIntel
{
    private static SimpleIntelHelper mDbHelper = null;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBSimpleIntel() {
    }

    private static DBSimpleIntel instance = null;


    public static DBSimpleIntel getInstance(Context context)
    {
        if (instance == null) {
            instance = new DBSimpleIntel();
            mDbHelper = new SimpleIntelHelper(context);
        }
        return instance;
    }
    public void addElementTodB(String nom, String value)
    {
        if (checkAlreadyExist(nom))
        {
            UpdateValue(nom, value);
        }
        else {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FeedEntry.COLUMN_NAME, nom);
            values.put(FeedEntry.COLUMN_LAST_SYNC, value);
            db.insert(TABLE_NAME, null, values);
        }
    }
    public void Deletecom (String Key)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, FeedEntry.COLUMN_NAME + "=?", new String[]{Key});
    }
    public boolean checkAlreadyExist(String name)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + FeedEntry.COLUMN_NAME + " =?";
        Cursor cursor = db.rawQuery(Query, new String[]{name});
        if (cursor.getCount() > 0)
        {
            cursor.close();
            return true;
        }
        else
            cursor.close();
        return false;
    }
    public String getLastValue (String name)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + FeedEntry.COLUMN_NAME + "=?", new String[]{name});

        while (cursor.moveToNext())
        {
            return cursor.getString(1);
        }
        cursor.close();
        return "";
    }
    public void UpdateValue(String nom, String time)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME, nom);
        values.put(FeedEntry.COLUMN_LAST_SYNC, time);
        db.update(TABLE_NAME, values, FeedEntry.COLUMN_NAME + "=?", new String[]{nom});
    }
    public String ReturnValueIfExists(String nom)
    {
        if (checkAlreadyExist(nom))
        {
            return getLastValue(nom);
        }
        else
        {
            return "not";
        }
    }

    public void SetEmailPassword(String UID, String email, int x)
    {
        if (x == 0) //first time : register !
        {
            addElementTodB(StaticValues.UserEmail, email);
            addElementTodB(StaticValues.UserID, UID);
        }
        else //login
        {
                addElementTodB(StaticValues.UserEmail, email);
                addElementTodB(StaticValues.UserID, UID);
        }
    }


    public void dropDB(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns
    {
        static final String TABLE_NAME = "Intel";
        static final String COLUMN_NAME = "Nom";
        static final String COLUMN_LAST_SYNC = "Sync";

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    FeedEntry.COLUMN_NAME + " TEXT PRIMARY KEY," +
                    FeedEntry.COLUMN_LAST_SYNC + TEXT_TYPE +" )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    private static class SimpleIntelHelper extends SQLiteOpenHelper
    {
        // If you change the database schema, you must increment the database version.
        static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "intel.db";

        SimpleIntelHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
