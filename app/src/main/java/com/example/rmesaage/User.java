package com.example.rmesaage;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private String ip;
    public User(String username, String password, String ip){
        this.username = username;
        this.password = password;
        this.ip = ip;
    }

    public String toCSV(){
        return username + ","
                + password + ","+
                ip;


    }

    @Override
    public String toString() {
        return username + ","
                + password + ","+
                ip;
    }

    public static User valueOf(String s){
        System.out.println(s);
        String[] str = s.split(",");
        return new User(str[0],str[1],str[2]);
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
}
