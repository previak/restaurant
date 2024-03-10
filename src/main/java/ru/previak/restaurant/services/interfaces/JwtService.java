package ru.previak.restaurant.services.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {
    String extractUsername(String token);

    String generateToken(UserDetails userDetails);

    String generateToken(Map<String, Object> extractClaims,
                                UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);
}
