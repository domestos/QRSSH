package com.example.vpelenskyi.qrssh.host;

import android.content.Intent;
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


import com.example.vpelenskyi.qrssh.R;
import com.example.vpelenskyi.qrssh.database.Data;
import com.example.vpelenskyi.qrssh.sshclient.AsynckCreatSession;

/**
 * Created by v.pelenskyi on 22.12.2015.
 */
public class NewHost extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "QRSSH_LOG";

    private EditText etAlias, etHost, etPort, etUsername, etPassword;
    private Button btnSaveHost, btnCheckConnect;
    private RadioGroup rgOS;
    private RadioButton rbWindows, rbUbuntu;
    private Data db;

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
        btnCheckConnect = (Button) findViewById(R.id.btnCheckConnect);
        btnCheckConnect.setOnClickListener(this);

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
        if (TextUtils.isEmpty(etAlias.getText().toString().trim())) {
            etAlias.setError("can't be empty");
            return false;
        }
        if (TextUtils.isEmpty(etHost.getText().toString().trim())) {
            etHost.setError("can't be empty");
            return false;
        }
        if (TextUtils.isEmpty(etPort.getText().toString().trim())) {
            etPort.setError("can't be empty\n usually default port  uses 22 ");
            return false;
        }
        if (TextUtils.isEmpty(etUsername.getText().toString().trim())) {
            etUsername.setError("can't be empty");
            return false;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            etPassword.setError("can't be empty");
            return false;
        }

        return true;
    }

    private void addHost() {
        if (validation()) {
            db = new Data(this);
            db.open();

            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();
            String alias = etAlias.getText().toString();
            String host = etHost.getText().toString();
            int os = chekedOS();
            int port = Integer.valueOf(etPort.getText().toString());
            long id = db.insertHost(alias, os, host, port, user, pass);
            db.close();
            if (id != -1) {
                Intent intent = new Intent();
                intent.putExtra("alias", alias);
                intent.putExtra("host", host);
                intent.putExtra("user", user);
                intent.putExtra("pass", pass);
                intent.putExtra("os", os);
                intent.putExtra("id", id);
               setResult(RESULT_OK, intent);
                finish();
            }else{
                setResult(RESULT_CANCELED, null);
                finish();
            }
            Log.i(TAG, "insert : " + id);

           // onBackPressed();
        }


    }


    private void intTextInputLayout() {
        TextInputLayout tilAlias, tilHost, tilPort, tilUsername, tilPassword;

        tilAlias = (TextInputLayout) findViewById(R.id.aliasInputLayout);
        etAlias = (EditText) tilAlias.findViewById(R.id.alias);
        tilAlias.setHint(getString(R.string.etAlias));

        tilHost = (TextInputLayout) findViewById(R.id.hostInputLayout);
        etHost = (EditText) tilHost.findViewById(R.id.host);
        tilHost.setHint(getString(R.string.hintHost));

        tilPort = (TextInputLayout) findViewById(R.id.portInputLayout);
        etPort = (EditText) tilPort.findViewById(R.id.port);
        tilPort.setHint(getString(R.string.hintPort));

        tilUsername = (TextInputLayout) findViewById(R.id.usernameInputLayout);
        etUsername = (EditText) tilUsername.findViewById(R.id.username);
        tilUsername.setHint(getString(R.string.hintUsername));

        tilPassword = (TextInputLayout) findViewById(R.id.passwordInputLayout);
        etPassword = (EditText) tilPassword.findViewById(R.id.password);
        tilPassword.setHint(getString(R.string.hintPassword));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveHost:
                addHost();
                break;
            case R.id.btnCheckConnect:
                checkConnect();
                break;
        }

    }

    private void checkConnect() {
        if (validation()) {
            AsynckCreatSession asynckCreatSession = new AsynckCreatSession(NewHost.this);
            asynckCreatSession.execute(new Host(etAlias.getText().toString(),
                    etHost.getText().toString(),
                    etUsername.getText().toString(),
                    etPassword.getText().toString(),
                    Integer.valueOf(etPort.getText().toString())));
        }

    }

}
