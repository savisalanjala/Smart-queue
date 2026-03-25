package com.example.queueueueeeee.repository;

import com.example.queueueueeeee.model.Admin;
import com.example.queueueueeeee.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepo extends JpaRepository<Place, Long> {

    List<Place> findByAdmin(Admin admin);
    Place findByName(String name);

}
