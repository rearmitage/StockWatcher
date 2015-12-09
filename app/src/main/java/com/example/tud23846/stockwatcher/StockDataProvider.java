package com.example.tud23846.stockwatcher;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.example.tud23846.stockwatcher.db.StockDBContract;
import com.example.tud23846.stockwatcher.db.StockDBHelper;

/**
 * Created by tud23846 on 12/3/2015.
 */
public class StockDataProvider extends ContentProvider {

    SQLiteDatabase db;
    StockDBHelper mDbHelper;
    Stock stock;

    boolean pauseFlag;

    public StockDataProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new StockDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Log.d("Selection", selection);
        Log.d("Arguments", selectionArgs[0]);

        Cursor c = getStockCursor(selection, selectionArgs);
        if (!(c.getCount() > 0)) {
            pauseFlag = true;
            downloadStockData(selectionArgs[0]);
            while (pauseFlag);
            saveData(stock.getSymbol());
            c = getStockCursor(selection, selectionArgs);
        }
        Log.d("Row count", String.valueOf(c.getCount()));
        return c;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Search for stock data in database
    private Cursor getStockCursor(String selection, String[] selectionArgs){
        db = mDbHelper.getReadableDatabase();

        Cursor c = db.query(
                StockDBContract.StockEntry.TABLE_NAME
                , new String[]{"_id", StockDBContract.StockEntry.COLUMN_NAME_COMPANY}
                , selection
                , selectionArgs
                , null
                , null
                , null);
        c.moveToNext();

        return c;
    }

    // Download stock data
    public void downloadStockData(final String stockSymbol) {

        Thread t = new Thread() {

            public void run() {

                URL stockQuoteUrl;

                try

                {

                    stockQuoteUrl = new URL("http://finance.yahoo.com/webservice/v1/symbols/" + stockSymbol + "/quote?format=json");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    stockQuoteUrl.openStream()));

                    String response = "", tmpResponse;

                    tmpResponse = reader.readLine();
                    while (tmpResponse != null) {
                        response = response + tmpResponse;
                        tmpResponse = reader.readLine();
                    }

                    JSONObject stockObject = new JSONObject(response);
                    Message msg = Message.obtain();
                    msg.obj = stockObject;

                    stockHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    Handler stockHandler = new Handler (new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                JSONObject stockObject = (JSONObject) msg.obj;
                stock = new Stock(stockObject.getJSONObject("list")
                        .getJSONArray("resources")
                        .getJSONObject(0)
                        .getJSONObject("resource")
                        .getJSONObject("fields"));
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                pauseFlag = false;
            }
            return true;
        }
    });


    // Save stock data to database
    private void saveData(String symbol){

        // Gets the data repository in write mode
        db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(StockDBContract.StockEntry.COLUMN_NAME_SYMBOL, symbol);
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                StockDBContract.StockEntry.TABLE_NAME,
                null,
                values);

        if (newRowId > 0) {
            Log.d("Stock data saved ", newRowId + " - " + symbol);
        } else {
            Log.d("Stock data NOT saved ", newRowId + " - " + symbol);
        }

    }
}
