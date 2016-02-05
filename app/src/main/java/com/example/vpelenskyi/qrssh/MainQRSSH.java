package com.example.vpelenskyi.qrssh;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ListView;

import com.example.vpelenskyi.qrssh.database.Data;
import com.example.vpelenskyi.qrssh.host.Host;
import com.example.vpelenskyi.qrssh.host.NewHost;
import com.example.vpelenskyi.qrssh.sshclient.SSH;

import java.util.ArrayList;

public class MainQRSSH extends AppCompatActivity {

    final int CM_DELETE_HOST = 0;
    final int CM_EDIT_HOST = 1;
    private int INT_ADD_HOST = 1;
    private String TAG = "ssh_log";
    public static ArrayList<Host> hosts;
    private HostAdapter hostAdapter;
    private ListView listView;
    private Data db;
    private Cursor cursor;
    public static Host host;

    //   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_qrssh);

        //OPEN DATA BASE
        db = new Data(this);
        db.open();
        hosts = getHosts();

        //LIST VIEW
        listView = (ListView) findViewById(R.id.lvHost);
        registerForContextMenu(listView);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FLOATIONG ACTION BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addHost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewHost.class);
                startActivityForResult(intent, INT_ADD_HOST);
            }
        });

        hostAdapter = new HostAdapter(MainQRSSH.this, hosts);
        listView.setAdapter(hostAdapter);
        new QRSSHAsynkTask().execute(hosts);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i(TAG, " parent " + parent.getCount() + " position " + position + " id " + id);
                host = hosts.get(position);
                Log.i(TAG, hosts.get(position).getAlias());
                Intent intent = new Intent(getApplicationContext(), ActivityHost.class);
//                intent.putExtra("alias", host.getAlias());
//                intent.putExtra("host", host.getHost());
//                intent.putExtra("port", host.getPort());
//                intent.putExtra("user", host.getUsername());
//                intent.putExtra("pass", host.getPassword());
//                intent.putExtra("os", host.getOs());
//                intent.putExtra("id", host.getId());
                startActivity(intent);
                // host=null;
//
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    hosts.add(new Host(
                            data.getStringExtra("alias"),
                            data.getStringExtra("host"),
                            data.getIntExtra("port", 22),
                            data.getStringExtra("user"),
                            data.getStringExtra("pass"),
                            data.getIntExtra("os", 0),
                            false,
                            data.getIntExtra("id", -1)));
                    Log.i(TAG, "successful add host");
                    listView.setAdapter(new HostAdapter(MainQRSSH.this, hosts));
                    hosts = getHosts();
                    Log.i(TAG, "onActivityResult = size cursor " + cursor.getCount());
                    Log.i(TAG, "onActivityResult = size ArrayLisy " + hosts.size());
                    new QRSSHAsynkTask().execute(hosts);
                    break;
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        db.close();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hostAdapter.notifyDataSetChanged();

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
                int p = acmi.position;
                Log.i(TAG, "positin  " + acmi.position + " id " + acmi.id + " alias " +
                        hosts.get(p).getAlias());
                int del = db.deleteItem(hosts.get(p).getId());
                Log.i(TAG, "del " + del);
                hosts.remove(p);
                listView.setAdapter(new HostAdapter(MainQRSSH.this, hosts));
                hosts = getHosts();
                new QRSSHAsynkTask().execute(hosts);
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

    public ArrayList<Host> getHosts() {
        cursor = db.getAllData();
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
                int id = cursor.getInt(cursor.getColumnIndex(Data.COLUMN_ID));
                boolean hostConnect = false;
                hosts.add(new Host(alias, hostName, port, user, pass, os, hostConnect, id));
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "count cursor = " + hosts.size());
        return hosts;
    }


    public class QRSSHAsynkTask extends AsyncTask<ArrayList<Host>, Void, Boolean> {
        SSH ssh = new SSH();
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
            //hostAdapter.notifyDataSetChanged();
            listView.setAdapter(new HostAdapter(MainQRSSH.this, hosts));

            progressDialog.dismiss();
        }
    }
}
