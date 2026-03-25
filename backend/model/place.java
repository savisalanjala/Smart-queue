package com.example.queueueueeeee.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private String contactNumber;

    private LocalTime openingTime;
    private LocalTime closingTime;

    @ElementCollection(targetClass = Day.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "place_leave_days",
            joinColumns = @JoinColumn(name = "place_id")
    )
    @Column(name = "leave_day")
    private List<Day> leaveDays;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PlaceService> services;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference
    private Admin admin;

    public Place() {}
}
