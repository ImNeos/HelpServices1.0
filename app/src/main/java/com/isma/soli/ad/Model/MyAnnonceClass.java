package com.isma.soli.ad.Model;

public class MyAnnonceClass
{
    String key;
    String title;
    String heart;
    String postcode;
    String time;
    String prix;
    String type;

    public MyAnnonceClass(String key, String title, String heart, String postcode, String time, String prix, String type) {
        this.key = key;
        this.title = title;
        this.heart = heart;
        this.postcode = postcode;
        this.time = time;
        this.prix = prix;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }
}
