package com.example.rmesaage;

import java.io.Serializable;

public class Response<T>  implements Serializable {
    private T data;
    private String comma;
    private String username;
    private String password;
    private String sendTo;

    public Response(String comma, String username, String password, String sendTo,T data) {
        this.data = data;
        this.comma = comma;
        this.username = username;
        this.password = password;
        this.sendTo = sendTo;
    }

    public Response(T data){
        this.data = data;
    }

    public String getComma() {
        return comma;
    }

    public String getSendTo() {
        return sendTo;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
}
