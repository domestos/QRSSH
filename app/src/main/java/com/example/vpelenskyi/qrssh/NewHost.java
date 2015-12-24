package com.example.vpelenskyi.qrssh;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by v.pelenskyi on 22.12.2015.
 */
public class NewHost extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "QRSSH_LOG";
    private TextInputLayout tilAlias, tilHost, tilPort, tilUsername, tilPassword;
    private EditText alias, host, port, username, password;
    private Button btnSaveHost;
    private String os = "";
    private HostDB hodtDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_host_activity);


        // TOOLBAR
        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setHomeButtonEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);

        toolbar.setTitle("Add New HOST");

        // INIT TIL
        intTextInputLayout();

        // INIT DB
        hodtDB = new HostDB(this, "hosts", null, 1);

        //CLICK BUTTON

        btnSaveHost = (Button) findViewById(R.id.btnSaveHost);
        btnSaveHost.setOnClickListener(this);

    }

    private void addHost() {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = hodtDB.getWritableDatabase();

        cv.put("alias_db", alias.getText().toString());
        cv.put("host_db", host.getText().toString());
        cv.put("port_db", Integer.valueOf(port.getText().toString()));
        cv.put("username_db", username.getText().toString());
        cv.put("password_db", password.getText().toString());

        long rowID = db.insert("hosts", null, cv);
        Log.d(TAG, "row inserted, ID = " + rowID);

        Toast.makeText(getApplicationContext(), cv.toString(), Toast.LENGTH_SHORT).show();

    }

    private void intTextInputLayout() {

        tilAlias = (TextInputLayout) findViewById(R.id.aliasInputLayout);
        alias = (EditText) tilAlias.findViewById(R.id.alias);
        tilAlias.setHint(getString(R.string.etAlias));

        tilHost = (TextInputLayout) findViewById(R.id.hostInputLayout);
        host = (EditText) tilHost.findViewById(R.id.host);
        tilHost.setHint(getString(R.string.hintHost));

        tilPort = (TextInputLayout) findViewById(R.id.portInputLayout);
        port = (EditText) tilPort.findViewById(R.id.port);
        tilPort.setHint(getString(R.string.hintPort));

        tilUsername = (TextInputLayout) findViewById(R.id.usernameInputLayout);
        username = (EditText) tilUsername.findViewById(R.id.username);
        tilUsername.setHint(getString(R.string.hintUsername));

        tilPassword = (TextInputLayout) findViewById(R.id.passwordInputLayout);
        password = (EditText) tilPassword.findViewById(R.id.password);
        tilPassword.setHint(getString(R.string.hintPassword));

    }

    @Override
    public void onClick(View v) {
        addHost();
        onBackPressed();
    }

    class HostDB extends SQLiteOpenHelper {

        public HostDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "-- creat date base --");
            db.execSQL(

                    "CREATE TABLE hosts ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "alias_db TEXT,"
                            + "host_db TEXT,"
                            + "port_db INTEGER,"
                            + "username_db TEXT,"
                            + "password_db TEXT"
                            + ");"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "-- upgate date base --");
        }
    }
}
