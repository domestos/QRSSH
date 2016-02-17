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
    private boolean booleanExit;

    public void link(ActiveHost activeHost) {
        this.activeHost = activeHost;
    }

    public void unLink() {
        this.activeHost = null;
    }

    /**
     * Mtehod checkSession():
     * restart (init) session if she was lost (disconnect)
     */
    private void checkSession(Host host) {
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

    /**
     * init url, ssh
     * locks the button and showing progressDialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        url = activeHost.getUrl();
        ssh = SSH.getInstanceSSH();
        activeHost.findViewById(R.id.btnSendUrl).setEnabled(true);
        activeHost.showDialog(true);
    }

    /**
     * Get session, openChannel, sendCommand
     *
     * @param params Host
     * @return boolean - result connect to SSH host
     */
    @Override
    protected Boolean doInBackground(Host... params) {

        for (Host host : params) {
            command = createCommand(host, url);
//         restart (init) session if she was lost (disconnect)
            checkSession(host);
        }
        session = ssh.getSession();

        Thread threadExit = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int result = 404;
                Log.i(TAG, "Status Session " + session.isConnected());
                ssh.openChannel(session);
                result = ssh.sendCommand(command);
                Log.i(TAG, " AsynkActivityHost ssh.getSession() " + ssh.getSession().hashCode());
                Log.i(TAG, " AsynkActivityHost ssh " + ssh.hashCode());
                publishProgress(session.hashCode(), result);

                booleanExit = true;
            }
        });

        threadExit.start();
        //remove the load from the CPU
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (booleanExit || isCancelled()) {
                Log.i(TAG, "i finished");
                break;
            }
        }

        return session.isConnected();
    }

    /**
     * Shows result method sendCommand in UI
     *
     * @param values int[0] result (if return 0 - successful another error)
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        activeHost.setTitle("Session = " + values[0]);
        if (values[1] == 0) {
            showResult("SUCCESSFUL");
        } else {
            showResult("ERROR");
        }
    }

    private void showResult(String string) {
        Toast.makeText(activeHost, string, Toast.LENGTH_LONG).show();
    }

    /**
     * unlocks button
     *
     * @param aBoolean
     */
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

    /**
     * unlocks button
     */
    @Override
    protected void onCancelled() {
        activeHost.findViewById(R.id.btnSendUrl).setEnabled(true);
        super.onCancelled();
    }

    /**
     * looking what os uses will be to create command
     *
     * @param host Host
     * @param url  String
     * @return String command
     */
    private String createCommand(Host host, String url) {
        if (url != null && !url.isEmpty()) {
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
            Log.i(TAG, "command = " + command);
        } else {
            command = null;
            Log.i(TAG, "url is empty");
        }
        return command;
    }


}
