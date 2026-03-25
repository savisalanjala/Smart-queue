package com.example.queueueueeeee.controller;

import com.example.queueueueeeee.model.LoginRequest;
import com.example.queueueueeeee.model.Place;
import com.example.queueueueeeee.model.PlaceService;
import com.example.queueueueeeee.model.Token;
import com.example.queueueueeeee.repository.PlaceServiceRepo;
import com.example.queueueueeeee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceServiceRepo placeServiceRepo;

    @PostMapping("/register")
    public String register(@RequestBody com.example.queueueueeeee.model.User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }

    @GetMapping("/places")
    public List<Place> viewPlaces() {
        return userService.getAllPlaces();
    }

    @GetMapping("/services")
    public List<PlaceService> getServicesByPlaceName(@RequestParam String placeName) {
        return userService.getServicesByPlaceName(placeName);
    }

    @PostMapping("/token")
    public String generateToken(@RequestParam String username,
                                @RequestParam Long placeId,
                                @RequestParam Long serviceId,
                                @RequestParam(required = false) String visitDate) {

        LocalDate date = (visitDate == null)
                ? LocalDate.now()
                : LocalDate.parse(visitDate);

        Token token = userService.generateToken(username, placeId,serviceId, date);

        if (token == null) return "Token generation failed";

        return "Token generated successfully | Token No: "
                + token.getTokenNumber()
                + " | Visit Date: " + date;
    }

    @GetMapping("/queue-length")
    public int getQueueLength(@RequestParam Long placeId) {
        return userService.getQueueLengthByPlace(placeId);
    }

    @GetMapping("/tokens")
    public List<UserService.UserTokenDTO> getUserTokens(
            @RequestParam String username) {

        return userService.getTokensByUsername(username);
    }

    @GetMapping("/token/{tokenId}")
    public String getTokenDetails(@PathVariable Long tokenId) {

        UserService.TokenDetails details =
                userService.getTokenDetails(tokenId);

        if (details == null) return "Token not found";

        return "Token Number: " + details.getTokenNumber()
                +"Token ID: " + details.getTokenId()
                + ", Queue Position: " + details.getQueuePosition()
                + ", Estimated Wait Time: "
                + details.getEstimatedWaitTime() + " minutes";
    }
}
