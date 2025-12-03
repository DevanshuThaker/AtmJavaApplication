package com.example.atmjava.repository;

import com.example.atmjava.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
    // method your CustomUserDetailsService already expects
    AppUser findByUserID(String userID);
}
