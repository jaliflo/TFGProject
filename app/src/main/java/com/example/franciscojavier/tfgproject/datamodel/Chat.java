package com.example.franciscojavier.tfgproject.datamodel;

import java.util.Date;

public class Chat {
    private int id;
    private Date startDateTime, finishDateTime;
    private User user;

    public Chat(){}

    public Chat(Date startDateTime, Date finishDateTime, User user){
        this.startDateTime = startDateTime;
        this.finishDateTime = finishDateTime;
        this.user = user;
    }

    public Chat(int id, Date startDateTime, Date finishDateTime, User user){
        this.id = id;
        this.startDateTime = startDateTime;
        this.finishDateTime = finishDateTime;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getFinishDateTime() {
        return finishDateTime;
    }

    public void setFinishDateTime(Date finishDateTime) {
        this.finishDateTime = finishDateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
