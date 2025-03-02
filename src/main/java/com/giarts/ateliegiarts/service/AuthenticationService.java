package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.authentication.JwtTokenResponseDTO;
import com.giarts.ateliegiarts.dto.authentication.LoginRequestDTO;
import com.giarts.ateliegiarts.repository.UserRepository;
import com.giarts.ateliegiarts.security.JwtTokenService;
import com.giarts.ateliegiarts.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public JwtTokenResponseDTO authenticateUser(LoginRequestDTO loginDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.username(), loginDTO.password());
        Authentication authentication = authenticationManager.authenticate(usernamePassword);

        String token = jwtTokenService.generateToken((UserDetailsImpl) authentication.getPrincipal());
        return new JwtTokenResponseDTO(token);
    }
}
