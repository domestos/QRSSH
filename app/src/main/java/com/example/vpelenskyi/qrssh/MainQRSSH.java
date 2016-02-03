package com.example.vpelenskyi.qrssh;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.vpelenskyi.qrssh.database.Data;
import com.example.vpelenskyi.qrssh.host.Host;
import com.example.vpelenskyi.qrssh.host.NewHost;
import com.example.vpelenskyi.qrssh.sshclient.SSH;
import com.example.vpelenskyi.qrssh.sshclient.TestConnect;
import com.jcraft.jsch.Session;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainQRSSH extends AppCompatActivity {
    final int CM_DELETE_HOST = 0;
    final int CM_EDIT_HOST = 1;
    final int CM_ACTIVE_HOST = 2;

    SimpleCursorAdapter scAdapter;
    ArrayList<Host> hosts;
    ArrayList<Boolean> itemConnects;
    ListView listView;
    Data db;
    Session session;
    Cursor cursor;
    TextView tvStatus, tvAlis, tvHost;
    long os;
    public Host host;

    //   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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

        // setStatusHost();

//        showConnectHost();
        ;
      //  TextView tv = (TextView) item.findViewById(R.id.itemText);
        Log.i("test", "this getItem(1) = "+listView.getAdapter().getItem(1).toString());
        //  Log.i("test", "class = " + listView.getAdapter().getItem(0).getClass() + " count " + listView.getCount());

    }


    @Override
    protected void onStart() {
        super.onStart();
        // setStatusHost();
        showConnectHost();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    protected void onRestart() {
        // setStatusHost();
        showConnectHost();
        Log.i("test", " " + host.hashCode() + host.toString());
        super.onRestart();
    }

    private ArrayList<Host> getHosts() {
        hosts = new ArrayList<>();
        if (cursor.moveToFirst()) {
          do  {
                String alias = cursor.getString(cursor.getColumnIndex(Data.COLUMN_ALIAS));
                String host = cursor.getString(cursor.getColumnIndex(Data.COLUMN_HOST));
                String user = cursor.getString(cursor.getColumnIndex(Data.COLUMN_USER));
                String pass = cursor.getString(cursor.getColumnIndex(Data.COLUMN_PASS));
                String os = cursor.getString(cursor.getColumnIndex(Data.COLUMN_OS));
                int port = cursor.getInt(cursor.getColumnIndex(Data.COLUMN_PORT));
                int id = cursor.getInt(cursor.getColumnIndex(Data.COLUMN_ID));
                hosts.add(new Host(alias, host, port, user, pass, os, id));
            } while (cursor.moveToNext());
        }

        return hosts;
    }

    private void showConnectHost() {
        QRSSHAsynkTask qrsshAsynkTask = new QRSSHAsynkTask();
        qrsshAsynkTask.execute(getHosts());
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_ACTIVE_HOST, 0, "set Activity");
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
                setStatusHost();
                cursor.requery();
                return true;
            case CM_ACTIVE_HOST:
                acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                db.setActivity(acmi.id);
                setStatusHost();
                cursor.requery();
                return true;
            case CM_EDIT_HOST:
                //need write code
                return true;
        }

        return super.onContextItemSelected(item);
    }

    private void setStatusHost() {
        boolean isConnect = false;
        if (host == null) {
            host = new Host();
            Log.i("test", "init host = " + host.hashCode());

        }

        if (host.getActiveHost(db) != null) {
            TestConnect testConnect = new TestConnect(this);
            testConnect.execute(host);

            try {
                isConnect = testConnect.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            if (isConnect) {
                tvStatus.setText(getResources().getText(R.string.st_status_host) + " connect");
                tvStatus.setTextColor(Color.GREEN);
            } else {
                tvStatus.setText(getResources().getText(R.string.st_status_host) + " no connect");
                tvStatus.setTextColor(Color.MAGENTA);
            }

            tvAlis.setText(getResources().getText(R.string.st_alias_host) + " " + host.getAlias().toString());
            tvHost.setText(getText(R.string.st_host) + " " + host.getHost());

        } else {
            tvStatus.setText(R.string.st_status_host + " no info");
            tvAlis.setText(getResources().getText(R.string.st_alias_host) + " no info");
            tvHost.setText(getText(R.string.st_host) + " no info");

        }

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
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);

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
                        //     v.setTextColor(Color.GREEN);
                        text = "ON";
                        break;
                    case 0:
                        //        v.setTextColor(Color.BLACK);
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


    class QRSSHAsynkTask extends AsyncTask<ArrayList<Host>, Void, ArrayList<Boolean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<Boolean> doInBackground(ArrayList<Host>... params) {
            SSH ssh = new SSH();
            itemConnects = new ArrayList<>();
            for (ArrayList<Host> hosts : params) {
                for (int i = 0; hosts.size() > i; i++) {

                    host = hosts.get(i);
                    String user =host.getUsername();
                    String  hostPC = host.getHost();
                    String pass = host.getPassword();
                    int port =host.getPort();
                    Boolean itemBool = ssh.openSession(user, hostPC, port, pass);
                    itemConnects.add(itemBool);
                }
            }
            return itemConnects;

        }


        @Override
        protected void onPostExecute(ArrayList<Boolean> booleans) {
            super.onPostExecute(booleans);
            for (int i = 0; booleans.size() > i; i++) {
                Log.i("test", "connect item id " + i + "  " + booleans.get(i));
            }

        }
    }

}
