package com.example.ontimeapp;

public class HelperClass {

    String fullName, email;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HelperClass(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public HelperClass() {
    }
}
