package com.example.rmesaage.Chat;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    int id;
    private String messageUser;
    private String text;
    private ArrayList<byte[]> bitMaps;
    public Message(String messageUser,String text, int id){
        this.messageUser = messageUser;
        this.text = text;
        this.id = id;

    }
    public Message(String messageUser,String text){
        this.messageUser = messageUser;
        this.text = text;
    }
    public Message(String messageUser, ArrayList<byte[]> bitMaps){
        this.messageUser = messageUser;
        this.bitMaps = bitMaps;
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

    public ArrayList<byte[]> getBitMaps() {
        return bitMaps;
    }
}
