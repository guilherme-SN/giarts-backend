package com.giarts.ateliegiarts.dto.event;

import com.giarts.ateliegiarts.model.Event;

import java.time.LocalDateTime;

public record ResponseEventDTO(
        Long id,
        String name,
        String description,
        String location,
        LocalDateTime dateTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResponseEventDTO fromEntity(Event event) {
        return new ResponseEventDTO(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getLocation(),
                event.getDateTime(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
