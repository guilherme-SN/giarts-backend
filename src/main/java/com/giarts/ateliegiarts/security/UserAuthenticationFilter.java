package com.giarts.ateliegiarts.security;

import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (isAuthorizationHeaderPresent(request)) {
            String token = recoveryTokenFromRequestHeader(request);
            String subject = jwtTokenService.getSubjectFromToken(token);
            UserDetailsImpl userDetails = getUserDetailsFromSubject(subject);

            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthorizationHeaderPresent(HttpServletRequest request) {
        return request.getHeader("Authorization") != null;
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
