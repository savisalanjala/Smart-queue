package com.example.queueueueeeee.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PlaceService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;

    private double price;

    // HARD CODED SERVICE TIME (minutes)
    private int estimatedTimeMinutes;

    @ManyToOne
    @JoinColumn(name = "place_id")
    @JsonBackReference
    private Place place;
}
