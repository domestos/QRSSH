package com.example.vpelenskyi.qrssh.sshclient;

import android.util.Log;

import com.example.vpelenskyi.qrssh.MainQRSSH;
import com.example.vpelenskyi.qrssh.host.Host;
import com.example.vpelenskyi.qrssh.host.NewHost;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by v.pelenskyi on 03.02.2016.
 */
public class SSH {

    private static SSH instanceSSH;
    private static Session session;

    private int timeOut = 5000;
    private String TAG = "ssh_log";
    private JSch jSch = new JSch();
    private Channel channelExe;


    private SSH() {
    }

    public static synchronized SSH getInstanceSSH() {
        if (instanceSSH == null) {
            instanceSSH = new SSH();
        }
        return instanceSSH;
    }

    public static void setSession(Session session) {
        SSH.session = session;
    }

    public synchronized boolean openSession(Host host) {
        if(host != null) {
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
//            Log.i(TAG, "open() session = " + session.hashCode());
            } catch (JSchException e) {
                e.printStackTrace();
                Log.e(TAG, "session.connect give the error " + e);
            }

            return session.isConnected();
        }
        Log.i(TAG, "Host null " );
        return false;
    }

    public void openChannel(Session session) {

        if (session != null & session.isConnected()) {
            try {
                channelExe = session.openChannel("exec");
                Log.d(TAG, "CreateChannel : " + channelExe.hashCode());
            } catch (JSchException e) {
                System.out.println("Error connect: " + e);
                e.printStackTrace();
            }
        }
    }

    public void closeChenal() {
        if (channelExe != null & channelExe.isConnected()) {
            channelExe.disconnect();
        }
    }

    public void close() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            Log.d(TAG, "close() connected session = " + session.isConnected());
            Log.d(TAG, "close() session = " + session.hashCode());
        }
    }

    public Session getSession() {
        return session;
    }

    public synchronized int sendCommand(String command) {
        if (channelExe != null && !channelExe.isClosed()) {
            ((ChannelExec) channelExe).setCommand(command);
            ((ChannelExec) channelExe).setErrStream(System.err);
            Log.d(TAG, "setCommand in channel " + channelExe.hashCode());
            InputStream in = null;
            try {
                in = channelExe.getInputStream();
                channelExe.connect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSchException e) {
                e.printStackTrace();
            }
            startTimeOutChenalExe(35);
            return answerFromChennalExe(in);
        }
        //error value
        return 404;
    }

    /**
     * @param in InputStream
     * @return Exit Status ChannelExe
     */
    private int answerFromChennalExe(InputStream in) {
        byte[] tmp = new byte[1024];
        if (in != null) {
            while (true) {
                try {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0) break;
                        Log.d(TAG, "ssh answer: " + new String(tmp, 0, i));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    channelExe.sendSignal("exit");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (channelExe.isClosed()) {
                    channelExe.disconnect();
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception ee) {
                }
            }
        }
        return channelExe.getExitStatus();
    }

    /**
     * forcible closes the channel if he finished work but dose not closes
     *
     * @param timeOut seconds
     */
    private void startTimeOutChenalExe(final int timeOut) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(timeOut);
                    if (!channelExe.isClosed()) {
                        channelExe.disconnect();
                        Log.d(TAG, "i finish waite..channelExe.isClosed() " + channelExe.isClosed());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
