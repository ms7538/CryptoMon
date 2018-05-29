package com.poloapps.cryptomon;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;


public class dbPriceAlertsAchieved extends SQLiteOpenHelper {

    private static final int    DATABASE_VERSION     = 1;
    private static final String DATABASE_NAME        = "cryptomon4.db";

    private static final String TABLE_CM_ACH_ALERTS  = "achieved_price_alerts";
    private static final String COLUMN_ID            = "_id";
    private static final String COLUMN_CRYPTOSYMB    = "cryptosymb";
    private static final String COLUMN_THRESH_BRK    = "thresh_breaker";
    private static final String COLUMN_THRESH_VAL    = "thresh_value";



    dbPriceAlertsAchieved(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = " CREATE TABLE " + TABLE_CM_ACH_ALERTS + " ( " + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CRYPTOSYMB + " TEXT, " +
                COLUMN_THRESH_BRK + " BLOB, " + COLUMN_THRESH_VAL + " BLOB " + " ); ";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_CM_ACH_ALERTS);
        onCreate(db);
    }

    public void addPriceAchAlert(String cryptoSymb, double breaker, double threshold) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CRYPTOSYMB, cryptoSymb);
        values.put(COLUMN_THRESH_BRK, breaker);
        values.put(COLUMN_THRESH_VAL, threshold);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CM_ACH_ALERTS, null, values);
        db.close();
    }

    public void removePAAlert(String cryptoSymb) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CM_ACH_ALERTS + " WHERE " + COLUMN_CRYPTOSYMB + "=\""
                + cryptoSymb + "\";");
    }

    public String dbToString(){
        StringBuilder dbString = new StringBuilder();
        SQLiteDatabase db      = getWritableDatabase();
        String query           = "SELECT * FROM " + TABLE_CM_ACH_ALERTS + " WHERE 1";
        Cursor c               = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("cryptosymb")) != null){
                dbString.append(c.getString(c.getColumnIndex("cryptosymb")));
                dbString.append(" passed ");
                dbString.append(c.getString(c.getColumnIndex("thresh_value")));
                dbString.append(" at ");
                dbString.append(c.getString(c.getColumnIndex("thresh_breaker")));
                dbString.append("\n");
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return dbString.toString();
    }

}