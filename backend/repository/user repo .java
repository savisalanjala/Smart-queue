package com.example.queueueueeeee.repository;

import com.example.queueueueeeee.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
