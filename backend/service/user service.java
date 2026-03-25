package com.example.queueueueeeee.service;

import com.example.queueueueeeee.model.*;
import com.example.queueueueeeee.repository.PlaceRepo;
import com.example.queueueueeeee.repository.PlaceServiceRepo;
import com.example.queueueueeeee.repository.TokenRepo;
import com.example.queueueueeeee.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PlaceRepo placeRepo;

    @Autowired
    private TokenRepo tokenRepo;

    @Autowired
    private PlaceServiceRepo placeServiceRepo;

    @Autowired
    private JavaMailSender mailSender;

    // ---------- AUTH ----------
    public String registerUser(User user) {

        if (userRepo.findByUsername(user.getUsername()) != null) {
            return "Username already exists";
        }

        userRepo.save(user);
        return "Registration successful";
    }

    public String loginUser(LoginRequest loginRequest) {

        User user = userRepo.findByUsername(loginRequest.getUsername());

        if (user == null) return "User not found";

        if (!user.getPassword().equals(loginRequest.getPassword()))
            return "Invalid credentials";

        return "Login successful";
    }

    // ---------- PLACES ----------
    public List<Place> getAllPlaces() {
        return placeRepo.findAll();
    }

    public List<PlaceService> getServicesByPlaceName(String placeName) {

        Place place = placeRepo.findByName(placeName);

        if (place == null) {
            return List.of();
        }

        return placeServiceRepo.findByPlace(place);
    }

    // ---------- WAIT TIME LOGIC ----------
    public int getWaitingTime(String serviceName, int peopleCount) {

        int capacity;
        int baseTime;

        switch (serviceName) {

            case "IOB Bank":
                capacity = 5;     // counters
                baseTime = 13;
                break;

            case "BSM Hospital":
                capacity = 5;     // doctors
                baseTime = 18;
                break;

            case "Mani's Dum Biriyani":
                capacity = 10;    // seats
                baseTime = 31;
                break;

            case "Disney Land":
                capacity = 40;    // ride capacity
                baseTime = 5;
                break;

            case "Passport Verification":
                capacity = 3;
                baseTime = 12;
                break;

            default:
                capacity = 5;
                baseTime = 10;
        }

        // If within capacity → no waiting
        if (peopleCount <= capacity) {
            return 0;
        }

        // calculate batches
        int batches = (int) Math.ceil((double) (peopleCount - capacity) / capacity);

        return batches * baseTime;
    }

    // ---------- TOKEN GENERATION ----------
    public Token generateToken(String username,
                               Long placeId,
                               Long serviceId,
                               LocalDate visitDate) {

        User user = userRepo.findByUsername(username);
        if (user == null) return null;

        Place place = placeRepo.findById(placeId).orElse(null);
        if (place == null) return null;

        PlaceService service = placeServiceRepo.findById(serviceId).orElse(null);
        if (service == null) return null;

        // Get last token
        Token lastToken =
                tokenRepo.findTopByPlaceAndVisitDateOrderByTokenNumberDesc(place, visitDate);

        int nextTokenNumber =
                (lastToken == null) ? 1 : lastToken.getTokenNumber() + 1;

        Token token = new Token();

        token.setUser(user);
        token.setPlace(place);
        token.setService(service);
        token.setVisitDate(visitDate);
        token.setBookingDate(LocalDate.now());
        token.setTokenNumber(nextTokenNumber);
        token.setStatus(TokenStatus.GENERATED);

        tokenRepo.save(token);

        // Calculate waiting time based on capacity logic
        int estimatedWait = getWaitingTime(
                service.getServiceName(),
                nextTokenNumber
        );

        sendTokenEmail(
                user.getEmail(),
                token.getTokenNumber(),
                place.getName(),
                service.getServiceName(),
                visitDate,
                estimatedWait
        );

        return token;
    }

    // ---------- EMAIL ----------
    private void sendTokenEmail(String toEmail,
                                int tokenNumber,
                                String placeName,
                                String serviceName,
                                LocalDate visitDate,
                                int wait) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Your Token for " + placeName);

        message.setText(
                "Hello,\n\n" +
                        "Welcome to " + placeName + "\n\n" +
                        "Your Token Number: " + tokenNumber + "\n" +
                        "Service: " + serviceName + "\n" +
                        "Estimated Waiting Time: " + wait + " minutes\n" +
                        "Visit Date: " + visitDate + "\n\n" +
                        "Please arrive on time.\n\n" +
                        "Thank you for using our Smart Queue System!"
        );

        mailSender.send(message);
    }

    // ---------- TOKEN DETAILS ----------
    public TokenDetails getTokenDetails(Long tokenId) {

        Token token = tokenRepo.findById(tokenId).orElse(null);

        if (token == null) return null;

        int queuePosition =
                tokenRepo.countByPlaceAndVisitDateAndStatusAndTokenNumberLessThan(
                        token.getPlace(),
                        token.getVisitDate(),
                        TokenStatus.GENERATED,
                        token.getTokenNumber()
                ) + 1;

        int estimatedWait = getWaitingTime(
                token.getService().getServiceName(),
                queuePosition
        );

        return new TokenDetails(
                token.getId(),
                token.getTokenNumber(),
                queuePosition,
                estimatedWait
        );
    }

    // ---------- USER TOKENS ----------
    public List<UserTokenDTO> getTokensByUsername(String username) {

        User user = userRepo.findByUsername(username);
        if (user == null) return List.of();

        return tokenRepo.findByUser(user)
                .stream()
                .map(token -> new UserTokenDTO(
                        token.getId(),
                        token.getTokenNumber(),
                        token.getPlace().getName(),
                        token.getService().getServiceName(),
                        token.getVisitDate().toString(),
                        token.getStatus().name()
                ))
                .toList();
    }

    public int getQueueLengthByPlace(Long placeId) {

        Place place = placeRepo.findById(placeId).orElse(null);
        if (place == null) return 0;

        return tokenRepo.countByPlaceAndStatus(place, TokenStatus.GENERATED);
    }

    // ---------- USER TOKEN DTO ----------
    public static class UserTokenDTO {

        private Long tokenId;
        private int tokenNumber;
        private String placeName;
        private String serviceName;
        private String visitDate;
        private String status;

        public UserTokenDTO(Long tokenId, int tokenNumber,
                            String placeName, String serviceName,
                            String visitDate, String status) {

            this.tokenId = tokenId;
            this.tokenNumber = tokenNumber;
            this.placeName = placeName;
            this.serviceName = serviceName;
            this.visitDate = visitDate;
            this.status = status;
        }

        public Long getTokenId() { return tokenId; }
        public int getTokenNumber() { return tokenNumber; }
        public String getPlaceName() { return placeName; }
        public String getServiceName() { return serviceName; }
        public String getVisitDate() { return visitDate; }
        public String getStatus() { return status; }
    }

    // ---------- TOKEN DETAILS DTO ----------
    public static class TokenDetails {

        private final Long tokenId;
        private final int tokenNumber;
        private final int queuePosition;
        private final int estimatedWaitTime;

        public TokenDetails(Long tokenId,
                            int tokenNumber,
                            int queuePosition,
                            int estimatedWaitTime) {

            this.tokenId = tokenId;
            this.tokenNumber = tokenNumber;
            this.queuePosition = queuePosition;
            this.estimatedWaitTime = estimatedWaitTime;
        }

        public Long getTokenId() { return tokenId; }
        public int getTokenNumber() { return tokenNumber; }
        public int getQueuePosition() { return queuePosition; }
        public int getEstimatedWaitTime() { return estimatedWaitTime; }
    }
}
