package com.example.atmjava.service;

public class WebUser {
    private final String userId;
    private final String accountNumber;

    public WebUser(String userId, String accountNumber) {
        this.userId = userId;
        this.accountNumber = accountNumber;
    }

    public String getUserId() {
        return userId;
    }

    // Controller expects getAccountNumber()
    public String getAccountNumber() {
        return accountNumber;
    }
}
