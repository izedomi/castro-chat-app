package com.example.castro.Models;

public class RequestsModel {

    String senderId;

    public RequestsModel(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
