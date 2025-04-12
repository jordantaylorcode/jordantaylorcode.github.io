package com.zybooks.jordaninventoryapp.model;

public class User {

    // database fields
    private String username;
    private String password;


    public User() {
        username = "";
        password = "";
    }


    // accessors for Username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // accessors for Password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
