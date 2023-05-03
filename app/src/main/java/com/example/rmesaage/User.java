package com.example.rmesaage;

import java.io.Serializable;
import java.util.Arrays;

public class User implements Serializable {
    private String username;
    private String password;
    private String ip;
    private String port;
    public User(String username, String password, String ip,String port){
        this.username = username;
        this.password = password;
        this.ip = ip;
        this.port = port;
    }

    public String toCSV(){
        return username + ","
                + password + ","+
                ip+","+
                port;


    }

    @Override
    public String toString() {
        return username + ","
                + password + ","+
                ip+","+
                port;
    }

    public static User valueOf(String s){
        String[] str = s.split(",");
        String[] ip = str[2].split(":");
        return new User(str[0],str[1],ip[0],ip[1]);
    }

    public static User valueOfCSV(String s){
        String[] str = s.split(",");
        return new User(str[0],str[1],str[2],str[3]);
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
