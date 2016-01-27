package com.example.vpelenskyi.qrssh.sshclient;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

public class ClientSsh extends AsyncTask<Void, Integer, Void> {

    String command = " export DISPLAY=:0 && gnome-open \"https://vk.com \" ";

    private String USER = "varenik";
    private String HOST = "192.168.0.102";
    private String PASS = "4554722";
    private int PORT = 22;

    private Session session;
    private Channel channelExe;


    private  Session createSession() {
        JSch jSch = new JSch();
        try {
            System.out.println("creat session...");
            session = jSch.getSession(USER, HOST, PORT);
            session.setPassword(PASS);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("compression.s2c", "zlib,none");
            config.put("compression.c2s", "zlib,none");

            session.setConfig(config);
        } catch (JSchException e) {
            System.out.println("Error creat session: " + e);
            e.printStackTrace();
        }


        return session;
    }


    private  void close(Session session, Channel channelExe) {
        channelExe.disconnect();
        session.disconnect();
    }

    private  void setCommand(Channel channelExe, String command) {
        ((ChannelExec)channelExe).setCommand(command);
        ((ChannelExec) channelExe).setErrStream(System.err);
        try {
            channelExe.connect();
        } catch (JSchException e) {
            e.printStackTrace();
            System.out.println("Error cannot connetc to channel :" + e);
        }


    }


    private  Channel createChannelExe(Session session) {
        try {
            System.out.println(" connect session ...");
            session.connect();
        } catch (JSchException e) {
            System.out.println("Error connect: " + e);
            e.printStackTrace();
        }


        try {
            channelExe = session.openChannel("exec");
        } catch (JSchException e) {
            System.out.println("Error connect: " + e);
            e.printStackTrace();
        }

        return channelExe;
    }


    @Override
    protected Void doInBackground(Void... params) {

        session = createSession();
        channelExe = createChannelExe(session);
        setCommand(channelExe, command);
        close(session, channelExe);


        return null;
    }

}
