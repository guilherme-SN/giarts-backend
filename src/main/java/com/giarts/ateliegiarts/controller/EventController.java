package com.giarts.ateliegiarts.controller;

import com.giarts.ateliegiarts.dto.event.CreateEventDTO;
import com.giarts.ateliegiarts.dto.event.ResponseEventDTO;
import com.giarts.ateliegiarts.dto.event.UpdateEventDTO;
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
    public ResponseEntity<List<ResponseEventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @Operation(summary = "Get an event by ID")
    @ApiResponse(responseCode = "200", description = "Event retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @GetMapping("/{eventId}")
    public ResponseEntity<ResponseEventDTO> getEventById(@PathVariable("eventId") Long eventId) {
        ResponseEventDTO response = eventService.getEventById(eventId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create an event")
    @ApiResponse(responseCode = "201", description = "Event created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid event input")
    @PostMapping
    public ResponseEntity<ResponseEventDTO> createEvent(@RequestBody @Valid CreateEventDTO createEventDTO) {
        ResponseEventDTO response = eventService.createEvent(createEventDTO);

        URI location = URI.create("/api/events/" + response.id().toString());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Update an event")
    @ApiResponse(responseCode = "200", description = "Event updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid event input")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @PutMapping("/{eventId}")
    public ResponseEntity<ResponseEventDTO> updateEventById(@PathVariable("eventId") Long eventId,
                                                            @RequestBody @Valid UpdateEventDTO updateEventDTO) {
        ResponseEventDTO response = eventService.updateEventById(eventId, updateEventDTO);
        return ResponseEntity.ok(response);
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
