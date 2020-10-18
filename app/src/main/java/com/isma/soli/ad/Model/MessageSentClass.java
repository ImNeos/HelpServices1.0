package com.isma.soli.ad.Model;

public class MessageSentClass
{
    String key;
    String userID;
    String userName;
    String title;
    String message;
    String time_stamp;

    public MessageSentClass(String key, String userID, String userName, String title, String message, String time_stamp) {
        this.key = key;
        this.userID = userID;
        this.userName = userName;
        this.title = title;
        this.message = message;
        this.time_stamp = time_stamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
}
