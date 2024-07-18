package com.example.primehomeservices;


import java.io.Serializable;

public class MicroserviceClass implements Serializable {
    private String name;
    private int price;
    private int discount;
    private int serviceFee;

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setServiceFee(int serviceFee) {
        this.serviceFee = serviceFee;
    }

    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
    }

    private int grandTotal;

    public MicroserviceClass(String name, int price, int discount, int serviceFee, int grandTotal) {
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.serviceFee = serviceFee;
        this.grandTotal = grandTotal;
    }

    // Getters and setters
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getDiscount() { return discount; }
    public int getServiceFee() { return serviceFee; }
    public int getGrandTotal() { return grandTotal; }
}
