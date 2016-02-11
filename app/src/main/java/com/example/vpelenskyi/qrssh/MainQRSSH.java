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

public class MainQRSSH extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String TAG = "ssh_log";
    private ListView listView;

    final int CM_DELETE_HOST = 0;
    final int CM_EDIT_HOST = 1;
    private int INT_ADD_HOST = 1;

    public static ArrayList<Host> hosts;
    public static Host host;

    private TaskMainQRSSH TaskMainQRSSH;
    private BaseAdapterHost baseAdapterHost;
    private Cursor cursor;
    private Data db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_qrssh);
        //OPEN DATA BASE
        db = new Data(this);
        db.open();
        //singleton hosts
        if (hosts == null) {
            hosts = getHosts();
        }
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
        //find, show, setMenu, setOnClickItem ViewList
        listView = (ListView) findViewById(R.id.lvHost);
        baseAdapterHost = new BaseAdapterHost(MainQRSSH.this, hosts);
        listView.setAdapter(baseAdapterHost);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(this);
        //Run AsyncTask only one when open APP
        runTask();
    }

    /**
     *
     */
    public void runTask() {
        TaskMainQRSSH = (TaskMainQRSSH) getLastCustomNonConfigurationInstance();
        if (TaskMainQRSSH == null) {
            TaskMainQRSSH = new TaskMainQRSSH();
        }
        TaskMainQRSSH.link(this);
        if (TaskMainQRSSH.getStatus() != AsyncTask.Status.RUNNING &&
                TaskMainQRSSH.getStatus() != AsyncTask.Status.FINISHED) {
            TaskMainQRSSH.execute(hosts);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        TaskMainQRSSH.unLink();
        return TaskMainQRSSH;
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
                    listView.setAdapter(new BaseAdapterHost(MainQRSSH.this, hosts));
                    hosts = getHosts();
                    Log.i(TAG, "onActivityResult = size cursor " + cursor.getCount());
                    Log.i(TAG, "onActivityResult = size ArrayLisy " + hosts.size());
                    new TaskMainQRSSH().execute(hosts);
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
        baseAdapterHost.notifyDataSetChanged();
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
                int del = db.deleteHost(hosts.get(p).getId());
                Log.i(TAG, "del " + del);
                hosts.remove(p);
                listView.setAdapter(new BaseAdapterHost(MainQRSSH.this, hosts));
                hosts = getHosts();
                new TaskMainQRSSH().execute(hosts);
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, " parent " + parent.getCount() + " position " + position + " id " + id);
        host = hosts.get(position);
        Log.i(TAG, hosts.get(position).getAlias());
        Intent intent = new Intent(getApplicationContext(), ActivityHost.class);
        startActivity(intent);
    }

    // ================= INTO CLASS ======================================
    static public class TaskMainQRSSH extends AsyncTask<ArrayList<Host>, Void, Boolean> {

        private static MainQRSSH activity;
        private String TAG = "ssh_log";

        public void link(MainQRSSH mainQRSSH) {
            activity = mainQRSSH;
            Log.i(TAG, "MainQRSHH Link get this: " + activity.hashCode());
        }

        public void unLink() {
            activity = null;
        }

        SSH ssh = SSH.getInstanceSSH();
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "MainQRSHH onPreExecute: " + activity.hashCode());
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Check connect to ssh Host");
            progressDialog.setMessage("pleas wait");
            progressDialog.setButton(Dialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            activity.listView.setAdapter(new BaseAdapterHost(activity, hosts));
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
// ================= INTO CLASS ======================================
}
