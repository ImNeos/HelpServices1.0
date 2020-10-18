package com.isma.soli.ad.InternalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.isma.soli.ad.Model.MyMessageClass;

import java.util.List;

import static com.isma.soli.ad.InternalDatabase.DBMessage.FeedEntry.TABLE_NAME;

public class DBMessage {


    private static MessageHelper mDbHelper = null;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBMessage()
    {
    }

    private static DBMessage instance = null;


    public static DBMessage getInstance(Context context)
    {
        if (instance == null) {
            instance = new DBMessage();
            mDbHelper = new MessageHelper(context);
        }
        return instance;
    }
    public void addElementTodB(MyMessageClass myMessageClass)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_KEY, myMessageClass.getKey());
        values.put(FeedEntry.COLUMN_TITLE, myMessageClass.getMessage());
        values.put(FeedEntry.COLUMN_Name, myMessageClass.getName());
        values.put(FeedEntry.COLUMN_PhoneNumber, myMessageClass.getPhonenumber());
        values.put(FeedEntry.COLUMN_TIME_STAMP, myMessageClass.getTime());
        values.put(FeedEntry.COLUMN_TEXT, myMessageClass.getText());



        if (checkAlreadyExist(myMessageClass.getKey()))
        {
            db.update(TABLE_NAME, values, FeedEntry.COLUMN_KEY + "=?", new String[]{myMessageClass.getKey()});

        }
        else
        {
            db.insert(TABLE_NAME, null, values);
        }

    }
    public void fillInlist(List<MyMessageClass> myMessageClassList)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        myMessageClassList.clear();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);

        while (cursor.moveToNext()) {


            MyMessageClass messageClass = new MyMessageClass(cursor.getString(2), cursor.getString(3), cursor.getString(1),
                    cursor.getString(0), cursor.getString(4),cursor.getString(5));
            myMessageClassList.add(0, messageClass);

        }
        cursor.close();
    }
    public boolean checkAlreadyExist(String name)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + FeedEntry.COLUMN_KEY + " =?";
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
    public void Deletecom (String Key)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, FeedEntry.COLUMN_KEY + "=?", new String[]{Key});
    }
    public String getInside (String key)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + FeedEntry.COLUMN_KEY + "=?", new String[]{key});

        while (cursor.moveToNext())
        {
            return cursor.getString(1);
        }
        cursor.close();
        return "";
    }


    public void dropDB(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void delete(String key)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();



        db.delete(TABLE_NAME, FeedEntry.COLUMN_KEY + "=?", new String[]{key});
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns
    {
        static final String TABLE_NAME = "Checkcom";
        static final String COLUMN_KEY = "uniquekey";
        static final String COLUMN_TITLE = "titre";
        static final String COLUMN_Name = "Name";
        static final String COLUMN_PhoneNumber = "Phone";
        static final String COLUMN_TIME_STAMP = "Time";
        static final String COLUMN_TEXT = "Text";



    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    FeedEntry.COLUMN_KEY + " TEXT PRIMARY KEY," +
                    FeedEntry.COLUMN_TITLE + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_Name + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_PhoneNumber + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_TIME_STAMP + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_TEXT + TEXT_TYPE +" )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    private static class MessageHelper extends SQLiteOpenHelper
    {
        // If you change the database schema, you must increment the database version.
        static final int DATABASE_VERSION = 2;
        static final String DATABASE_NAME = "message.db";

        MessageHelper(Context context) {
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

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}

