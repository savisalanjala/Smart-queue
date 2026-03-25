package com.example.queueueueeeee.repository;

import com.example.queueueueeeee.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepo extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);
}
