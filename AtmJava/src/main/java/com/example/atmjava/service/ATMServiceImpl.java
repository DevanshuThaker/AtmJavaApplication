package com.example.atmjava.service;

import com.example.atmjava.model.AppUser;
import com.example.atmjava.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ATMServiceImpl implements ATMService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public AppUser createUser(String name, String pin) {
        AppUser u = new AppUser();
        String generatedUserId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        u.setUserID(generatedUserId);
        u.setName(name);
        u.setUserPIN(pin);
        u.setAccountNumber(generateAccountNumber());
        u.setBalance(0.0);
        return userRepository.save(u);
    }

    private String generateAccountNumber() {
        return "AC" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    @Override
    public boolean loginUser(String userId, String pin) {
        AppUser u = userRepository.findByUserID(userId);
        if (u == null) return false;
        return u.getUserPIN().equals(pin);
    }

    @Override
    public AppUser getUserById(String userId) {
        AppUser u = userRepository.findByUserID(userId);
        if (u == null) throw new IllegalArgumentException("User not found: " + userId);
        return u;
    }

    @Override
    public void deposit(String userId, double amount) {
        AppUser u = getUserById(userId);
        u.setBalance(u.getBalance() + amount);
        userRepository.save(u);
    }

    @Override
    public boolean withdraw(String userId, double amount) {
        AppUser u = getUserById(userId);
        if (u.getBalance() < amount) return false;
        u.setBalance(u.getBalance() - amount);
        userRepository.save(u);
        return true;
    }

    @Override
    public boolean transfer(String fromUserId, String targetAccount, double amount) {
        AppUser from = getUserById(fromUserId);

        Optional<AppUser> optTarget = userRepository.findAll()
                .stream()
                .filter(x -> targetAccount.equals(x.getAccountNumber()))
                .findFirst();

        if (optTarget.isEmpty()) return false;

        AppUser to = optTarget.get();

        if (from.getBalance() < amount) return false;

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        userRepository.save(from);
        userRepository.save(to);

        return true;
    }
}
