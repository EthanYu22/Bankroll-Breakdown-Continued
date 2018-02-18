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
    // Set ID
    public void setId(int id) {
        this.id = id;
    }

    // Set Poker Variation Type
    public void setType(String type) {
        this.type = type;
    }

    // Set Blinds
    public void setBlinds(String blinds) { this.blinds = blinds; }

    // Set Location
    public void setLocation(String location) {
        this.location = location;
    }

    // Set Date
    public void setDate(String date) {
        this.date = date;
    }

    // Set Duration
    public void setTime(double time) {
        this.time = time;
    }

    // Set Buy In
    public void setBuyIn(double buyIn) {
        this.buyIn = buyIn;
    }

    // Set Cash Out
    public void setCashOut(double cashOut) {
        this.cashOut = cashOut;
    }


    // ~ Get Functions ~
    // Get ID
    public int getId() {
        return id;
    }

    // Get Poker Variation Type
    public String getType() {
        return type;
    }

    // Get Blinds
    public String getBlinds() { return blinds; }

    // Get Location
    public String getLocation() {
        return location;
    }

    // Get Date
    public String getDate() {
        return date;
    }

    // Get Duration
    public double getTime() {
        return time;
    }

    // Get Buy In
    public double getBuyIn() { return buyIn; }

    // Get Cash Out
    public double getCashOut() {
        return cashOut;
    }


    // Used for Array Adapter to Understand
    @Override
    // Turns Date into a String
    public String toString() {
        return date;
    }

    // Helps Compare Dates to Sort Them
    public int compareTo(Game other) {
        return date.compareTo(other.date);
    }

    // Sorts the Games
    public static Comparator<Game> COMPARE_BY_DATE = new Comparator<Game>() {
        public int compare(Game one, Game other) {
            return one.date.compareTo(other.date);
        }
    };

    // Arbitrary For No Error Alerts
    @Override
    public int compare(Game game, Game t1) {
        return 0;
    }
}
