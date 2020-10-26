package com.comp322olivet.tipcalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/*
_id INTEGER
bill_date INTEGER
bill_amount REAL
tip_percent REAL
 */

public class TipDB {

    //name and version of the database
    public static final String  DB_NAME = "tipDB.db";
    public static final int     DB_VERSION = 1;

    //name of table
    public static final String  TIP_TABLE = "tip";

    //tip id column name and column number
    public static final String  TIP_ID = "_id";
    public static final int     TIP_ID_COL = 0;

    //tip date column name and column number
    public static final String BILL_DATE = "bill_date";
    public static final int    BILL_DATE_COL = 1;

    //tip bill amount column name and column number
    public static final String BILL_AMOUNT = "bill_amount";
    public static final int    BILL_AMOUNT_COL = 2;

    //tip percentage column name and column number
    public static final String TIP_PERCENT = "tip_percent";
    public static final int    TIP_PERCENT_COL = 3;

    //used to create the table
    public static final String CREATE_TIP_TABLE =
            "CREATE TABLE " + TIP_TABLE + " (" +
            TIP_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            BILL_DATE   + " INTEGER, " +
            BILL_AMOUNT + " REAL, " +
            TIP_PERCENT + " REAL);";

    //deletes tip table
    public static final String DROP_TIP_TABLE =
            "DROP TABLE IF EXISTS " + TIP_TABLE;


    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name,
                        CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            //creates table
            db.execSQL(CREATE_TIP_TABLE);

            //enters first two default rows
            db.execSQL("INSERT INTO tip VALUES (1, 0, 40.60, .15)");
            db.execSQL("INSERT INTO tip VALUES (2, 0, 34.54, .21)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.d("Tip", "Upgrading from version " +
                    oldVersion + " to " + newVersion);

            db.execSQL(TipDB.DROP_TIP_TABLE);
            onCreate(db);
        }
    }

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    //constructor
    public TipDB(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWritableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null) {
            db.close();
        }
    }

    //returns all tips
    public ArrayList<Tip> getTips() {

        ArrayList<Tip> tips = new ArrayList<>();

        openReadableDB();
        Cursor cursor = db.query(TIP_TABLE, null, null,
                null, null, null, null);

        //creates Tip object from each row and adds to ArrayList
        while (cursor.moveToNext()) {
            Tip tip = new Tip(cursor.getLong(TIP_ID_COL), cursor.getLong(BILL_DATE_COL),
                    cursor.getFloat(BILL_AMOUNT_COL), cursor.getFloat(TIP_PERCENT_COL));

            tips.add(tip);
        }

        if (cursor != null) {
            cursor.close();
        }
        closeDB();

        return tips;
    }

    //returns most recent Tip object
    public Tip getRecentTip() {

        openReadableDB();
        Cursor cursor = db.query(TIP_TABLE, null, null,
                null, null, null, null);

        //so that we get the last row that was entered
        cursor.moveToLast();

        //creates new Tip object with data from the current row
        Tip tip = new Tip(cursor.getLong(TIP_ID_COL), cursor.getLong(BILL_DATE_COL),
                cursor.getFloat(BILL_AMOUNT_COL), cursor.getFloat(TIP_PERCENT_COL));

        if (cursor != null) {
            cursor.close();
        }
        closeDB();

        return tip;
    }

    //inserts Tip object argument into database
    public void saveTip(Tip tip) {
        ContentValues cv = new ContentValues();
        cv.put(BILL_DATE, tip.getDateMillis());
        cv.put(BILL_AMOUNT, tip.getBillAmount());
        cv.put(TIP_PERCENT, tip.getTipPercent());

        this.openWritableDB();
        db.insert(TIP_TABLE, null, cv);
        this.closeDB();
    }

    //returns the average tip percentage of all tips
    public float getAverage() {

        //SQLite command to get average of all tip percentages
        String sqlAverageTipPercent = "SELECT AVG(" + TIP_PERCENT + ") FROM " + TIP_TABLE;

        float average = 0;
        openWritableDB();
        Cursor cursor = db.rawQuery(sqlAverageTipPercent, null);

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            average = cursor.getFloat(TIP_ID_COL);
            cursor.close();
        }

        return average;
    }

}
