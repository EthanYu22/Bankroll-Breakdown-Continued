package com.example.ethan.pokerjournal;

/**
 * Created by Andrew on 3/10/2016.
 */
public class Game {
    private int id; //maybe long?
    private String type;
    private String location;
    private String date;
    private int time;
    private double buyIn;
    private double cashOut;

    //set functions

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setBuyIn(double buyIn) {
        this.buyIn = buyIn;
    }

    public void setCashOut(double cashOut) {
        this.cashOut = cashOut;
    }

    //get functions
    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public int getTime() {
        return time;
    }

    public double getBuyIn() {
        return buyIn;
    }

    public double getCashOut() {
        return cashOut;
    }

    //used for array adapter
    @Override
    public String toString() {
        return date;
    }
}
