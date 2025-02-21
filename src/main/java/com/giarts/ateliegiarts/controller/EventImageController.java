package com.giarts.ateliegiarts.controller;

import com.giarts.ateliegiarts.model.EventImage;
import com.giarts.ateliegiarts.service.EventImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/images")
@Tag(name = "EventImage Controller", description = "Operations related to the images of the events")
@RequiredArgsConstructor
public class EventImageController {
    private final EventImageService eventImageService;

    @Operation(summary = "List all images of an event")
    @ApiResponse(responseCode = "200", description = "Images retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @GetMapping
    public ResponseEntity<List<EventImage>> getAllEventImages(@PathVariable("eventId") Long eventId) {
        return ResponseEntity.ok(eventImageService.getAllEventImages(eventId));
    }

    @Operation(summary = "Upload an image to an event")
    @ApiResponse(responseCode = "200", description = "Image uploaded successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @PostMapping
    public ResponseEntity<EventImage> uploadEventImage(@PathVariable("eventId") Long eventId,
                                                       @RequestParam("file")MultipartFile file) {
        return ResponseEntity.ok(eventImageService.saveUploadedEventImage(eventId, file));
    }

    @Operation(summary = "Delete an image")
    @ApiResponse(responseCode = "204", description = "Image deleted successfully")
    @ApiResponse(responseCode = "404", description = "Event or Image not found")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteEventImageById(@PathVariable("eventId") Long eventId,
                                                     @PathVariable("imageId") Long imageId) {
        eventImageService.deleteEventImageById(eventId, imageId);
        return ResponseEntity.noContent().build();
    }
}
