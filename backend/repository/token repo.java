package com.example.queueueueeeee.repository;

import com.example.queueueueeeee.model.Place;
import com.example.queueueueeeee.model.Token;
import com.example.queueueueeeee.model.TokenStatus;
import com.example.queueueueeeee.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token, Long> {

    Token findTopByPlaceAndVisitDateOrderByTokenNumberDesc(
            Place place,
            LocalDate visitDate
    );

    List<Token> findByPlaceAndVisitDateAndStatusOrderByTokenNumber(
            Place place,
            LocalDate visitDate,
            TokenStatus status
    );

    int countByPlaceAndVisitDateAndStatusAndTokenNumberLessThan(
            Place place,
            LocalDate visitDate,
            TokenStatus status,
            int tokenNumber
    );

    int countByPlaceAndStatus(Place place, TokenStatus status);

    List<Token> findByPlaceAndVisitDateAndStatusAndTokenNumberLessThan(
            Place place,
            LocalDate visitDate,
            TokenStatus status,
            int tokenNumber
    );

    List<Token> findByUser(User user);

    List<Token> findByPlaceAndStatusOrderByTokenNumberAsc(
            Place place,
            TokenStatus status
    );

    List<Token> findByPlaceAndVisitDateOrderByTokenNumberAsc(Place place, LocalDate visitDate);
}

