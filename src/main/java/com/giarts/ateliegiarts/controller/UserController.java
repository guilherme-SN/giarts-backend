package com.giarts.ateliegiarts.controller;

import com.giarts.ateliegiarts.dto.UserDTO;
import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<User> getUserById(@PathVariable("userId") Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Create an user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid user input")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDTO userDTO) {
        User createdUser = userService.createUser(userDTO);

        URI location = URI.create("/api/users/" + createdUser.getId().toString());
        return ResponseEntity.created(location).body(createdUser);
    }

    @Operation(summary = "Update an user")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid user input")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUserById(@PathVariable("userId") Long userId, @RequestBody @Valid UserDTO updatedUserDTO) {
        User updatedUser = userService.updateUserById(userId, updatedUserDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete an user")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("userId") Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }
}
