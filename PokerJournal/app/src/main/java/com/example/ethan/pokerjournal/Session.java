package com.example.ethan.pokerjournal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

// ~ Need Comparator to Sort Sessions by Date ~
// Represents a Poker Session/Session
public class Session implements Comparator<Session>
{

    protected int id; // Session ID
    protected String type; // Poker Variation Type
    protected String blinds; // Blinds
    protected String location; // Casino
    protected String date; // Computed Date YYYY-MM-DD
    protected int time; // Session Duration Time
    protected int buyIn; // Total Buy In Amount
    protected int cashOut; // Total Cash Out Amount

    public void setEntries(String type, String blinds, String location, String date, int time, int buyIn, int cashOut)
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

    public void setAll(int id, String type, String blinds, String location, String date, int time, int buyIn, int cashOut)
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

    public int getTime() {return time;}

    public void setTime(int time) {this.time = time;}

    public int getBuyIn() {return buyIn;}

    public void setBuyIn(int buyIn) {this.buyIn = buyIn;}

    public int getCashOut() {return cashOut;}

    public void setCashOut(int cashOut) {this.cashOut = cashOut;}

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
        int netProfit = cashOut - buyIn;
        if (netProfit < 0)
        {
            return " " + getConvertedDateMMddyyyy() + "\n Location: " + location + "\n -$" + -netProfit + " in " + String.format("%.2f", time/60.0) + " hours";
        }
        return " " + getConvertedDateMMddyyyy() + "\n Location: " + location + "\n $" + netProfit + " in " + String.format("%.2f", time/60.0) + " hours";
    }
    // Arbitrary For No Error Alerts
    @Override
    public int compare(Session session, Session t1) {return 0;}
}
