package com.example.vpelenskyi.qrssh.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by varenik on 25.01.16.
 */
public class Data {

    DBHelpelr dbHelpelr;
    SQLiteDatabase db;
    public final Context context;

    private String TAG = "log_qrssh";

    public Data(Context context) {
        this.context = context;
    }

    private final String DB_DATA = "db_host";
    private final int DB_VERSION = 1;
    private final String DB_TABLE = "host";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ALIAS = "alias";
    public static final String COLUMN_HOST = "host";
    public static final String COLUMN_PORT = "port";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_PASS = "pass";
    public static final String COLUMN_OS = "os";
    public static final String COLUMN_ACTIVE = "active";

    public void open() {
        dbHelpelr = new DBHelpelr(context, DB_DATA, null, DB_VERSION);
        db = dbHelpelr.getWritableDatabase();
    }

    public void close() {
        if (dbHelpelr != null) {
            dbHelpelr.close();
        }
    }

    public Cursor getAllData() {
        return db.query(DB_TABLE, null, null, null, null, null, null);
    }

    public long insertHost(String alias, int os, String host, int port, String user, String pass) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ALIAS, alias);
        cv.put(COLUMN_OS, os);
        cv.put(COLUMN_HOST, host);
        cv.put(COLUMN_PORT, port);
        cv.put(COLUMN_USER, user);
        cv.put(COLUMN_PASS, pass);
        long rowID = db.insert(DB_TABLE, null, cv);
        Log.d(TAG, "row inserted, ID = " + rowID);
        Log.d(TAG, "cv = " + cv.toString());
        Toast.makeText(context, cv.toString(), Toast.LENGTH_SHORT).show();
        // cv.clear();
        return rowID;
    }

    class DBHelpelr extends SQLiteOpenHelper {
        private final String DB_CREATE = "CREATE TABLE " + DB_TABLE + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_OS + " INTEGER, " +
                COLUMN_ALIAS + " TEXT, " +
                COLUMN_HOST + " TEXT, " +
                COLUMN_PORT + " INTEGER, " +
                COLUMN_USER + " TEXT, " +
                COLUMN_PASS + " TEXT " +
                ");";


        public DBHelpelr(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
