package com.example.vpelenskyi.qrssh;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vpelenskyi.qrssh.sshclient.SSH;
import com.example.vpelenskyi.qrssh.sshclient.TaskCreatSession;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jcraft.jsch.Session;

/**
 * Created by v.pelenskyi on 05.02.2016.
 */


public class ActiveHost extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "ssh_log";
    private TextView tvAlias;
    private TextView tvHost;
    private TextView tvResultScan;
    private EditText edUrl;
    private Button btnSendUrl;
    private Button btnQRReader;
    private SSH ssh;
    private String url;
    private ProgressDialog dialog;
    private ActiveHostTask activeHostTask;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getAsyncTask();

        activeHostTask = (ActiveHostTask) getLastCustomNonConfigurationInstance();
        if (activeHostTask == null) {
            activeHostTask = new ActiveHostTask();
        }
        activeHostTask.link(this);

        setContentView(R.layout.activity_connect_host);
        ssh = SSH.getInstanceSSH();
        Log.i(TAG, " onCreate init ssh class  " + ssh.hashCode());
        tvResultScan = (TextView) findViewById(R.id.tvResultScan);
        tvAlias = (TextView) findViewById(R.id.tvAlias);
        tvHost = (TextView) findViewById(R.id.tvHost);
        edUrl = (EditText) findViewById(R.id.edUrl);
        btnSendUrl = (Button) findViewById(R.id.btnSendUrl);
        btnQRReader = (Button) findViewById(R.id.btnQRReader);

        if (ssh != null) {
            if (ssh.getSession() == null) {
                Log.i(TAG, "TaskCreatSession(ActiveHost.this) ");
                new TaskCreatSession(ActiveHost.this).execute(MainQRSSH.host);
            }
            session = ssh.getSession();
            //  Log.i(TAG, "onCreate session = "+session.hashCode());
        }

        if (MainQRSSH.host != null) {
            tvAlias.setText(MainQRSSH.host.getAlias());
            tvHost.setText(MainQRSSH.host.getHost() + " : " + MainQRSSH.host.getPort());
            Log.i(TAG, MainQRSSH.host.toString());
        } else {
            onBackPressed();
        }
        btnSendUrl.setOnClickListener(this);
        btnQRReader.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activeHostTask.getStatus() == AsyncTask.Status.RUNNING
             ) {
            btnSendUrl.setEnabled(false);
            showDialog(true);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        activeHostTask.unLink();
        return activeHostTask;
    }

    public void showDialog(boolean run) {

        if (run) {
            dialog = new ProgressDialog(this);
            Log.i(TAG, " creat new dialog " + dialog.hashCode());
            dialog.setTitle("Dialog");
            dialog.setMessage("Pleas Waite...");
            dialog.setButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelTask();
                }
            });
            try {
                dialog.show();
            } catch (Exception e) {
            }

        } else {
            Log.i(TAG, "dialog.dismiss()");

            dialog.dismiss();
        }

    }

    private void cancelTask() {
        if (activeHostTask == null) {
            return;
        }
        Log.d(TAG, "Cancel Task " + activeHostTask.cancel(false));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            Log.d(TAG, "pvprot ekrany");
            dialog.dismiss();
        }
        // ssh.close();
    }


    private void taskClick() {

        activeHostTask = new ActiveHostTask();
        activeHostTask.link(this);
        activeHostTask.execute(MainQRSSH.host);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendUrl:
                url = edUrl.getText().toString();
                if (url != null && !url.isEmpty()) {
                    taskClick();
                } else {
                    Toast.makeText(this, "First scan code", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnQRReader:
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
                break;

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            edUrl.setText(scanContent);
            tvResultScan.setText("CONTENT: " + scanContent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public String getUrl() {
        return url;
    }

    //    //============= INTO CLASS ===================
//    class AsynkActivityHost extends AsyncTask<Host, Void, Boolean> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Boolean doInBackground(Host... params) {
//            for (Host host : params) {
//                command = createCommand(host);
//                // restart session if she was lost (disconnect)
//                if (session != null) {
//                    if (!session.isConnected()) {
//                        Log.i(TAG, "isConnected()  = " + session.isConnected());
//                        ssh.openSession(host);
//                    }
//                }
//            }
//            session = ssh.getSession();
//            ssh.openChannel(session);
//            if (url != null & !url.isEmpty()) {
//                ssh.sendCommand(command);
//            } else {
//                Log.i(TAG, "url is empty");
//            }
//            Log.i(TAG, " AsynkActivityHost ssh.getSession() " + ssh.getSession().hashCode());
//            Log.i(TAG, " AsynkActivityHost ssh " + ssh.hashCode());
//            return ssh.getSession().isConnected();
//        }
//
//        private String createCommand(Host host) {
//            switch (host.getOs()) {
//                case Host.OS_UBUNTU:
//                    command = "DISPLAY=:0 firefox \"" + url + "\"";
////                    command = "DISPLAY=:0 nohup gnome-open \"" + url + "\"";
////                    command = "DISPLAY=:0 gvfs-open  \"" + url + "\"";
////                    command = "DISPLAY=:0 x-www-browser  \"" + url + "\"";
//                    break;
//                case Host.OS_WINDOWS:
//                    command = "cmd.exe /u /c \"start " + url + "\"";
//                    break;
//            }
//            return command;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//          //  ssh.close();
//            super.onPostExecute(aBoolean);
//            if (aBoolean) {
//                tvAlias.setTextColor(Color.GREEN);
//            } else {
//                tvAlias.setTextColor(Color.RED);
//            }
//
//        }
//        //============= END INTO CLASS ===================
//
//    }


}
