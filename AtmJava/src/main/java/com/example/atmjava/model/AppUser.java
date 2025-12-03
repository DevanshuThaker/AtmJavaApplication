package com.example.atmjava.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @Column(name = "userid")
    // DO NOT use @GeneratedValue because you generate the userID yourself (UUID)
    private String userID;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "balance")
    private double balance;

    @Column(name = "name")
    private String name;

    @Column(name = "userpin")
    private String userPIN;

    public AppUser() {}

    // getters & setters
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserPIN() { return userPIN; }
    public void setUserPIN(String userPIN) { this.userPIN = userPIN; }
}
