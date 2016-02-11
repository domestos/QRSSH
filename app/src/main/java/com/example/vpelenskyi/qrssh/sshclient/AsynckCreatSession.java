package com.example.vpelenskyi.qrssh.sshclient;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vpelenskyi.qrssh.ActivityHost;
import com.example.vpelenskyi.qrssh.R;
import com.example.vpelenskyi.qrssh.host.Host;
import com.example.vpelenskyi.qrssh.host.NewHost;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * Created by varenik on 30.01.16.
 */
public class AsynckCreatSession extends AsyncTask<Host, Void, Boolean> {

    private String TAG = "ssh_log";
    JSch jSch;
    private Context context;
    ProgressDialog progressDialog;

    public AsynckCreatSession(NewHost context) {
        this.context = context;
    }

    public AsynckCreatSession(ActivityHost context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        jSch = new JSch();
        progressDialog = new ProgressDialog(context);
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
        boolean testConnect = false;
        if (params.length == 1) {
            SSH ssh = SSH.getInstanceSSH();
            for (Host host : params) {
                testConnect = ssh.openSession(host);
            }
        }
        return testConnect;
    }

    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            aliasSetColor(Color.GREEN);
            Toast.makeText(context, "Connect", Toast.LENGTH_LONG).show();
        } else {
            aliasSetColor(Color.RED);
            Toast.makeText(context, "NO Connect", Toast.LENGTH_LONG).show();
        }
        progressDialog.dismiss();
    }

    private void aliasSetColor(int color) {
        if (context.getClass().equals(ActivityHost.class)) {
            TextView tvAlias = (TextView) ((ActivityHost) (context)).findViewById(R.id.tvAlias);
            tvAlias.setTextColor(color);
        }


    }
}
