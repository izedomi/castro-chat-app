package com.example.castro.Models;

public class UserModel {

    int userImage;
    String userName;
    String userMsg;

    public UserModel(int userImage, String userName, String userMsg) {
        this.userImage = userImage;
        this.userName = userName;
        this.userMsg = userMsg;
    }

    public int getUserImage() {
        return userImage;
    }

    public void setUserImage(int userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(String userMsg) {
        this.userMsg = userMsg;
    }
}
