package com.giarts.ateliegiarts.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class JwtTokenService {
    private static final String ISSUER = "giarts-api";

    @Value("${api.security.token.secret}")
    private String secretKey;

    public String generateToken(UserDetailsImpl user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(generateCreationDate())
                    .withExpiresAt(generateExpirationDate())
                    .withSubject(user.getUsername())
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
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
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException("Invalid or expired token", ex);
        }
    }
}
