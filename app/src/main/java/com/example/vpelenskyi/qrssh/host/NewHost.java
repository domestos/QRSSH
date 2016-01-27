package com.example.vpelenskyi.qrssh.host;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.example.vpelenskyi.qrssh.MainQRSSH;
import com.example.vpelenskyi.qrssh.R;
import com.example.vpelenskyi.qrssh.database.Data;

/**
 * Created by v.pelenskyi on 22.12.2015.
 */
public class NewHost extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "QRSSH_LOG";

    private EditText alias, host, port, username, password;
    private Button btnSaveHost;
    private RadioGroup rgOS;
    private RadioButton rbWindows, rbUbuntu;
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
        rgOS = (RadioGroup) findViewById(R.id.rgOS);
        rbWindows = (RadioButton) findViewById(R.id.rbWindows);
        rbUbuntu = (RadioButton) findViewById(R.id.rbUbntu);
    }

    public int chekedOS() {
        switch (rgOS.getCheckedRadioButtonId()) {
            case R.id.rbWindows:
                return Host.OS_WINDOWS;
            case R.id.rbUbntu:
                return Host.OS_UBUNTU;
            default:
                return Host.OS_WINDOWS;
        }
    }

    private boolean validation() {
        if (TextUtils.isEmpty(alias.getText().toString().trim())) {
            alias.setError("can't be empty");
            return false;
        }
        if (TextUtils.isEmpty(host.getText().toString().trim())) {
            host.setError("can't be empty");
            return false;
        }
        if (TextUtils.isEmpty(port.getText().toString().trim())) {
            port.setError("can't be empty\n usually default port  uses 22 ");
            return false;
        }
        if (TextUtils.isEmpty(username.getText().toString().trim())) {
            username.setError("can't be empty");
            return false;
        }
        if (TextUtils.isEmpty(password.getText().toString().trim())) {
            password.setError("can't be empty");
            return false;
        }

        return true;
    }

    private void addHost() {
        if (validation()) {

            db = new Data(this);
            db.open();

            long l = db.insertHost(
                    alias.getText().toString(),
                    chekedOS(),
                    host.getText().toString(),
                    Integer.valueOf(port.getText().toString()),
                    username.getText().toString(),
                    password.getText().toString(), db.ACTIVE);
            db.close();
            Log.i(TAG, "insert : " + l);

            onBackPressed();
        }


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

    }

}
