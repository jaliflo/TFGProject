package com.example.franciscojavier.tfgproject.datamodel;

public class ChatMessage {
    private int id;
    private String content;
    private boolean sendOrReceive;
    private Chat chat;
    private User user;

    public ChatMessage(){
    }

    public ChatMessage(String content, boolean sendOrReceive, Chat chat, User user){
        this.content = content;
        this.sendOrReceive = sendOrReceive;
        this.chat = chat;
        this.user = user;
    }

    public ChatMessage(int id, String content, boolean sendOrReceive, Chat chat, User user){
        this.id = id;
        this.content = content;
        this.sendOrReceive = sendOrReceive;
        this.chat = chat;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSend() {
        return sendOrReceive;
    }

    public void setSendOrReceive(boolean sendOrReceive) {
        this.sendOrReceive = sendOrReceive;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
