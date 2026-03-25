package com.example.queueueueeeee.controller;

import com.example.queueueueeeee.model.Admin;
import com.example.queueueueeeee.model.Place;
import com.example.queueueueeeee.model.PlaceService;
import com.example.queueueueeeee.model.Token;
import com.example.queueueueeeee.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public String register(@RequestBody Admin admin) {
        return adminService.register(admin);
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        return adminService.login(username, password);
    }

    @PostMapping("/place")
    public String addPlace(@RequestBody Place place,
                           @RequestParam String username) {
        return adminService.addPlace(place, username);
    }



    @GetMapping("/places")
    public List<Place> getMyPlaces(@RequestParam String username) {
        return adminService.getPlacesOfAdmin(username);
    }

//    @PostMapping("/place/{placeId}/service")
//    public String addServiceToPlace(@PathVariable Long placeId, @RequestBody PlaceService service) {
//        return adminService.addService(placeId, service);
//    }

    @PostMapping("/place/service")
    public String addServiceForAdmin(@RequestBody PlaceService service,
                                     @RequestParam String adminUsername) {
        return adminService.addServiceForAdmin(adminUsername, service);
    }

    @GetMapping("/place/{placeId}/services")
    public List<PlaceService> getServicesOfPlace(@PathVariable Long placeId) {
        return adminService.getServices(placeId);
    }


    @GetMapping("/queue")
    public List<Token> viewLiveQueueByAdmin(@RequestParam String adminUsername) {
        return adminService.getLiveQueueByAdmin(adminUsername);
    }

    @PostMapping("/queue/next/{placeId}")
    public String callNext(@PathVariable Long placeId) {
        return adminService.callNextToken(placeId);
    }

    @PostMapping("/queue/skip/{tokenId}")
    public String skip(@PathVariable Long tokenId) {
        return adminService.skipToken(tokenId);
    }

}
