package com.example.ethan.pokerjournal;

import java.util.Comparator;

// Represents a Deposit or Withdraw Bank Transaction
public class Bank {

    protected int id; // Transaction ID
    protected String type; // Transaction Type
    protected String date; // Transaction Displayed Date MM/DD/YYYY
    protected String date2; // Transaction Computed Date YYYY/MM/DD
    protected double amount; // Transaction Amount

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
    public String toString()
    {
        return " " + date + "\n " + type + "\n $" + amount;
    }

    // Arbitrary For No Error Alerts
    public int compare(Bank bank, Bank t1) {return 0;}

}
