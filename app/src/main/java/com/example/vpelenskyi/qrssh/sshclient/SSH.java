package com.example.vpelenskyi.qrssh.sshclient;

import android.util.Log;

import com.example.vpelenskyi.qrssh.host.Host;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * Created by v.pelenskyi on 03.02.2016.
 */
public class SSH {
    private int timeOut = 3000;
    private String TAG = "ssh_log";
    private Session session;
    private JSch jSch;

    public boolean openSession(Host host) {
        jSch = new JSch();
        try {
            session = jSch.getSession(host.getUsername(), host.getHost(), host.getPort());
        } catch (JSchException e) {
            e.printStackTrace();
            Log.e(TAG, "session = jSch.getSession() give the error " + e);
        }

        session.setPassword(host.getPassword());

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("compression.s2c", "zlib,none");
        config.put("compression.c2s", "zlib,none");
        session.setConfig("PreferredAuthentications", "password");
        session.setConfig(config);

        try {
            session.connect(timeOut);
        } catch (JSchException e) {
            e.printStackTrace();
            Log.e(TAG, "session.connect give the error " + e);
        }

        return session.isConnected();
    }

    public void close() {
        if (session.isConnected()) {
            session.disconnect();
        }
    }

}
