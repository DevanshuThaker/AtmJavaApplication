package com.example.atmjava.service;

import com.example.atmjava.model.AppUser;

public interface ATMService {
    AppUser createUser(String name, String pin);         // used in create
    boolean loginUser(String userId, String pin);        // used in controller login (simple check)
    AppUser getUserById(String userId);                  // <<--- required by controller (getUserById)
    void deposit(String userId, double amount);
    boolean withdraw(String userId, double amount);
    boolean transfer(String fromUserId, String targetAccount, double amount);
}
