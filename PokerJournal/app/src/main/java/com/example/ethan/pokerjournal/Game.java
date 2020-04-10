package com.example.ethan.pokerjournal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

// ~ Need Comparator to Sort Games by Date ~
// Represents a Poker Session/Game
public class Game implements Comparator<Game>
{

    protected int id; // Game ID
    protected String type; // Poker Variation Type
    protected String blinds; // Blinds
    protected String location; // Casino
    protected String date; // Computed Date YYYY/MM/DD
    protected double time; // Session Duration Time
    protected double buyIn; // Total Buy In Amount
    protected double cashOut; // Total Cash Out Amount

    public void setEntries(String type, String blinds, String location, String date, double time, double buyIn, double cashOut)
    {
        this.id = id;
        this.type = type;
        this.blinds = blinds;
        this.location = location;
        this.date = date;
        this.time = time;
        this.buyIn = buyIn;
        this.cashOut = cashOut;
    }

    public void setAll(int id, String type, String blinds, String location, String date, double time, double buyIn, double cashOut)
    {
        this.id = id;
        this.type = type;
        this.blinds = blinds;
        this.location = location;
        this.date = date;
        this.time = time;
        this.buyIn = buyIn;
        this.cashOut = cashOut;
    }

    // ~ Get Functions ~
    public int getId() {return id;}

    // ~ Set Functions ~
    public void setId(int id) {this.id = id;}

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}

    public String getBlinds() {return blinds;}

    public void setBlinds(String blinds) { this.blinds = blinds;}

    public String getLocation() {return location;}

    public void setLocation(String location) {this.location = location;}

    public String getDate() {return date;}

    public void setDate(String date) {this.date = date;}

    public double getTime() {return time;}

    public void setTime(double time) {this.time = time;}

    public double getBuyIn() {return buyIn;}

    public void setBuyIn(double buyIn) {this.buyIn = buyIn;}

    public double getCashOut() {return cashOut;}

    public void setCashOut(double cashOut) {this.cashOut = cashOut;}

    public String getConvertedDateMMddyyyy()
    {
        String sessionDate = this.date;
        LocalDate localDate = LocalDate.parse(sessionDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        return localDate.format(formatter);
    }

    // Used for Array Adapter to Understand
    @Override
    // Turns Date into a Stringf
    public String toString()
    {
        double netProfit = cashOut - buyIn;
        if (netProfit < 0)
        {
            return " " + getConvertedDateMMddyyyy() + "\n Location: " + location + "\n -$" + -netProfit + " in " + time + " hours";
        }
        return " " + getConvertedDateMMddyyyy() + "\n Location: " + location + "\n $" + netProfit + " in " + time + " hours";
    }
    // Arbitrary For No Error Alerts
    @Override
    public int compare(Game game, Game t1) {return 0;}
}
