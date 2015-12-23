package com.example.vpelenskyi.qrssh;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by v.pelenskyi on 22.12.2015.
 */
public class NewHost extends AppCompatActivity {


    private TextInputLayout tilAlias, tilHost, tilPort, tilUsername, tilPassword;
    private EditText alias, host, port, username, password;
    private Button btnSaveHost;
    private String os = "";
    //  Host newhost =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_host_activity);


        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Add New HOST");
        intTextInputLayout();

//        btnSaveHost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                newhost = new Host(
//                        alias.getText().toString(),
//                        host.getText().toString(),
//                        Integer.valueOf(port.getText().toString()),
//                        username.getText().toString(),
//                        password.getText().toString(),
//                        os
//                );
//
//                Toast.makeText(getApplicationContext(), newhost.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });


//        Toolbar toolbarNewHost = (Toolbar) findViewById(R.id.toolbarNewHost);
//        setSupportActionBar(toolbarNewHost);
    }

    public void myClick(View v) {

        Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();

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
        tilPort.setHint(getString(R.string.hintUsername));

        tilPassword = (TextInputLayout) findViewById(R.id.usernameInputLayout);
        password = (EditText) tilPassword.findViewById(R.id.password);
        tilPort.setHint(getString(R.string.hintPassword));

    }


}
