package com.giarts.ateliegiarts.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class JwtTokenService {
    private static final String ISSUER = "giarts-api";

    @Value("${api.security.token.secret}")
    private String secretKey;

    public String generateToken(UserDetailsImpl user) {
        try {
            log.info("Generating token for user: {}", user.getUsername());

            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(generateCreationDate())
                    .withExpiresAt(generateExpirationDate())
                    .withSubject(user.getUsername())
                    .sign(algorithm);

            log.debug("Token successfully generated for user: {}", user.getUsername());

            return token;
        } catch (JWTCreationException ex) {
            log.error("Error while generating token for user: {}", user.getUsername(), ex);
            throw new JWTCreationException("Error while generating token", ex);
        }
    }

    private Instant generateCreationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
    }

    private Instant generateExpirationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusHours(4).toInstant();
    }

    public String getSubjectFromToken(String token) {
        try {
            log.info("Retrieving subject from token");

            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String subject = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();

            log.debug("Subject retrieved successfully from token");

            return subject;
        } catch (JWTVerificationException ex) {
            log.error("Token verification failed for user. Token might be invalid or expired", ex);
            throw new JWTVerificationException("Invalid or expired token", ex);
        }
    }
}
