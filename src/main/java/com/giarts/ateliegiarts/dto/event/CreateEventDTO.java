package com.giarts.ateliegiarts.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateEventDTO(
        @NotBlank(message = "Name is required")
        String name,
        String description,
        @NotNull(message = "Event location is required")
        String location,
        @NotNull(message = "Event date and time is required")
        LocalDateTime dateTime
) {
}
