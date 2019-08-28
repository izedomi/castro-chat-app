package com.example.castro.Models;

public class AllUserModel {


    String thumbnail;
    String fullname;
    String department;
    String rank;
    String image_url;
    String gender;
    String staff_id;
    String username;
    String setup;

    public AllUserModel(){}

    public AllUserModel(String thumbnail, String fullname, String department, String rank, String image_url, String gender, String staff_id, String username, String setup) {
        this.thumbnail = thumbnail;
        this.fullname = fullname;
        this.department = department;
        this.rank = rank;
        this.image_url = image_url;
        this.gender = gender;
        this.staff_id = staff_id;
        this.username = username;
        this.setup = setup;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSetup() {
        return setup;
    }

    public void setSetup(String setup) {
        this.setup = setup;
    }




}
