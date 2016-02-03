package com.example.vpelenskyi.qrssh.sshclient;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vpelenskyi.qrssh.host.Host;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * Created by varenik on 30.01.16.
 */
public class TestConnect extends AsyncTask<Host, Void, Boolean> {

    private String TAG = "log_ssh";
    Session session;
    JSch jSch;
    private Context contHost;

    boolean running;
    ProgressDialog progressDialog;

    public TestConnect(Context contHost) {

        this.contHost = contHost;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        jSch = new JSch();

        progressDialog = new ProgressDialog(contHost);
        progressDialog.setTitle("Connect to ssh Host");
        progressDialog.setMessage("pleas wait");

        progressDialog.setButton(Dialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        progressDialog.show();


    }

    @Override
    protected Boolean doInBackground(Host... params) {
        if (params.length == 1) {

            for (Host host : params) {
                Log.i(TAG, "hist values:" + host.getUsername() + " " + host.getHost() + " " + host.getPort());
                try {
                    session = jSch.getSession(
                            host.getUsername(),
                            host.getHost(),
                            host.getPort()
                    );
                } catch (JSchException e) {
                    e.printStackTrace();
                    Log.e(TAG, "session = jSch.getSession() give the error " + e.getMessage());

                }
                session.setPassword(host.getPassword());

                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                config.put("compression.s2c", "zlib,none");
                config.put("compression.c2s", "zlib,none");

                session.setConfig(config);
                try {
                    session.connect(5000);
                } catch (JSchException e) {
                    e.printStackTrace();
                    Log.e(TAG, "session.connect give the error " + e);
                }
            }


        }
        return session.isConnected();
    }

    @Override
    protected void onPostExecute(Boolean isConnect) {
        if (isConnect) {
            Toast.makeText(contHost, "Connect", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(contHost, "NO Connect", Toast.LENGTH_LONG).show();

        }
        progressDialog.dismiss();
        super.onPostExecute(isConnect);
    }


}
