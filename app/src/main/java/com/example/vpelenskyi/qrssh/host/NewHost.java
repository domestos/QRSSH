package com.example.vpelenskyi.qrssh.host;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vpelenskyi.qrssh.R;
import com.example.vpelenskyi.qrssh.database.Data;

/**
 * Created by v.pelenskyi on 22.12.2015.
 */
public class NewHost extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "QRSSH_LOG";

    private EditText alias, host, port, username, password;
    private Button btnSaveHost;
    private String os = "";
    Data db;

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

        //CLICK BUTTON
        btnSaveHost = (Button) findViewById(R.id.btnSaveHost);
        btnSaveHost.setOnClickListener(this);

    }


    private void addHost() {
        db = new Data(this);
        db.open();
        long l = db.insertHost(
                alias.getText().toString(),
                host.getText().toString(),
                Integer.valueOf(port.getText().toString()),
                username.getText().toString(),
                password.getText().toString());
        db.close();
        Log.i(TAG, "insert : " + l);
    }

    private void intTextInputLayout() {
        TextInputLayout tilAlias, tilHost, tilPort, tilUsername, tilPassword;

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

}