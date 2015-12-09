package com.example.tud23846.stockwatcher.db;

import android.provider.BaseColumns;

/**
 * Created by tud23846 on 12/3/2015.
 */
public class StockDBContract {


    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + StockEntry.TABLE_NAME + " (" +
                    StockEntry.COLUMN_NAME_SYMBOL + TEXT_TYPE + " collate nocase " + COMMA_SEP +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StockEntry.TABLE_NAME;

    public static abstract class StockEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_SYMBOL = "symbol";
    }
}
