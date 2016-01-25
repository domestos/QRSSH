package com.example.vpelenskyi.qrssh;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.vpelenskyi.qrssh.database.Data;
import com.example.vpelenskyi.qrssh.host.NewHost;

public class MainQRSSH extends AppCompatActivity {
    SimpleCursorAdapter scAdapter;
    ListView listView;
    Data db;
    Cursor cursor;
    TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_qrssh);

        Data db = new Data(this);
        db.open();
        cursor = db.getAllData();
        startManagingCursor(cursor);

        String[] from = new String[]{db.COLUMN_ALIAS};
        int[] to = new int[]{R.id.itemText};

        //SIMPLE CURSOR ADAPTER
        SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);

        //LIST VIEW
        listView = (ListView) findViewById(R.id.lvHost);
        listView.setAdapter(scAdapter);

        //COUNT VIEW TEXT
        tvCount = (TextView) findViewById(R.id.tvConuts);
        tvCount.setText("counts host: "+cursor.getCount());

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
}
