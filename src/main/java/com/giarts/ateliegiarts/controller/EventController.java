package com.giarts.ateliegiarts.controller;

import com.giarts.ateliegiarts.dto.EventDTO;
import com.giarts.ateliegiarts.model.Event;
import com.giarts.ateliegiarts.service.EventService;
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
@RequestMapping("/events")
@Tag(name = "Event Controller", description = "Operations related to events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @Operation(summary = "List all events")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @Operation(summary = "Get an event by ID")
    @ApiResponse(responseCode = "200", description = "Event retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable("eventId") Long eventId) {
        Event event = eventService.getEventById(eventId);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Create an event")
    @ApiResponse(responseCode = "201", description = "Event created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid event input")
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody @Valid EventDTO eventDTO) {
        Event createdEvent = eventService.createEvent(eventDTO);

        URI location = URI.create("/api/events/" + createdEvent.getId().toString());
        return ResponseEntity.created(location).body(createdEvent);
    }

    @Operation(summary = "Update an event")
    @ApiResponse(responseCode = "200", description = "Event updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid event input")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEventById(@PathVariable("eventId") Long eventId,
                                                 @RequestBody @Valid EventDTO updateEventDTO) {
        Event updatedEvent = eventService.updateEventById(eventId, updateEventDTO);
        return ResponseEntity.ok(updatedEvent);
    }

    @Operation(summary = "Delete an event")
    @ApiResponse(responseCode = "204", description = "Event deleted successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEventById(@PathVariable("eventId") Long eventId) {
        eventService.deleteEventById(eventId);
        return ResponseEntity.noContent().build();
    }
}
