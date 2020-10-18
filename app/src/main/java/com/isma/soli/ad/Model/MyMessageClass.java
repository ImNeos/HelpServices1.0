package com.isma.soli.ad.Model;

public class MyMessageClass
{
    String Name;
    String phonenumber;
    String message;
    String key;
    String time;
    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MyMessageClass()
    {

    }

    public MyMessageClass(String name, String phonenumber, String message, String key, String time, String text) {
        Name = name;
        this.phonenumber = phonenumber;
        this.message = message;
        this.key = key;
        this.time = time;
        this.text= text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
