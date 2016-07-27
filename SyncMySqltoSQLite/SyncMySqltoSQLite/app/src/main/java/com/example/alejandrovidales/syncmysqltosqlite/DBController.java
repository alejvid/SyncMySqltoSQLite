//database class to control the creation of tables and db query for SQlite

package com.example.alejandrovidales.syncmysqltosqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alejandrovidales on 5/24/15.
 */
public class DBController  extends SQLiteOpenHelper {

    public DBController(Context applicationcontext) {
        super(applicationcontext, "user12.db", null, 1);
    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE users ( userId INTEGER, userName TEXT)";
        database.execSQL(query);
        String query1;
        query1 = "CREATE TABLE brand ( brandId INTEGER, brandName TEXT)";
        database.execSQL(query1);
        String query2;
        query2 = "CREATE TABLE model ( modelId INTEGER, modelName TEXT)";
        database.execSQL(query2);
        String query3;
        query3 = "CREATE TABLE location ( locationId INTEGER, locationName TEXT)";
        database.execSQL(query3);
        String query4;
        query4 = "CREATE TABLE property ( propertyId INTEGER, propertyName TEXT)";
        database.execSQL(query4);
        String query5;
        query5 = "CREATE TABLE owner ( ownerId INTEGER, ownerName TEXT)";
        database.execSQL(query5);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS users";
        database.execSQL(query);
        onCreate(database);
        String query1;
        query1 = "DROP TABLE IF EXISTS brand";
        database.execSQL(query1);
        onCreate(database);
        String query2;
        query2 = "DROP TABLE IF EXISTS model";
        database.execSQL(query2);
        onCreate(database);
        String query3;
        query3 = "DROP TABLE IF EXISTS location";
        database.execSQL(query3);
        onCreate(database);
        String query4;
        query4 = "DROP TABLE IF EXISTS property";
        database.execSQL(query4);
        onCreate(database);
        String query5;
        query5 = "DROP TABLE IF EXISTS owner";
        database.execSQL(query5);
        onCreate(database);
    }

    /**
     * Inserts User into SQLite DB
     *
     * @param queryValues
     */
    public void insertUser(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", queryValues.get("userId"));
        values.put("userName", queryValues.get("userName"));
        database.insert("users", null, values);
        database.close();
    }

    /**
     * Inserts Brand into SQLite DB
     *
     * @param queryValues
     */
    public void insertBrand(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("brandId", queryValues.get("brandId"));
        values.put("brandName", queryValues.get("brandName"));
        database.insert("brand", null, values);
        database.close();
    }

    /**
     * Inserts Model into SQLite DB
     *
     * @param queryValues
     */
    public void insertModel(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("modelId", queryValues.get("modelId"));
        values.put("modelName", queryValues.get("modelName"));
        database.insert("model", null, values);
        database.close();
    }

    /**
     * Inserts Location into SQLite DB
     *
     * @param queryValues
     */
    public void insertLocation(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("locationId", queryValues.get("locationId"));
        values.put("locationName", queryValues.get("locationName"));
        database.insert("location", null, values);
        database.close();
    }

    /**
     * Inserts Property into SQLite DB
     *
     * @param queryValues
     */
    public void insertProperty(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("propertyId", queryValues.get("propertyId"));
        values.put("propertyName", queryValues.get("propertyName"));
        database.insert("property", null, values);
        database.close();
    }

    /**
     * Inserts Owner into SQLite DB
     *
     * @param queryValues
     */
    public void insertOwner(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ownerId", queryValues.get("ownerId"));
        values.put("ownerName", queryValues.get("ownerName"));
        database.insert("owner", null, values);
        database.close();
    }


    /**
     * Get list of Users from SQLite DB as Array List
     *
     * @return
     */
    public List<String> getAllLabels(){
        List<String> labels = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM users";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return labels;
    }
    public List<String> getAllBrands(){
        List<String> labels = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM brand";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return labels;
    }

    public List<String> getAllModels(){
        List<String> labels = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM model";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return labels;
    }

    public List<String> getAllLocations(){
        List<String> labels = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM location";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return labels;
    }

    public List<String> getAllProperties(){
        List<String> labels = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM property";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return labels;
    }

    public List<String> getAllOwners(){
        List<String> labels = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM owner";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return labels;
    }

}