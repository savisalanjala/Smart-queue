package com.example.queueueueeeee.repository;

import com.example.queueueueeeee.model.PlaceService;
import com.example.queueueueeeee.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceServiceRepo extends JpaRepository<PlaceService, Long> {
    List<PlaceService> findByPlace(Place place);

}
