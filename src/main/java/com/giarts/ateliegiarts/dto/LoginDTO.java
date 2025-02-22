package com.giarts.ateliegiarts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotNull(message = "Username is required")
    @Email(message = "Email must be valid")
    private String username;

    @NotNull(message = "Password is required")
    private String password;
}
