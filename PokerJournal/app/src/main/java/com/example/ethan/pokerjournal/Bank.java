package com.example.ethan.pokerjournal;

/**
 * Created by Andrew on 3/11/2016.
 */
public class Bank {
    private int id;
    private String dw;
    private double amount;

    //set functions
    public void setId(int id) {
        this.id = id;
    }

    public void setWd(String dw) {
        this.dw = dw;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    //get functions
    public int getId() {
        return id;
    }

    public String getDw() {
        return dw;
    }

    public double getAmount() {
        return amount;
    }
}
