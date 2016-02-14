package com.example.vpelenskyi.qrssh;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vpelenskyi.qrssh.host.Host;
import com.example.vpelenskyi.qrssh.sshclient.SSH;
import com.jcraft.jsch.Session;

import java.util.concurrent.TimeUnit;

/**
 * Created by varenik on 14.02.16.
 */
public class ActiveHostTask extends AsyncTask<Host, Integer, Boolean> {
    private ActiveHost activeHost;
    private String url;
    private String TAG = "ssh_log";
    private String command = "";
    private SSH ssh;
    private Session session;

    public void link(ActiveHost activeHost) {
        this.activeHost = activeHost;
    }

    public void unLink() {
        this.activeHost = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        url = activeHost.getUrl();
        ssh = SSH.getInstanceSSH();
        activeHost.findViewById(R.id.btnSendUrl).setEnabled(true);
        activeHost.showDialog(true);
    }

    @Override
    protected Boolean doInBackground(Host... params) {
        int result = 404;
        for (Host host : params) {
            command = createCommand(host);
//         restart (init) session if she was lost (disconnect)
            if (session == null) {
                Log.i(TAG, "session == null");
                session = ssh.getSession();
            }
            if (session != null) {
                if (!session.isConnected()) {
                    Log.i(TAG, "isConnected()  = " + session.isConnected());
                    ssh.openSession(host);
                }
            }

        }
        if (isCancelled()) {
            return false;
        }
        session = ssh.getSession();

        Log.i(TAG, "Status Session " + session.isConnected());
        ssh.openChannel(session);
        if (isCancelled()) {
            return false;
        }
        if (url != null & !url.isEmpty()) {
            Log.i(TAG, "command = " + command);
            result = ssh.sendCommand(command);
        } else {
            Log.i(TAG, "url is empty");
        }

        Log.i(TAG, " AsynkActivityHost ssh.getSession() " + ssh.getSession().hashCode());
        Log.i(TAG, " AsynkActivityHost ssh " + ssh.hashCode());
        publishProgress(session.hashCode(), result);
        return session.isConnected();


    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        super.onProgressUpdate(values);

        activeHost.setTitle("Session = " + values[0]);
        if (values[1] == 0) {
            Toast toast = new Toast(activeHost);
            toast.setGravity(Gravity.TOP, 10, 0);
            toast.makeText(activeHost, "SUCCESSFUL", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activeHost, "ERROR", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            ((TextView) activeHost.findViewById(R.id.tvAlias)).setTextColor(Color.GREEN);
        } else {
            ((TextView) activeHost.findViewById(R.id.tvAlias)).setTextColor(Color.RED);
        }
        activeHost.findViewById(R.id.btnSendUrl).setEnabled(true);
        activeHost.showDialog(false);
    }

    @Override
    protected void onCancelled() {
        activeHost.findViewById(R.id.btnSendUrl).setEnabled(true);
        super.onCancelled();
    }

    private String createCommand(Host host) {
        switch (host.getOs()) {
            case Host.OS_UBUNTU:
//                command = "DISPLAY=:0 firefox \"" + url + "\"";
                command = "DISPLAY=:0 nohup gnome-open \"" + url + "\"";
//                    command = "DISPLAY=:0 gvfs-open  \"" + url + "\"";
//                    command = "DISPLAY=:0 x-www-browser  \"" + url + "\"";
                break;
            case Host.OS_WINDOWS:
                command = "cmd.exe /u /c \"start " + url + "\"";
                break;
        }
        return command;
    }


}
