package com.giarts.ateliegiarts.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import com.giarts.ateliegiarts.dto.UserDTO;
import com.giarts.ateliegiarts.exception.UserNotFoundException;
import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "Operations related to users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "List all users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @Operation(summary = "Get an user by ID")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @Operation(summary = "Create an user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid user input")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        User createdUser = userService.createUser(userDTO);

        URI location = URI.create("/api/users/" + createdUser.getId().toString());
        return ResponseEntity.created(location).body(createdUser);
    }


    @Operation(summary = "Update an user")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid user input")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserById(
            @PathVariable("userId") Long userId, 
            @RequestBody @Valid UserDTO updatedUserDTO, 
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            User updatedUser = userService.updateUserById(userId, updatedUserDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @Operation(summary = "Delete an user")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
