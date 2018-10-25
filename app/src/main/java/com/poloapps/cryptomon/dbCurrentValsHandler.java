package com.poloapps.cryptomon;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

public class dbCurrentValsHandler extends SQLiteOpenHelper {
    private static final int    DATABASE_VERSION  = 5;
    private static final String DATABASE_NAME     = "cryptomon3b.db";

    private static final String TABLE_CM_CVALS    = "current_vals";
    private static final String COLUMN_ID         = "_id";
    private static final String COLUMN_CRYPTOSYMB = "cryptosymb";
    private static final String COLUMN_CURRSYMB   = "currsymbol";
    private static final String COLUMN_CURR_PRICE = "price_current";
    private static final String COLUMN_CURR_VOL   = "volume_current";
    private static final String COLUMN_CURR_HOUR  = "time_current";

    dbCurrentValsHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = " CREATE TABLE " + TABLE_CM_CVALS + " ( " + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CRYPTOSYMB + " TEXT, "
                + COLUMN_CURRSYMB + " TEXT, " + COLUMN_CURR_PRICE + " INTEGER, "
                + COLUMN_CURR_VOL + " BLOB, " +  COLUMN_CURR_HOUR  + " INTEGER "   + "  ); ";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_CM_CVALS);
        onCreate(db);
    }

    void addCurrentVals(String cryptoSymb, double pr_value, double vol_value, int day) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CRYPTOSYMB, cryptoSymb);
        values.put(COLUMN_CURR_PRICE, pr_value);
        values.put(COLUMN_CURR_VOL,   vol_value);
        values.put(COLUMN_CURRSYMB,   "$");
        values.put(COLUMN_CURR_HOUR,  day);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CM_CVALS, null, values);
        db.close();
    }
    void deleteEntry(String cryptoSymb) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CM_CVALS + " WHERE " + COLUMN_CRYPTOSYMB + "=\""
                + cryptoSymb + "\";");
    }

    String currentPrice(String cryptoId){
        StringBuilder dbCurrPrice = new StringBuilder();
        SQLiteDatabase db         = getWritableDatabase();
        String query              = "SELECT * FROM " + TABLE_CM_CVALS + " WHERE 1";
        Cursor c                  = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")).equals(cryptoId)){
                dbCurrPrice.append(c.getString(c.getColumnIndex("price_current")));
                dbCurrPrice.append("\n");
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbCurrPrice.toString();
    }
    String currentVol(String cryptoId){
        StringBuilder dbCurrVol = new StringBuilder();
        SQLiteDatabase db       = getWritableDatabase();
        String query            = "SELECT * FROM " + TABLE_CM_CVALS + " WHERE 1";
        Cursor c                = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")).equals(cryptoId)){
                dbCurrVol.append(c.getString(c.getColumnIndex("volume_current")));
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbCurrVol.toString();
    }

    public Boolean Exists(String in){
        Boolean exists         = false;
        SQLiteDatabase db      = getWritableDatabase();
        String query           = "SELECT * FROM " + TABLE_CM_CVALS + " WHERE 1";
        Cursor c               = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")).equals(in)){
                exists = true;
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return exists;
    }
    String currentHour(String cryptoId){
        StringBuilder dbCurrVol = new StringBuilder();
        SQLiteDatabase db       = getWritableDatabase();
        String query            = "SELECT * FROM " + TABLE_CM_CVALS + " WHERE 1";
        Cursor c                = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")).equals(cryptoId)){
                dbCurrVol.append(c.getString(c.getColumnIndex("time_current")));
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbCurrVol.toString();
    }
}
