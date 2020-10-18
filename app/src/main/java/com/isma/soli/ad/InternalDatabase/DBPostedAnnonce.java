package com.isma.soli.ad.InternalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.isma.soli.ad.Model.AnnonceClass;
import com.isma.soli.ad.Model.MyAnnonceClass;

import java.util.List;

import static com.isma.soli.ad.InternalDatabase.DBPostedAnnonce.FeedEntry.TABLE_NAME;

public class DBPostedAnnonce {
    private static AnnonceHelper mDbHelper = null;

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBPostedAnnonce() {
    }

    private static DBPostedAnnonce instance = null;


    public static DBPostedAnnonce getInstance(Context context)
    {
        if (instance == null) {
            instance = new DBPostedAnnonce();
            mDbHelper = new AnnonceHelper(context);
        }
        return instance;
    }
    public void addElementTodB(AnnonceClass annonceClass, String annonceKey, String postcode)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_KEY, annonceKey);
        values.put(FeedEntry.COLUMN_HEART, annonceClass.getHeart());
        values.put(FeedEntry.COLUMN_TITLE, annonceClass.getTitle());

        values.put(FeedEntry.COLUMN_TIME_STAMP, annonceClass.getTime());
        values.put(FeedEntry.COLUMN_PRICE, annonceClass.getPrix());
        values.put(FeedEntry.COLUMN_TYPE, annonceClass.getType());

        if (checkAlreadyExist(annonceKey))
        {
            values.put(FeedEntry.COLUMN_POST_CODE,getInside(annonceKey));
            db.update(FeedEntry.TABLE_NAME, values, FeedEntry.COLUMN_KEY + "=?", new String[]{annonceKey});

        }
        else
        {
            values.put(FeedEntry.COLUMN_POST_CODE, postcode);
            db.insert(TABLE_NAME, null, values);
        }

    }
    public void updatetime(String key, String time)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + FeedEntry.COLUMN_KEY + "=?", new String[]{key});

        while (cursor.moveToNext()) {

            MyAnnonceClass annonceClass = new MyAnnonceClass(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(5), cursor.getString(4), cursor.getString(6));
            ContentValues values = new ContentValues();
            values.put(FeedEntry.COLUMN_KEY, key);
            values.put(FeedEntry.COLUMN_HEART, annonceClass.getHeart());
            values.put(FeedEntry.COLUMN_TITLE, annonceClass.getTitle());
            values.put(FeedEntry.COLUMN_POST_CODE, annonceClass.getPostcode());
            values.put(FeedEntry.COLUMN_TIME_STAMP, time);
            values.put(FeedEntry.COLUMN_PRICE, annonceClass.getPrix());

            db.update(FeedEntry.TABLE_NAME, values, FeedEntry.COLUMN_KEY + "=?", new String[]{key});

        }
        cursor.close();
    }
    public void fillInlist(List<MyAnnonceClass> annonceClassList)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        annonceClassList.clear();
        Cursor cursor = db.rawQuery("select * from " + FeedEntry.TABLE_NAME, null);

        while (cursor.moveToNext()) {

            MyAnnonceClass annonceClass = new MyAnnonceClass(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(5), cursor.getString(4), cursor.getString(6));
            annonceClassList.add(0,annonceClass);

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
            return cursor.getString(3);
        }
        cursor.close();
        return "";
    }
  /*  public void UpdateSyncTime(String nom, String inside)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_KEY, nom);
        values.put(FeedEntry.COLUMN_INSIDE, inside);
        db.update(TABLE_NAME, values, FeedEntry.COLUMN_KEY + "=?", new String []{nom});
    }*/
    /*public String returnTimeifexist(String nom)
    {
        if (checkAlreadyExist(nom))
        {
            return getInside(nom);
        }
        else
        {
            return "not";
        }
    }*/


    public void dropDB(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void delete(String key)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();



        db.delete(FeedEntry.TABLE_NAME, FeedEntry.COLUMN_KEY + "=?", new String[]{key});
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns
    {
        static final String TABLE_NAME = "Checkcom";
        static final String COLUMN_KEY = "uniquekey";
        static final String COLUMN_HEART = "HEART";
        static final String COLUMN_TITLE = "TITLE";
        static final String COLUMN_POST_CODE = "MANGER";
        static final String COLUMN_TIME_STAMP = "Time";
        static final String COLUMN_TYPE = "Type";
        static final String COLUMN_PRICE = "PRIX";




    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    FeedEntry.COLUMN_KEY + " TEXT PRIMARY KEY," +
                    FeedEntry.COLUMN_TITLE + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_HEART + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_POST_CODE + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_PRICE + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_TIME_STAMP + TEXT_TYPE + "," +
                    FeedEntry.COLUMN_TYPE + TEXT_TYPE +" )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    private static class AnnonceHelper extends SQLiteOpenHelper
    {
        // If you change the database schema, you must increment the database version.
        static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "annonce.db";

        AnnonceHelper(Context context) {
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
