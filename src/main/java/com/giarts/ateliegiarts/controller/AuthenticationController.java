package com.giarts.ateliegiarts.controller;

import com.giarts.ateliegiarts.dto.authentication.JwtTokenResponseDTO;
import com.giarts.ateliegiarts.dto.authentication.LoginRequestDTO;
import com.giarts.ateliegiarts.dto.user.CreateUserDTO;
import com.giarts.ateliegiarts.dto.user.ResponseUserDTO;
import com.giarts.ateliegiarts.service.AuthenticationService;
import com.giarts.ateliegiarts.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Operations related to authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Operation(summary = "Get JWT token")
    @ApiResponse(responseCode = "200", description = "JWT token retrieved successfully")
    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginDTO) {
        return ResponseEntity.ok(authenticationService.authenticateUser(loginDTO));
    }

    @Operation(summary = "Create an user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid user input")
    @PostMapping("/register")
    public ResponseEntity<ResponseUserDTO> createUser(@RequestBody @Valid CreateUserDTO createUserDTO) {
        ResponseUserDTO response = userService.createUser(createUserDTO);

        URI location = URI.create("/api/users/" + response.id().toString());
        return ResponseEntity.created(location).body(response);
    }
}
