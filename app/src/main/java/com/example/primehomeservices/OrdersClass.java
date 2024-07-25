package com.example.primehomeservices;

public class OrdersClass {
    private String userId;
    private int itemTotal;
    private int itemsDiscount;
    private int serviceFee;
    private int grandTotal;
    private String status;
    private String location;
    private String date;
    private String time;
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrdersClass() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(int itemTotal) {
        this.itemTotal = itemTotal;
    }

    public int getItemsDiscount() {
        return itemsDiscount;
    }

    public void setItemsDiscount(int itemsDiscount) {
        this.itemsDiscount = itemsDiscount;
    }

    public int getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(int serviceFee) {
        this.serviceFee = serviceFee;
    }

    public int getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public OrdersClass(String userId, int itemTotal, int itemsDiscount, int serviceFee, int grandTotal, String status, String location, String date, String time) {
        this.userId = userId;
        this.itemTotal = itemTotal;
        this.itemsDiscount = itemsDiscount;
        this.serviceFee = serviceFee;
        this.grandTotal = grandTotal;
        this.status = status;
        this.location = location;
        this.date = date;
        this.time = time;
        this.orderId = orderId;
    }

}
