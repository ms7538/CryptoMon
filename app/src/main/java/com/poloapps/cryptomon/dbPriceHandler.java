package com.poloapps.cryptomon;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;


public class dbPriceHandler extends SQLiteOpenHelper {

    private static final int    DATABASE_VERSION  = 9;
    private static final String DATABASE_NAME     = "cryptomon.db";

    private static final String TABLE_CM_ALERTS   = "price_alerts";
    private static final String COLUMN_ID         = "_id";
    private static final String COLUMN_CRYPTOSYMB = "cryptosymb";
    private static final String COLUMN_CURRSYMB   = "currsymbol";
    private static final String COLUMN_THRESH_IND = "price_indicator";
    private static final String COLUMN_THRESH_VAL = "price_value";

    dbPriceHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = " CREATE TABLE " + TABLE_CM_ALERTS + " ( " + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CRYPTOSYMB + " TEXT, "
                + COLUMN_CURRSYMB + " TEXT, "+  COLUMN_THRESH_IND + " INTEGER, "
                                                            + COLUMN_THRESH_VAL + " BLOB " + " ); ";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_CM_ALERTS);
        onCreate(db);
    }

    void addPriceAlert(String cryptoSymb, int threshold_check, double price_value) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CRYPTOSYMB, cryptoSymb);
        values.put(COLUMN_CURRSYMB,   "$");
        values.put(COLUMN_THRESH_IND, threshold_check);
        values.put(COLUMN_THRESH_VAL, price_value);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CM_ALERTS, null, values);
        db.close();
    }

    void deleteAlert(String cryptoSymb) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CM_ALERTS + " WHERE " + COLUMN_CRYPTOSYMB + "=\""
                + cryptoSymb + "\";");
    }

    void deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CM_ALERTS + " WHERE 1");
    }

    String dbToString(){
        StringBuilder dbString = new StringBuilder();
        SQLiteDatabase db      = getWritableDatabase();
        String query           = "SELECT * FROM " + TABLE_CM_ALERTS + " WHERE 1";
        Cursor c               = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")) != null){
                dbString.append(c.getString(c.getColumnIndex("cryptosymb")));
                dbString.append("\n");
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbString.toString();
    }
    String getThresh_Check(String in){
        StringBuilder dbString = new StringBuilder();
        SQLiteDatabase db      = getWritableDatabase();
        String query           = "SELECT * FROM " + TABLE_CM_ALERTS + " WHERE 1";
        Cursor c               = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")).equals(in)){
                dbString.append(c.getString(c.getColumnIndex("price_indicator")));
            }
            c.moveToNext();
        }
        c.close();
        db.close();

        return dbString.toString();
    }

    String getPrice_Val(String in){
        StringBuilder dbString = new StringBuilder();
        SQLiteDatabase db      = getWritableDatabase();
        String query           = "SELECT * FROM " + TABLE_CM_ALERTS + " WHERE 1";
        Cursor c               = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")).equals(in)){
                dbString.append(c.getString(c.getColumnIndex("price_value")));
            }
            c.moveToNext();
        }
        c.close();
        db.close();

        return dbString.toString();
    }
}