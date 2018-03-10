package com.example.ethan.pokerjournal;

// Represents a Deposit or Withdraw Transaction
public class Bank {

    protected int id; // Deposit/Withdraw ID
    protected String type; // Deposit/Withdraw Label
    protected String date; // Deposit/Withdraw Date
    protected double amount; // Deposit/Withdraw Amount

    // ~ Set Functions ~
    public void setId(int id) {this.id = id;}
    public void setType(String type) {this.type = type;}
    public void setDate(String date) {this.date = date;}
    public void setAmount(double amount) {this.amount = amount;}
    public void setEntries(String type, String date, double amount){
        this.type = type;
        this.date = date;
        this.amount = amount;
    }
    public void setAll(int id, String type, String date, double amount){
        this.id = id;
        this.type = type;
        this.date = date;
        this.amount = amount;
    }

    // ~ Get Functions ~
    public int getId() {return id;}
    public String getType() {return type;}
    public String getDate() {return date;}
    public double getAmount() {return amount;}

    @Override
    // Turns Date into a String
    public String toString() {return date;}

}
