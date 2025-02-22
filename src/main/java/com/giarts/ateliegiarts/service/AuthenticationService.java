package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.JwtTokenDTO;
import com.giarts.ateliegiarts.dto.LoginDTO;
import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.repository.UserRepository;
import com.giarts.ateliegiarts.security.JwtTokenService;
import com.giarts.ateliegiarts.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public JwtTokenDTO authenticateUser(LoginDTO loginDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        var authentication = authenticationManager.authenticate(usernamePassword);

        var token = jwtTokenService.generateToken((UserDetailsImpl) authentication.getPrincipal());
        return new JwtTokenDTO(token);
    }
}
