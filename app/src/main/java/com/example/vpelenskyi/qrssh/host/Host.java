package com.example.vpelenskyi.qrssh.host;

import android.database.Cursor;
import android.util.Log;
import android.widget.EditText;

import com.example.vpelenskyi.qrssh.database.Data;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by v.pelenskyi on 23.12.2015.
 */
public class Host {


    public static final int OS_WINDOWS = 0;
    public static final int OS_UBUNTU = 1;


    private String alias;
    private String host;
    private String username;
    private String password;
    private int port;
    private String os;

    public Host() {
    }

    public Host(String alias, String host, int port, String username, String password, String os) {
        this.alias = alias;
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.os = os;
    }

    public Host(String alias, String host, String username, String password, int port) {
        this.alias = alias;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }


    public Host getActiveHost(Data db) {
        if (db != null) {
            Log.i("test", db.toString());
            Cursor cursor = db.getActiveCursor();
            if (cursor != null && cursor.moveToFirst()) {
                alias = cursor.getString(cursor.getColumnIndex(Data.COLUMN_ALIAS));
                host = (cursor.getString(cursor.getColumnIndex(Data.COLUMN_HOST)));
                port = (cursor.getInt(cursor.getColumnIndex(Data.COLUMN_PORT)));
                username = (cursor.getString(cursor.getColumnIndex(Data.COLUMN_USER)));
                password = (cursor.getString(cursor.getColumnIndex(Data.COLUMN_PASS)));
                os =  (cursor.getString(cursor.getColumnIndex(Data.COLUMN_OS)));
                return this;
            }
        }
        return null;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }


    @Override
    public String toString() {
        return "Host{" +
                "alias='" + alias + '\'' +
                ", host='" + host + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", port=" + port +
                ", os='" + os + '\'' +
                '}';
    }
}
