package com.example.vpelenskyi.qrssh;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.vpelenskyi.qrssh.database.Data;
import com.example.vpelenskyi.qrssh.host.Host;
import com.example.vpelenskyi.qrssh.host.NewHost;

public class MainQRSSH extends AppCompatActivity {
    final int CM_DELETE_HOST = 0;
    final int CM_EDIT_HOST = 1;

    SimpleCursorAdapter scAdapter;
    ListView listView;
    Data db;
    Cursor cursor;
    TextView tvCount;
    long os;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_qrssh);

        //OPEN DATA BASE
        db = new Data(this);
        db.open();
        cursor = db.getAllData();
        startManagingCursor(cursor);

        //GET SIMPLE CURSOR ADAPTER
        String[] from = new String[]{db.COLUMN_ALIAS, db.COLUMN_OS, db.COLUMN_ACTIVE, db.COLUMN_OS};
        int[] to = new int[]{R.id.itemText, R.id.tvOS, R.id.tvStatus, R.id.itemImeg};
        SimpleCursorAdapter scAdapter = new MySimlpeCursorAdapte(this, R.layout.item, cursor, from, to);

        //LIST VIEW
        listView = (ListView) findViewById(R.id.lvHost);
        listView.setAdapter(scAdapter);
        registerForContextMenu(listView);

        //COUNT VIEW TEXT
        tvCount = (TextView) findViewById(R.id.tvConuts);
        tvCount.setText("counts host: " + cursor.getCount());

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FLOATIONG ACTION BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addHost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewHost.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_HOST, 0, "Delete host");
        menu.add(0, CM_EDIT_HOST, 0, "Edit host");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_HOST) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            db.deleteItem(acmi.id);
            cursor.requery();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_qrssh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class MySimlpeCursorAdapte extends SimpleCursorAdapter {

        public MySimlpeCursorAdapte(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

        @Override
        public void setViewImage(ImageView v, String value) {

            switch (Integer.parseInt(value)) {
                case Host.OS_UBUNTU:
                    value = String.valueOf(R.drawable.ubuntu);
                    break;
                case Host.OS_WINDOWS:
                    value = String.valueOf(R.drawable.windows);
                    break;
                default:
                    value = String.valueOf(R.drawable.windows);
                    break;
            }
            super.setViewImage(v, value);
        }

        @Override
        public void setViewText(TextView v, String text) {
            if (v.getId() == R.id.tvStatus) {
                switch (Integer.parseInt(text)) {
                    case 1:
                        v.setTextColor(Color.GREEN);
                        text = "ON";

                        break;
                    case 0:
                        text = "OFF";
                        break;
                }
            }
            if (v.getId() == R.id.tvOS) {
                switch (Integer.parseInt(text)) {
                    case Host.OS_UBUNTU:
                        text = "";
                        break;
                    case Host.OS_WINDOWS:
                        text = "";
                        break;
                }
            }
            super.setViewText(v, text);
        }

    }
}
