package com.example.vpelenskyi.qrssh;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.example.vpelenskyi.qrssh.sshclient.SSH;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.ArrayList;
import java.util.Properties;

public class MainQRSSH extends AppCompatActivity {
    final int CM_DELETE_HOST = 0;
    final int CM_EDIT_HOST = 1;

    private String TAG = "ssh_log";
    private ArrayList<Host> hosts;
    private HostAdapter hostAdapter;
    private ListView listView;
    private Data db;
    private Cursor cursor;
    private TextView tvStatus;
    private TextView tvAlis;
    private TextView tvHost;
    public static Host host;

    //   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_qrssh);

        //OPEN DATA BASE
        db = new Data(this);
        db.open();
        cursor = db.getAllData();
        hosts = getHosts();

        //LIST VIEW
        listView = (ListView) findViewById(R.id.lvHost);
        registerForContextMenu(listView);

        //COUNT VIEW TEXT
        tvHost = (TextView) findViewById(R.id.tvHost);
        tvAlis = (TextView) findViewById(R.id.tvAlias);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
//        tvCount.setText("counts host: " + cursor.getCount());

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

        new QRSSHAsynkTask().execute(hosts);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    protected void onRestart() {
        //  setStatusHost();
        hostAdapter.notifyDataSetChanged();
        super.onRestart();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_HOST, 0, "Delete host");
        menu.add(0, CM_EDIT_HOST, 0, "Edit host");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi;
        switch (item.getItemId()) {
            case CM_DELETE_HOST:
                acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                db.deleteItem(acmi.id);
                hostAdapter.notifyDataSetChanged();
                return true;
            case CM_EDIT_HOST:
                //need write code
                return true;
        }

        return super.onContextItemSelected(item);
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



    private ArrayList<Host> getHosts() {
        hosts = new ArrayList<>();
        Log.i(TAG, "count cursor = " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                String alias = cursor.getString(cursor.getColumnIndex(Data.COLUMN_ALIAS));
                String hostName = cursor.getString(cursor.getColumnIndex(Data.COLUMN_HOST));
                String user = cursor.getString(cursor.getColumnIndex(Data.COLUMN_USER));
                String pass = cursor.getString(cursor.getColumnIndex(Data.COLUMN_PASS));
                int port = cursor.getInt(cursor.getColumnIndex(Data.COLUMN_PORT));
                int os = cursor.getInt(cursor.getColumnIndex(Data.COLUMN_OS));
                boolean hostConnect = false;
                hosts.add(new Host(alias, hostName, port, user, pass, os, hostConnect));
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "count cursor = " + hosts.size());
        return hosts;
    }

    public class QRSSHAsynkTask extends AsyncTask<ArrayList<Host>, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainQRSSH.this);
            progressDialog.setTitle("Check connect to ssh Host");
            progressDialog.setMessage("pleas wait");

            progressDialog.setButton(Dialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(ArrayList... params) {
            SSH ssh = new SSH();
            for (ArrayList<Host> arrayList : params) {
                for (int i = 0; arrayList.size() > i; i++) {
                    arrayList.get(i).setHostConnect(ssh.openSession(arrayList.get(i)));
                    Log.i(TAG, arrayList.get(i).toString());
                    ssh.close();
                }


            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            hostAdapter = new HostAdapter(MainQRSSH.this, hosts);
            listView.setAdapter(hostAdapter);
            progressDialog.dismiss();
        }
    }
}
