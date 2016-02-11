package com.example.vpelenskyi.qrssh;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vpelenskyi.qrssh.host.Host;
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
    private Session session;
    private String url;
    private String command = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connect_host);
        ssh = SSH.getInstanceSSH();
        Log.i(TAG, " onCreate init ssh class  " + ssh.hashCode());
        tvResultScan = (TextView) findViewById(R.id.tvResultScan);
        tvAlias = (TextView) findViewById(R.id.tvAlias);
        tvHost = (TextView) findViewById(R.id.tvHost);
        edUrl = (EditText) findViewById(R.id.edUrl);
        btnSendUrl = (Button) findViewById(R.id.btnSendUrl);
        btnQRReader = (Button) findViewById(R.id.btnQRReader);

        if (MainQRSSH.host != null) {
            tvAlias.setText(MainQRSSH.host.getAlias());
            tvHost.setText(MainQRSSH.host.getHost() + " : " + MainQRSSH.host.getPort());
            new TaskCreatSession(ActiveHost.this).execute(MainQRSSH.host);
            Log.i(TAG, MainQRSSH.host.toString());
        } else {
            onBackPressed();
        }
        btnSendUrl.setOnClickListener(this);
        btnQRReader.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   ssh.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendUrl:
                url = edUrl.getText().toString();
                if (url != null && !url.isEmpty()) {
                    new AsynkActivityHost().execute(MainQRSSH.host);
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

    //============= INTO CLASS ===================
    class AsynkActivityHost extends AsyncTask<Host, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Host... params) {
            for (Host host : params) {
                command = createCommand(host);
                // restart session if she was lost (disconnect)
                if (session != null) {
                    if (!session.isConnected()) {
                        Log.i(TAG, "isConnected()  = " + session.isConnected());
                        ssh.openSession(host);
                    }
                }
            }
            session = ssh.getSession();
            ssh.openChannel(session);
            if (url != null & !url.isEmpty()) {
                ssh.sendCommand(command);
            } else {
                Log.i(TAG, "url is empty");
            }
            Log.i(TAG, " AsynkActivityHost ssh.getSession() " + ssh.getSession().hashCode());
            Log.i(TAG, " AsynkActivityHost ssh " + ssh.hashCode());
            return ssh.getSession().isConnected();
        }

        private String createCommand(Host host) {
            switch (host.getOs()) {
                case Host.OS_UBUNTU:
                    command = "DISPLAY=:0 firefox \"" + url + "\"";
//                    command = "DISPLAY=:0 nohup gnome-open \"" + url + "\"";
//                    command = "DISPLAY=:0 gvfs-open  \"" + url + "\"";
//                    command = "DISPLAY=:0 x-www-browser  \"" + url + "\"";
                    break;
                case Host.OS_WINDOWS:
                    command = "cmd.exe /u /c \"start " + url + "\"";
                    break;
            }
            return command;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                tvAlias.setTextColor(Color.GREEN);
            } else {
                tvAlias.setTextColor(Color.RED);
            }

        }
        //============= END INTO CLASS ===================

    }


}
