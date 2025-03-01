package com.giarts.ateliegiarts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;

    @NotNull(message = "Event location is required")
    private String location;

    @NotNull(message = "Event date and time is required")
    private LocalDateTime dateTime;
}
