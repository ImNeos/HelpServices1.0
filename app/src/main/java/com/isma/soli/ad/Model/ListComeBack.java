package com.isma.soli.ad.Model;

public class ListComeBack
{
    String phonenumber;
    String uid;

    public ListComeBack(String phonenumber, String uid) {
        this.phonenumber = phonenumber;
        this.uid = uid;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
