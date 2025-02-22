package com.giarts.ateliegiarts.security;

import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if (checkIfEndpointIsNotPublic(request)) {
            String token = recoveryTokenFromRequestHeader(request);
            String subject = jwtTokenService.getSubjectFromToken(token);
            UserDetailsImpl userDetails = getUserDetailsFromSubject(subject);

            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }

        filterChain.doFilter(request, response);
    }

    private boolean checkIfEndpointIsNotPublic(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        System.out.println("Requisição para: " + requestURI);

        boolean isPublic = Arrays.stream(SecurityConfiguration.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED)
                .anyMatch(pattern -> {
                    boolean matches = requestURI.matches(pattern.replace("**", ".*"));
                    System.out.println("Comparando com " + pattern + " -> " + matches);
                    return matches;
                });

        return !isPublic;
//        return Arrays.stream(SecurityConfiguration.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED)
//                .noneMatch(requestURI::startsWith);
    }

    private String recoveryTokenFromRequestHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null) {
            throw new RuntimeException("Token is missing");
        }

        return authorizationHeader.replace("Bearer ", "");
    }

    private UserDetailsImpl getUserDetailsFromSubject(String subject) {
        Optional<User> userOptional = userRepository.findByEmail(subject);
        return userOptional.map(UserDetailsImpl::new).orElse(null);
    }
}
