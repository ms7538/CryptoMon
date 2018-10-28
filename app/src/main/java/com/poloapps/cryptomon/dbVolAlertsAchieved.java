package com.poloapps.cryptomon;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

public class dbVolAlertsAchieved extends SQLiteOpenHelper {

    private static final int    DATABASE_VERSION     = 1;
    private static final String DATABASE_NAME        = "cm_vol_ach1.db";

    private static final String TABLE_CM_ACH_ALERTS2 = "achieved_vol_alerts";
    private static final String COLUMN_ID            = "_id";
    private static final String COLUMN_CRYPTOSYMB    = "cryptosymb";
    private static final String COLUMN_CURRSYMB      = "currsymbol";
    private static final String COLUMN_THRESH_BRK    = "thresh_breaker";
    private static final String COLUMN_THRESH_VAL    = "thresh_value";
    private static final String COLUMN_BREAKER_CHCK  = "breaker_check";
    private static final String COLUMN_ACH_MIN       = "min_achieved";

    dbVolAlertsAchieved(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = " CREATE TABLE " + TABLE_CM_ACH_ALERTS2 + " ( " + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CRYPTOSYMB + " TEXT, "
                + COLUMN_CURRSYMB + " TEXT, " + COLUMN_THRESH_BRK + " BLOB, "
                + COLUMN_THRESH_VAL + " BLOB, " + COLUMN_BREAKER_CHCK + " INTEGER, "
                + COLUMN_ACH_MIN + " INTEGER "+ " ); ";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_CM_ACH_ALERTS2);
        onCreate(db);
    }

    void addVolAchAlert(
            String cryptoSymb, double breaker, double threshold, int check, int sysMins) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CRYPTOSYMB,   cryptoSymb);
        values.put(COLUMN_THRESH_BRK,   breaker);
        values.put(COLUMN_THRESH_VAL,   threshold);
        values.put(COLUMN_BREAKER_CHCK, check);
        values.put(COLUMN_CURRSYMB,     "$");
        values.put(COLUMN_ACH_MIN,      sysMins);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CM_ACH_ALERTS2, null, values);
        db.close();
    }

    void removeVolAchAlert(String cryptoSymb) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CM_ACH_ALERTS2 + " WHERE " + COLUMN_CRYPTOSYMB + "=\""
                + cryptoSymb + "\";");
    }

    String dbEntries(){
        StringBuilder dbString = new StringBuilder();
        SQLiteDatabase db      = getWritableDatabase();
        String query           = "SELECT * FROM " + TABLE_CM_ACH_ALERTS2 + " WHERE 1";
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

    String getThreshVal(String in){

        StringBuilder dbString = new StringBuilder();
        SQLiteDatabase db      = getWritableDatabase();
        String query           = "SELECT * FROM " + TABLE_CM_ACH_ALERTS2 + " WHERE 1";
        Cursor c               = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")).equals(in)){
                dbString.append(c.getString(c.getColumnIndex("thresh_value")));
            }
            c.moveToNext();
        }
        c.close();
        db.close();

        return dbString.toString();
    }

    String getThreshBrk(String in){

        StringBuilder dbString = new StringBuilder();
        SQLiteDatabase db      = getWritableDatabase();
        String query           = "SELECT * FROM " + TABLE_CM_ACH_ALERTS2 + " WHERE 1";
        Cursor c               = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")).equals(in)){
                dbString.append(c.getString(c.getColumnIndex("thresh_breaker")));
            }
            c.moveToNext();
        }
        c.close();
        db.close();

        return dbString.toString();
    }

    String getColumnBreakerChck(String in){

        StringBuilder dbString = new StringBuilder();
        SQLiteDatabase db      = getWritableDatabase();
        String query           = "SELECT * FROM " + TABLE_CM_ACH_ALERTS2 + " WHERE 1";
        Cursor c               = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")).equals(in)){
                dbString.append(c.getString(c.getColumnIndex("breaker_check")));
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbString.toString();
    }

    String getAchievedTimeStamp(String in){

        StringBuilder dbString = new StringBuilder();
        SQLiteDatabase db      = getWritableDatabase();
        String query           = "SELECT * FROM " + TABLE_CM_ACH_ALERTS2 + " WHERE 1";
        Cursor c               = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")).equals(in)){
                dbString.append(c.getString(c.getColumnIndex("min_achieved")));
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbString.toString();
    }

    Boolean alertExists(String in){

        Boolean exists = false;
        SQLiteDatabase db      = getWritableDatabase();
        String query           = "SELECT * FROM " + TABLE_CM_ACH_ALERTS2 + " WHERE 1";
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

    void deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CM_ACH_ALERTS2 + " WHERE 1");
    }
}
