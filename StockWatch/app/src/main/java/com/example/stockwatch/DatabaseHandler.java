package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "StockAppDB";

    // DB Table Name
    private static final String TABLE_NAME = "StockWatchTable";

    ///DB Columns
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";


    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null)";

    private static  final String EXIST = "select" + SYMBOL + "from" + TABLE_NAME + "where" + SYMBOL + " =?";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
        //Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: Making New DB");
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public HashMap<String, String> loadStocks()
    {
        HashMap<String,String> hm  = new HashMap<String,String>();
        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{SYMBOL,COMPANY}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order



        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                hm.put(symbol,company);
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: DONE");

        return hm;
    }

    public void addStocks(ArrayList<Stock> stock) {

        Log.d(TAG,"addStock: Adding " + stock.get(0).getSymbol());
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.get(0).getSymbol());
        values.put(COMPANY, stock.get(0).getName());

        database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: Add complete");
    }


    public void deleteStock(String symbol) {
        Log.d(TAG, "deleteStock: Deleting Stock " + symbol);

        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{symbol});

        Log.d(TAG, "deleteStock: " + cnt);
    }

    public int checkAlreadyExist(String symbol)
    {
        int flag=0;
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME + " where " + SYMBOL + " =?",new String[]{symbol});

        if(cursor != null){
            cursor.moveToFirst();
            if (cursor.getCount() > 0)
               flag =0;
            else
                flag=1;

        }
        cursor.close();
    return flag;
    }

    public void shutDown() {
        database.close();
    }
}
