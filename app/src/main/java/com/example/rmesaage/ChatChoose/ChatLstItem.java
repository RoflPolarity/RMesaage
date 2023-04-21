package com.example.rmesaage.ChatChoose;

public class ChatLstItem {
    private String lastMessage, name;
    public ChatLstItem(String lastMessage,String name){
        this.lastMessage = lastMessage;
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
