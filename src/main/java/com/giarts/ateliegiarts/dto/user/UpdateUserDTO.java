package com.giarts.ateliegiarts.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.giarts.ateliegiarts.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UpdateUserDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        @JsonIgnore
        Set<UserRole> userRoles
) {
}
