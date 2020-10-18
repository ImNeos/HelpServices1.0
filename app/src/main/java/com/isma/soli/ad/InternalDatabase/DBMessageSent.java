package com.isma.soli.ad.InternalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.isma.soli.ad.Model.MessageSentClass;
import com.isma.soli.ad.Model.MyMessageClass;

import java.util.List;

import static com.isma.soli.ad.InternalDatabase.DBMessageSent.FeedEntry.TABLE_NAME;

public class DBMessageSent {

    private static MessageSentHelper mDbHelper = null;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBMessageSent()
    {
    }

    private static DBMessageSent instance = null;


    public static DBMessageSent getInstance(Context context)
    {
        if (instance == null) {
            instance = new DBMessageSent();
            mDbHelper = new MessageSentHelper(context);
        }
        return instance;
    }


    public void addElementTodB(MessageSentClass myMessageClass)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_KEY, myMessageClass.getKey());
        values.put(FeedEntry.COLUMN_TITLE, myMessageClass.getTitle());
        values.put(FeedEntry.COLUMN_Name, myMessageClass.getUserName());
        values.put(FeedEntry.COLUMN_USER_ID, myMessageClass.getUserID());
        values.put(FeedEntry.COLUMN_TIME_STAMP, myMessageClass.getTime_stamp());
        values.put(FeedEntry.COLUMN_TEXT, myMessageClass.getMessage());



        db.insert(TABLE_NAME, null, values);

    }

    public void fillInlist(List<MessageSentClass> myMessageClassList)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        myMessageClassList.clear();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);

        while (cursor.moveToNext()) {


            MessageSentClass messageClass = new MessageSentClass(cursor.getString(0), cursor.getString(3), cursor.getString(2),
                    cursor.getString(1), cursor.getString(5),cursor.getString(4));
            myMessageClassList.add(0, messageClass);

        }
        cursor.close();
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
        static final String TABLE_NAME = "Name";
        static final String COLUMN_KEY = "uniquekey";
        static final String COLUMN_TITLE = "titre";
        static final String COLUMN_Name = "Name";
        static final String COLUMN_USER_ID = "Phone";
        static final String COLUMN_TIME_STAMP = "Time";
        static final String COLUMN_TEXT = "Text";



    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    FeedEntry.COLUMN_KEY + " TEXT PRIMARY KEY," +
                    FeedEntry.COLUMN_TITLE + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_Name + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_USER_ID + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_TIME_STAMP + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_TEXT + TEXT_TYPE +" )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    private static class MessageSentHelper extends SQLiteOpenHelper
    {
        // If you change the database schema, you must increment the database version.
        static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "messagesent.db";

        MessageSentHelper(Context context) {
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
