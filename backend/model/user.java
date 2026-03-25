package com.example.queueueueeeee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;  // MAIN LOGIN FIELD

    @Column(nullable = false, unique = true)
    private String email;


    @Column(nullable = false)
    private String password;


}
