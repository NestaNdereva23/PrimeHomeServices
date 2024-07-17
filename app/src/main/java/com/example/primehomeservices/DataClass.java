package com.example.primehomeservices;

public class DataClass {
    private String imageURL , servicename;

    public DataClass(){

    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public DataClass(String imageURL, String servicename) {
        this.imageURL = imageURL;
        this.servicename = servicename;
    }
}
