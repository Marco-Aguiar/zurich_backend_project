package com.zurich.demo.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.zurich.demo.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("book-reader-api")
                    .withSubject(user.getUsername())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
            logger.info("JWT token generated successfully for user: {}", user.getUsername());
            return token;
        } catch (JWTCreationException exception) {
            logger.error("Error during JWT token creation for user: {}", user.getUsername(), exception);
            throw new RuntimeException("Error generating JWT token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String subject = JWT.require(algorithm)
                    .withIssuer("book-reader-api")
                    .build()
                    .verify(token)
                    .getSubject();
            logger.info("JWT token validated successfully for subject: {}", subject);
            return subject;
        } catch (JWTVerificationException exception) {
            logger.warn("JWT validation failed: {}", exception.getMessage());
            return "";
        }
    }

    private Instant generateExpirationDate() {
        return Instant.now().plus(1, ChronoUnit.HOURS);
    }
}