package com.example.alejandrovidales.syncmysqltosqlite;

/**
 * Created by alejandrovidales on 6/23/15.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBControllerNewAsset extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SQLiteAssets3";

    // Contacts table name
    public static final String TABLE_NAME = "asset";

    // Contacts Table Columns names
    public static final String KEY_TAG = "tag";
    public static final String KEY_TYPE = "type";
    public static final String KEY_BRAND = "brand";
    public static final String KEY_MODEL = "model";
    public static final String KEY_SERIAL_NUMBER = "serialnumber";
    public static final String KEY_PROPERTY = "property";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_FLOOR = "floor";
    public static final String KEY_ROOM = "room";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_STATUS = "updateStatus";

    public DBControllerNewAsset(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CreateTable = "CREATE TABLE " + TABLE_NAME + " ( "
                + KEY_TAG + " INTEGER PRIMARY KEY,"
                + KEY_TYPE + " TEXT, " + KEY_BRAND + " TEXT, " + KEY_MODEL + " TEXT, "
                + KEY_SERIAL_NUMBER + " TEXT, " + KEY_PROPERTY + " TEXT, " + KEY_LOCATION + " TEXT, "
                + KEY_FLOOR + " TEXT, " + KEY_ROOM + " TEXT, " + KEY_OWNER + " TEXT, " + KEY_STATUS + " TEXT" + ");";
        db.execSQL(CreateTable);
    }

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllAssets() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM asset";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("tag", cursor.getString(0));
                map.put("type", cursor.getString(1));
                map.put("brand", cursor.getString(2));
                map.put("model", cursor.getString(3));
                map.put("serialnumber", cursor.getString(4));
                map.put("property", cursor.getString(5));
                map.put("location", cursor.getString(6));
                map.put("floor", cursor.getString(7));
                map.put("room", cursor.getString(8));
                map.put("owner", cursor.getString(9));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }
    /**
     * Compose JSON out of SQLite records
     * @return
     */
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM asset where updateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("serialnumber", cursor.getString(4));
                map.put("tag", cursor.getString(0));
                map.put("type", cursor.getString(1));
                map.put("brand", cursor.getString(2));
                map.put("model", cursor.getString(3));
                map.put("property", cursor.getString(5));
                map.put("location", cursor.getString(6));
                map.put("floor", cursor.getString(7));
                map.put("room", cursor.getString(8));
                map.put("owner", cursor.getString(9));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }
    /**
     * Get Sync status of SQLite
     * @return
     */
    public String getSyncStatus(){
        String msg = null;
        if(this.dbSyncCount() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync needed";
        }
        return msg;
    }
    /**
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public int dbSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM asset where updateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }
    /**
     * Update Sync status against each User ID
     * @param serialnumber
     * @param status
     */
    public void updateSyncStatus(String id, String status){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update asset set updateStatus = '"+ status +"' where serialnumber="+"'"+ id +"'";
        Log.d("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public List<String> getRowAssets(){
        List<String> RowList = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM asset";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                RowList.add(cursor.getString(0));
                RowList.add(cursor.getString(1));
                RowList.add(cursor.getString(2));
                RowList.add(cursor.getString(3));
                RowList.add(cursor.getString(4));
                RowList.add(cursor.getString(5));
                RowList.add(cursor.getString(6));
                RowList.add(cursor.getString(7));
                RowList.add(cursor.getString(8));
                RowList.add(cursor.getString(9));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return RowList;
    }


    /** public ArrayList<HashMap<String, String>> getRowAssets(String serialnumber) {
        ArrayList<HashMap<String, String>> RowList;
        RowList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM asset where serialnumber = '"+ serialnumber +"'";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("tag", cursor.getString(0));
                map.put("type", cursor.getString(1));
                map.put("brand", cursor.getString(2));
                map.put("model", cursor.getString(3));
                map.put("serialnumber", cursor.getString(4));
                map.put("property", cursor.getString(5));
                map.put("location", cursor.getString(6));
                map.put("floor", cursor.getString(7));
                map.put("room", cursor.getString(8));
                map.put("owner", cursor.getString(9));
                RowList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return RowList;
    }
    */
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }
}
