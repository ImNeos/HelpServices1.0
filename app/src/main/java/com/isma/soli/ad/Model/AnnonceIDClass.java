package com.isma.soli.ad.Model;

public class AnnonceIDClass
{
    String ID;
    float distance;


    public AnnonceIDClass(String ID, float distance) {
        this.ID = ID;
        this.distance = distance;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
