package com.example.ethan.pokerjournal;

import java.util.Comparator;

// ~ Need Comparator to Sort Games by Date ~
// Represents a Poker Session/Game
public class Game implements Comparator<Game> {

    protected int id; // Game ID
    protected String type; // Poker Variation Type
    protected String blinds; // Blinds
    protected String location; // Casino
    protected String date; // Date
    protected double time; // Session Duration Time
    protected double buyIn; // Total Buy In Amount
    protected double cashOut; // Total Cash Out Amount

    // ~ Set Functions ~
    public void setId(int id) {this.id = id;}
    public void setType(String type) {this.type = type;}
    public void setBlinds(String blinds) { this.blinds = blinds;}
    public void setLocation(String location) {this.location = location;}
    public void setDate(String date) {this.date = date;}
    public void setTime(double time) {this.time = time;}
    public void setBuyIn(double buyIn) {this.buyIn = buyIn;}
    public void setCashOut(double cashOut) {this.cashOut = cashOut;}

    // ~ Get Functions ~
    public int getId() {return id;}
    public String getType() {return type;}
    public String getBlinds() {return blinds;}
    public String getLocation() {return location;}
    public String getDate() {return date;}
    public double getTime() {return time;}
    public double getBuyIn() {return buyIn;}
    public double getCashOut() {return cashOut;}

    // Used for Array Adapter to Understand
    @Override
    // Turns Date into a String
    public String toString() {return date;}

    // Helps Compare Dates to Sort Game Log
    public int compareTo(Game other) {return date.compareTo(other.date);}

    // Sorts the Games
    public static Comparator<Game> COMPARE_BY_DATE = new Comparator<Game>() {
        public int compare(Game one, Game other) {return one.date.compareTo(other.date);}
    };

    // Arbitrary For No Error Alerts
    @Override
    public int compare(Game game, Game t1) {return 0;}
}
