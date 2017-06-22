package com.example.ethan.pokerjournal;

// Represents a Deposit or Withdraw Transaction
public class Bank {
    protected int id; // Deposit/Withdraw ID
    protected String dw; // Deposit/Withdraw Label
    protected String date; // Deposit/Withdraw Date
    protected double amount; // Deposit/Withdraw Amount

    // ~ Set Functions ~
    // Set ID
    public void setId(int id) { this.id = id; }

    // Set Deposit or Withdraw Label
    public void setWd(String dw) { this.dw = dw; }

    // Set Date
    public void setDate(String date) { this.date = date; }

    // Set Amount
    public void setAmount(double amount) {this.amount = amount;}


    // ~ Get Functions ~
    // Get ID
    public int getId() { return id; }

    // Get Deposit or Withdraw Label
    public String getDw() { return dw; }

    // Get Date
    public String getDate() { return date; }

    // Get Amount
    public double getAmount() { return amount; }


    @Override
    // Turns Date into a String
    public String toString() { return date; }

}
