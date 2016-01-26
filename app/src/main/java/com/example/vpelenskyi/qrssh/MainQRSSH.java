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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.vpelenskyi.qrssh.database.Data;
import com.example.vpelenskyi.qrssh.host.Host;
import com.example.vpelenskyi.qrssh.host.NewHost;

public class MainQRSSH extends AppCompatActivity {
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

        Data db = new Data(this);
        db.open();
        cursor = db.getAllData();
        startManagingCursor(cursor);


        //   ImageView img = (ImageView) findViewById(R.id.itemImeg);
        //    img.setImageResource(R.drawable.ubuntu);

        String[] from = new String[]{db.COLUMN_ALIAS, db.COLUMN_OS, db.COLUMN_OS};
        int[] to = new int[]{R.id.itemText, R.id.tvOS, R.id.itemImeg};

        //SIMPLE CURSOR ADAPTER
        SimpleCursorAdapter scAdapter = new MySimlpeCursorAdapte(this, R.layout.item, cursor, from, to);
        //new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);

        //LIST VIEW
        listView = (ListView) findViewById(R.id.lvHost);
        listView.setAdapter(scAdapter);

        //COUNT VIEW TEXT
        tvCount = (TextView) findViewById(R.id.tvConuts);
        tvCount.setText("counts host: " + cursor.getCount());

        //  TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FLOATIONG ACTION BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addHost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(getApplicationContext(), NewHost.class);
                startActivity(intent);


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_qrssh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

          //  Log.i("test", "" + value);
            super.setViewImage(v, value);
        }

        @Override
        public void setViewText(TextView v, String text) {
            if(v.getId() == R.id.tvOS){
                switch (Integer.parseInt(text)){
                    case Host.OS_UBUNTU:
                        text = "ubuntu";
                        break;
                    case Host.OS_WINDOWS:
                        text = "windows";
                        break;

                }
                Log.i("test", "" + text);

            }
            super.setViewText(v, text);


        }

    }
}
