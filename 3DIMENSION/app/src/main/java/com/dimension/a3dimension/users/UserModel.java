package com.dimension.a3dimension.users;

import java.sql.Timestamp;

public class UserModel {
    String username;
    String password;
    String role;



    String date;



    UserModel(){}

    public UserModel(String username, String password, String role,String date) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.date=date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
