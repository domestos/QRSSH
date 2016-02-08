package com.example.vpelenskyi.qrssh;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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


public class ActivityHost extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "ssh_log";
    private TextView tvAlias;
    private TextView tvHost;
    private EditText edUrl;
    private Button btnSendUrl;
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
        tvAlias = (TextView) findViewById(R.id.tvAlias);
        tvHost = (TextView) findViewById(R.id.tvHost);
        edUrl = (EditText) findViewById(R.id.edUrl);
        btnSendUrl = (Button) findViewById(R.id.btnSendUrl);

        if (MainQRSSH.host != null) {
            tvAlias.setText(MainQRSSH.host.getAlias());
            tvHost.setText(MainQRSSH.host.getHost() + " : " + MainQRSSH.host.getPort());
            new TestConnect(ActivityHost.this).execute(MainQRSSH.host);
            Log.i(TAG, MainQRSSH.host.toString());
        } else {
            onBackPressed();
        }
        btnSendUrl.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ssh.close();

    }

    @Override
    public void onClick(View v) {
        url = edUrl.getText().toString();
        new AsynkActivityHost().execute(MainQRSSH.host);
    }


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
                    command = "DISPLAY=:0 nohup gnome-open \"" + url + "\"";
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


    }


}
