package com.example.queueueueeeee.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tokenNumber;

    private LocalDate visitDate;
    private LocalDate bookingDate;

    private LocalDateTime servedAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private Place place;

    @ManyToOne
    private PlaceService service;

    @Enumerated(EnumType.STRING)
    private TokenStatus status;
}
