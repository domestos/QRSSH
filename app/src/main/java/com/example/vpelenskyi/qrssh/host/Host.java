package com.example.vpelenskyi.qrssh.host;

/**
 * Created by v.pelenskyi on 23.12.2015.
 */
public class Host {

    private String alias;
    private String host;
    private String username;
    private String password;
    private int port;
    private String os;
    public Host(){}
    public Host(String alias, String host, int port, String username, String password,  String os) {
        this.alias = alias;
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.os = os;
    }


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }


    @Override
    public String toString() {
        return "Host{" +
                "alias='" + alias + '\'' +
                ", host='" + host + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", port=" + port +
                ", os='" + os + '\'' +
                '}';
    }
}
