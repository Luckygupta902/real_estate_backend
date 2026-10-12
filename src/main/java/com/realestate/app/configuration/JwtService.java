package com.realestate.app.configuration;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.realestate.app.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

        private final SecretKey secretKey = Keys.hmacShaKeyFor(
                        "mySecretKeyForRealEstateApplication123456789"
                                        .getBytes());

        public String generateToken(User user) {
                long expirationTime = 7L * 24 * 60 * 60 * 1000;

                return Jwts.builder()
                                .subject(String.valueOf(user.getId()))
                                .claim("email", user.getEmail())
                                .claim("roleId", user.getRoleId())
                                .issuedAt(new Date())
                                .expiration(
                                                new Date(System.currentTimeMillis()
                                                                + expirationTime))
                                .signWith(secretKey)
                                .compact();
        }

        public Claims extractClaims(String token) {

                return Jwts.parser()
                                .verifyWith(secretKey)
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();
        }
}
