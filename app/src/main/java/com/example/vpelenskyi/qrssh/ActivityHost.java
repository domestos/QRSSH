package com.example.vpelenskyi.qrssh;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vpelenskyi.qrssh.host.Host;
import com.example.vpelenskyi.qrssh.sshclient.SSH;
import com.example.vpelenskyi.qrssh.sshclient.TestConnect;
import com.jcraft.jsch.Session;

/**
 * Created by v.pelenskyi on 05.02.2016.
 */


public class ActivityHost extends AppCompatActivity {
    private String TAG = "ssh_log";
    private TextView tvAlias;
    private TextView tvHost;
    private EditText edUrl;
    private Button btnSendUrl;
    private SSH ssh;
  //  private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_host);
        ssh = new SSH();

        tvAlias = (TextView) findViewById(R.id.tvAlias);
        tvHost = (TextView) findViewById(R.id.tvHost);
        edUrl = (EditText) findViewById(R.id.edUrl);
        btnSendUrl = (Button) findViewById(R.id.btnSendUrl);


        if(MainQRSSH.host != null) {

            tvAlias.setText(MainQRSSH.host.getAlias());
            tvHost.setText(MainQRSSH.host.getHost() + " : " + MainQRSSH.host.getPort());
            new TestConnect(ActivityHost.this).execute(MainQRSSH.host);
      //      new AsynkActivityHost().execute(MainQRSSH.host);
            Log.i(TAG, MainQRSSH.host.toString());
        }

    }

    class AsynkActivityHost extends AsyncTask<Host, Void, Boolean> {
        private Session session;
        @Override
        protected Boolean doInBackground(Host... params) {
            boolean aBoolean = false;
            for (Host h : params) {
                aBoolean = ssh.openSession(h);

            }
            return aBoolean;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                tvAlias.setTextColor(Color.GREEN);
            } else {
                tvAlias.setTextColor(Color.RED);
            }
            session = ssh.getSession();
            Log.i(TAG," AsynkActivityHost ssh.getSession() "+session.hashCode());
            Log.i(TAG, " AsynkActivityHost ssh " + ssh.hashCode());
        }
    }

}
