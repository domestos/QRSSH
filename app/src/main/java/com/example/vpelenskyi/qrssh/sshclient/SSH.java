package com.example.vpelenskyi.qrssh.sshclient;

import android.util.Log;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * Created by varenik on 01.02.16.
 */
public class SSH {
    private int timeOut = 5000;
    private String TAG = "log_ssh";
    private Session session;
    private JSch jSch;


    public boolean openSession(String user, String host, int port, String pass) {
        jSch = new JSch();
        try {
            session = jSch.getSession(user, host, port);
        } catch (JSchException e) {
            Log.e(TAG, "jSch.getSession(user, host, port) give exception " + e);
            e.printStackTrace();
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("compression.s2c", "zlib,none");
        config.put("compression.c2s", "zlib,none");
        session.setConfig(config);
        session.setPassword(pass);

        try {

            session.connect(timeOut);
        } catch (JSchException e) {
            Log.e(TAG, " session.connect(timeOut); give exception " + e);
            e.printStackTrace();
        }

        return session.isConnected();
    }




}
