package com.example.queueueueeeee.service;

import com.example.queueueueeeee.model.*;
import com.example.queueueueeeee.repository.AdminRepo;
import com.example.queueueueeeee.repository.PlaceRepo;
import com.example.queueueueeeee.repository.PlaceServiceRepo;
import com.example.queueueueeeee.repository.TokenRepo;
import com.example.queueueueeeee.repository.PlaceServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private PlaceRepo placeRepo;

    @Autowired
    private TokenRepo tokenRepo;

    @Autowired
    private PlaceServiceRepo placeServiceRepo;


    public String register(Admin admin) {
        if (adminRepo.findByUsername(admin.getUsername()) != null) {
            return "Admin username already exists";
        }
        adminRepo.save(admin);
        return "Admin registered successfully";
    }

    public String login(String username, String password) {
        Admin admin = adminRepo.findByUsername(username);
        if (admin == null) return "Admin not found";
        if (!admin.getPassword().equals(password)) return "Invalid credentials";
        return "Login successful";
    }


    public List<Place> getPlacesOfAdmin(String username) {
        Admin admin = adminRepo.findByUsername(username);
        return placeRepo.findByAdmin(admin);
    }

    public String addPlace(Place place, String username) {

        Admin admin = adminRepo.findByUsername(username);

        if (admin == null) {
            return "Admin not found";
        }

        place.setAdmin(admin);
        admin.setPlace(place);


        placeRepo.save(place);

        return "Place added successfully for " + username;
    }

    public List<Place> getAllPlaces() {
        return placeRepo.findAll();
    }




//    public String addService(Long placeId, PlaceService service) {
//        Place place = placeRepo.findById(placeId).orElse(null);
//        if (place == null) return "Place not found";
//
//        service.setPlace(place);
//        placeServiceRepo.save(service);
//
//        return "Service added successfully to " + place.getName();
//    }

    public String addServiceForAdmin(String adminUsername, PlaceService service) {
        // Find admin
        Admin admin = adminRepo.findByUsername(adminUsername);
        if (admin == null) return "Admin not found";

        // Get admin's place
        Place place = admin.getPlace();  // assuming one-to-one
        if (place == null) return "No place assigned to admin";

        // Assign place to service
        service.setPlace(place);

        // Set default estimated time if you want (e.g., 0)
        service.setEstimatedTimeMinutes(0);

        // Save service
        placeServiceRepo.save(service);

        return "Service added successfully to " + place.getName();
    }

    public List<PlaceService> getServices(Long placeId) {
        Place place = placeRepo.findById(placeId).orElse(null);
        if (place == null) return null;

        return placeServiceRepo.findByPlace(place);
    }

    public List<Token> getLiveQueueByAdmin(String adminUsername) {

        Admin admin = adminRepo.findByUsername(adminUsername);
        if (admin == null) {
            return List.of();
        }

        Place place = admin.getPlace(); // admin ↔ place mapping
        if (place == null) {
            return List.of();
        }

        LocalDate today = LocalDate.now();


        return tokenRepo.findByPlaceAndVisitDateOrderByTokenNumberAsc(place, today);
    }



    public List<Token> getLiveQueue(Long placeId) {
        Place place = placeRepo.findById(placeId).orElse(null);
        if (place == null) return List.of();

        LocalDate today = LocalDate.now();


        return tokenRepo.findByPlaceAndVisitDateOrderByTokenNumberAsc(place, today);
    }

    public String callNextToken(Long placeId) {
        Place place = placeRepo.findById(placeId).orElse(null);
        if (place == null) return "Place not found";

        List<Token> queue = tokenRepo
                .findByPlaceAndVisitDateAndStatusOrderByTokenNumber(
                        place,
                        LocalDate.now(),
                        TokenStatus.GENERATED
                );

        if (queue.isEmpty()) return "No tokens in queue";

        Token next = queue.get(0);
        next.setStatus(TokenStatus.SERVED);
        next.setServedAt(LocalDateTime.now());

        tokenRepo.save(next);

        return "Token " + next.getTokenNumber() + " called";
    }

    public String skipToken(Long tokenId) {
        Token token = tokenRepo.findById(tokenId).orElse(null);
        if (token == null) return "Token not found";

        if (!token.getVisitDate().equals(LocalDate.now())) {
            return "Cannot skip future tokens";
        }

        token.setStatus(TokenStatus.SKIPPED);
        tokenRepo.save(token);

        return "Token " + token.getTokenNumber() + " skipped";
    }
}
