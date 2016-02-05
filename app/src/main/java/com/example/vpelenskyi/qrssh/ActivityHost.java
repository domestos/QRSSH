package com.example.vpelenskyi.qrssh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.vpelenskyi.qrssh.R;

/**
 * Created by v.pelenskyi on 05.02.2016.
 */


public class ActivityHost extends AppCompatActivity {

    TextView tvAlias;
    TextView tvHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_host);
        tvAlias = (TextView) findViewById(R.id.tvAlias);
        tvHost = (TextView) findViewById(R.id.tvHost);
        Intent intent = getIntent();
        tvAlias.setText(intent.getStringExtra("alias"));
        tvHost.setText(intent.getStringExtra("host")+" : "+intent.getIntExtra("port",22));
    }


}
