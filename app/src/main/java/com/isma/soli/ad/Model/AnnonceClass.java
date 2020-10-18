package com.isma.soli.ad.Model;

public class AnnonceClass
{
    String title;
    String heart;
    String userID;
    String prix;
    Float distance;
    String name;
    String time;
    String type;
    String key;
    boolean image;

    public Boolean getImage() {
        return image;
    }

    public void setImage(Boolean image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AnnonceClass()
    {

    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnnonceClass(String title, String heart, String userID, String prix, Float distance, String name, String time, String type, String key, boolean image)
    {
        this.title = title;
        this.heart = heart;
        this.userID = userID;
        this.prix = prix;
        this.distance = distance;
        this.name = name;
        this.time = time;
        this.type = type;
        this.key = key;
        this.image=  image;
    }

    public AnnonceClass(String title, String heart, String userID, String prix, Float distance, String name, String time)
    {
        this.title = title;
        this.heart = heart;
        this.userID = userID;
        this.prix = prix;
        this.distance = distance;
        this.name = name;
        this.time = time;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }
}
