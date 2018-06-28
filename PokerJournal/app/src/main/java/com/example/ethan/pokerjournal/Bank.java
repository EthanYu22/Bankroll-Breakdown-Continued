package com.example.ethan.pokerjournal;

import java.util.Comparator;

// Represents a Deposit or Withdraw Bank Transaction
public class Bank {

    protected int id; // Deposit/Withdraw ID
    protected String type; // Deposit/Withdraw Type
    protected String date; // Deposit/Withdraw Displayed Date
    protected String date2; // Deposit/Withdraw Computed Date
    protected double amount; // Deposit/Withdraw Amount

    // ~ Set Functions ~
    public void setId(int id) {this.id = id;}
    public void setType(String type) {this.type = type;}
    public void setDate(String date) {this.date = date;}
    public void setDate2(String date2) {this.date2 = date2;}
    public void setAmount(double amount) {this.amount = amount;}
    public void setEntries(String type, String date, String date2, double amount){
        this.type = type;
        this.date = date;
        this.date2 = date2;
        this.amount = amount;
    }
    public void setAll(int id, String type, String date, String date2, double amount){
        this.id = id;
        this.type = type;
        this.date = date;
        this.date2 = date2;
        this.amount = amount;
    }

    // ~ Get Functions ~
    public int getId() {return id;}
    public String getType() {return type;}
    public String getDate() {return date;}
    public String getDate2() {return date2;}
    public double getAmount() {return amount;}

    // Used for Array Adapter to Understand
    @Override
    // Turns Date into a String
    public String toString() {return date;}

    // Arbitrary For No Error Alerts
    public int compare(Bank bank, Bank t1) {return 0;}

}
