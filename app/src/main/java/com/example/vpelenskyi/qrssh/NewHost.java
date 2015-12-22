package com.example.vpelenskyi.qrssh;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

/**
 * Created by v.pelenskyi on 22.12.2015.
 */
public class NewHost extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_host_activity);

        ActionBar toolbar =  getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Add New HOST");
        intTextInputLayout();


//        Toolbar toolbarNewHost = (Toolbar) findViewById(R.id.toolbarNewHost);
//        setSupportActionBar(toolbarNewHost);
    }

    private void intTextInputLayout() {

        TextInputLayout tilAlias = (TextInputLayout) findViewById(R.id.aliasInputLayout);
        EditText alias = (EditText) tilAlias.findViewById(R.id.alias);
        tilAlias.setHint(getString(R.string.etAlias));

        TextInputLayout tilHost = (TextInputLayout) findViewById(R.id.hostInputLayout);
        EditText host = (EditText) tilHost.findViewById(R.id.host);
        tilHost.setHint(getString(R.string.hintHost));

        TextInputLayout tilPort = (TextInputLayout) findViewById(R.id.portInputLayout);
        EditText port = (EditText) tilPort.findViewById(R.id.port);
        tilPort.setHint(getString(R.string.hintPort));

        TextInputLayout tilUsername = (TextInputLayout) findViewById(R.id.usernameInputLayout);
        EditText username = (EditText) tilUsername.findViewById(R.id.username);
        tilPort.setHint(getString(R.string.hintUsername));

        TextInputLayout tilPassword = (TextInputLayout) findViewById(R.id.usernameInputLayout);
        EditText password = (EditText) tilPassword.findViewById(R.id.password);
        tilPort.setHint(getString(R.string.hintPassword));

    }


}
