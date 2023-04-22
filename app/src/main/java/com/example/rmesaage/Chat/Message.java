package com.example.rmesaage.Chat;

import java.io.Serializable;

public class Message implements Serializable {
    int id;
    private String messageUser;
    private String text;
    public Message(String messageUser,String text, int id){
        this.messageUser = messageUser;
        this.text = text;
        this.id = id;

    }

    public int getId() {
        return id;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public String getText() {
        return text;
    }
}
