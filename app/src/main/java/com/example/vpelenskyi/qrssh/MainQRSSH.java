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
import android.widget.Toast;

import com.example.vpelenskyi.qrssh.database.Data;
import com.example.vpelenskyi.qrssh.host.Host;
import com.example.vpelenskyi.qrssh.host.NewHost;
import com.example.vpelenskyi.qrssh.sshclient.SSH;

import java.util.ArrayList;

public class MainQRSSH extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String TAG = "ssh_log";
    private ListView listView;
    private SSH ssh = SSH.getInstanceSSH();

    final int CM_DELETE_HOST = 0;
    final int CM_EDIT_HOST = 1;
    private int INT_ADD_HOST = 1;

    public static ArrayList<Host> hosts;   //singleton hosts
    public static Host host;

    private TaskMainQRSSH taskMainQRSSH;
    private BaseAdapterHost baseAdapterHost;
    private Cursor cursor;
    private Data db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_qrssh);
        db = new Data(this);
        db.open();
        if (hosts == null) {
            hosts = getHosts();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initFloatingActionButton();  // FLOATIONG ACTION BUTTON
        listView = (ListView) findViewById(R.id.lvHost);
        baseAdapterHost = new BaseAdapterHost(MainQRSSH.this, hosts);
        listView.setAdapter(baseAdapterHost);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(this);
        //Run AsyncTask only one when open APP
        singleRunTask();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart close session");
        ssh.close();  //make give exception
        baseAdapterHost.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    /**
     * Shows the FloatingActionButton and shows new Intent if clicked on the button
     * intent returns object data what has info about new host
     */
    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addHost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewHost.class);
                startActivityForResult(intent, INT_ADD_HOST);
            }
        });
    }

    /**
     * gets a new Host from Activity NewHost.class (startActivityForResult(intent, INT_ADD_HOST);)
     * and adds this host into ArrayList after that shows new list
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    hosts = addNewHost(data);
                    Log.i(TAG, "successful add host");
                    //shows not checked host
                    listView.setAdapter(new BaseAdapterHost(MainQRSSH.this, hosts));
                    //update the hosts. checks and shows hosts again
                    hosts = getHosts();
                    Log.i(TAG, "onActivityResult = size cursor " + cursor.getCount());
                    Log.i(TAG, "onActivityResult = size ArrayLisy " + hosts.size());
                    createAndRunTask(hosts);
                    break;
            }
        }

    }

    /**
     * Run Into class TaskMainQRSSH extends AsyncTask what check connect to host
     */
    public void singleRunTask() {
        //returns object taskMainQRSSH if he was init earlier
        taskMainQRSSH = (TaskMainQRSSH) getLastCustomNonConfigurationInstance();
        if (taskMainQRSSH == null) {
            taskMainQRSSH = new TaskMainQRSSH();
        }
        taskMainQRSSH.link(this);
        //run only if this first start
        if (taskMainQRSSH.getStatus() != AsyncTask.Status.RUNNING &&
                taskMainQRSSH.getStatus() != AsyncTask.Status.FINISHED) {
            Log.i(TAG, "Start on Creat  taskMainQRSSH.execute(hosts)");
            taskMainQRSSH.execute(hosts);
        }
    }

    /**
     * after returning  screen, clears old activity(content =null) into TaskMainQRSSH.class
     * and returns this class
     *
     * @return taskMainQRSSH - object TaskMainQRSSH.class what was been initialized earlier
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        taskMainQRSSH.unLink(); //set context =null
        return taskMainQRSSH;
    }


    private void createAndRunTask(ArrayList<Host> hosts) {
        taskMainQRSSH = new TaskMainQRSSH();
        taskMainQRSSH.link(this);
        taskMainQRSSH.execute(hosts);
    }

    /**
     * Gets info from data what returned from Intent, creates new host and adds him into ArrayList<Host>
     *
     * @param data - contains info about a new host
     * @return hosts - ArrayList<Host>
     */
    private ArrayList<Host> addNewHost(Intent data) {
        hosts.add(new Host(
                data.getStringExtra("alias"),
                data.getStringExtra("host"),
                data.getIntExtra("port", 22),
                data.getStringExtra("user"),
                data.getStringExtra("pass"),
                data.getIntExtra("os", 0),
                false,
                data.getIntExtra("id", 404)));
        return hosts;
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
                createAndRunTask(hosts);
                return true;
            case CM_EDIT_HOST:
                //need write code
                return true;
        }
        return super.onContextItemSelected(item);
    }



    /**
     * get all hosts from database and is to write them in ArrayList
     *
     * @return ArrayList<Host> hosts
     */
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


    /***
     * @param parent
     * @param view
     * @param position position view in listView
     * @param id       equals position
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, " parent " + parent.getCount() + " position " + position + " id " + id);
        host = hosts.get(position);
        Log.i(TAG, hosts.get(position).getAlias());
        Intent intent = new Intent(getApplicationContext(), ActiveHost.class);
        startActivity(intent);
    }

    //=================Not use now ===========================
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
    //=================Not use now ===========================
    /**
     * This class is executed when deleting or adding new host
     * and runs only ones when executed method onCreate
     * This (TaskMainQRSSH) class use TaskCreateSession class what checks connect to host, and
     * result (boolean) what returned is writes in object Host what is into the ArrayList<Host> hosts
     */
    // ================= INTO CLASS ======================================
    static public class TaskMainQRSSH extends AsyncTask<ArrayList<Host>, Void, Boolean> {

        private String TAG = "ssh_log";
        private SSH ssh = SSH.getInstanceSSH();
        private MainQRSSH activity;
        private ProgressDialog progressDialog;

        public void link(MainQRSSH mainQRSSH) {
            activity = mainQRSSH;
            Log.i(TAG, "MainQRSHH Link get activity: " + activity.hashCode());
        }

        public void unLink() {
            activity = null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(activity, "begin check connect to hosts", Toast.LENGTH_SHORT).show();
//            //  Log.i(TAG, "MainQRSHH onPreExecute: " + activity.hashCode());
//            progressDialog = new ProgressDialog(activity);
//            progressDialog.setTitle("Check connect to ssh Host");
//            progressDialog.setMessage("pleas wait");
//            progressDialog.setButton(Dialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                }
//            });
//
//            try {
//                progressDialog.show();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        @Override
        protected Boolean doInBackground(ArrayList... params) {
            for (ArrayList<Host> arrayList : params) {
                for (int i = 0; arrayList.size() > i; i++) {
                    arrayList.get(i).setHostConnect(ssh.openSession(arrayList.get(i)));
                    Log.i(TAG, arrayList.get(i).toString());
                    publishProgress();
                    ssh.close();
                }
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            activity.listView.setAdapter(new BaseAdapterHost(activity, hosts));
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            activity.listView.setAdapter(new BaseAdapterHost(activity, hosts));
            Toast.makeText(activity, "finished checked..", Toast.LENGTH_SHORT).show();
//
//  try {
//                progressDialog.dismiss();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        }
    }
// ================= INTO CLASS ======================================
}
