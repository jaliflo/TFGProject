package com.example.franciscojavier.tfgproject.datamodel;


public class MessageToServer {
    private String content;
    private int userEmitter;
    private int userReceiver;

    public MessageToServer(){
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserEmitter() {
        return userEmitter;
    }

    public void setUserEmitter(int userEmitter) {
        this.userEmitter = userEmitter;
    }

    public int getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(int userReceiver) {
        this.userReceiver = userReceiver;
    }
}
