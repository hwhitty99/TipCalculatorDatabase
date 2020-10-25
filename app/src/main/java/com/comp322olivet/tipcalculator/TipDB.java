package com.comp322olivet.tipcalculator;

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

    public static final String  DB_NAME = "tip.db";
    public static final int     DB_VERSION = 1;

    public static final String  TIP_TABLE = "tip";

    public static final String  TIP_ID = "_id";
    public static final int     TIP_ID_COL = 0;

    public static final String BILL_DATE = "bill_date";
    public static final int    BILL_DATE_COL = 1;

    public static final String BILL_AMOUNT = "bill_amount";
    public static final int    BILL_AMOUNT_COL = 2;

    public static final String TIP_PERCENT = "tip_percent";
    public static final int    TIP_PERCENT_COL = 3;

    public static final String CREATE_TIP_TABLE =
            "CREATE TABLE " + TIP_TABLE + " (" +
                    TIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    BILL_DATE + " INTEGER, " +
                    BILL_AMOUNT + " REAL, " +
                    TIP_PERCENT + " REAL);";

    public static final String DROP_TIP_TABLE =
            "DROP TABLE IF EXISTS " + TIP_TABLE;

    public static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name,
                        CursorFactory factory, int version) {
            super(context, name, factory, version);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_TIP_TABLE);

            db.execSQL("INSERT INTO tip VALUES (1, 0, 40.60, .15)");

            db.execSQL("INSERT INTO tip VALUES (1, 0, 420.69, .69)");
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

    public ArrayList<Tip> getTips() {
        ArrayList<Tip> tips = new ArrayList<Tip>();
        openReadableDB();
        Cursor cursor = db.query(TIP_TABLE, null, null,
                null, null, null, null);

        while (cursor.moveToNext()) {
            Tip tip = new Tip();
            tip.setId(cursor.getInt(TIP_ID_COL));
            tip.setDateMillis(cursor.getInt(BILL_DATE_COL));
            tip.setBillAmount(cursor.getInt(BILL_AMOUNT_COL));
            tip.setTipPercent(cursor.getInt(TIP_PERCENT_COL));

            tips.add(tip);
        }

        if (cursor != null) {
            cursor.close();
        }
        closeDB();

        return tips;
    }
}
