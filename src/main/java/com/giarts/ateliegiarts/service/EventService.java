package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.event.CreateEventDTO;
import com.giarts.ateliegiarts.dto.event.ResponseEventDTO;
import com.giarts.ateliegiarts.dto.event.UpdateEventDTO;
import com.giarts.ateliegiarts.exception.EventNotFoundException;
import com.giarts.ateliegiarts.model.Event;
import com.giarts.ateliegiarts.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;

    public List<ResponseEventDTO> getAllEvents() {
        log.info("Retrieving all events");

        List<ResponseEventDTO> events = eventRepository.findAll().stream().map(ResponseEventDTO::fromEntity).toList();

        log.debug("Found {} events", events.size());

        return events;
    }

    public Event getEventEntityById(Long eventId) {
        log.info("Retrieving event entity by ID: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event entity with ID: {} not found", eventId);
                    return new EventNotFoundException(eventId);
                });

        log.debug("Successfully retrieved event entity with ID: {}", eventId);

        return event;
    }

    public ResponseEventDTO getEventById(Long eventId) {
        log.info("Retrieving event by ID: {}", eventId);

        ResponseEventDTO event = eventRepository.findById(eventId).map(ResponseEventDTO::fromEntity)
                .orElseThrow(() -> {
                    log.warn("Event with ID: {} not found", eventId);
                    return new EventNotFoundException(eventId);
                });

        log.debug("Successfully retrieved event with ID: {}", eventId);

        return event;
    }

    public ResponseEventDTO createEvent(CreateEventDTO createEventDTO) {
        log.info("Creating new event with name: {}", createEventDTO.name());

        Event event = new Event(createEventDTO);
        Event savedEvent = eventRepository.save(event);

        log.debug("Event created successfully with ID: {}", savedEvent.getId());

        return ResponseEventDTO.fromEntity(savedEvent);
    }

    public ResponseEventDTO updateEventById(Long eventId, UpdateEventDTO updateEventDTO) {
        log.info("Updating event with ID: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with ID: {} not found while updating", eventId);
                    return new EventNotFoundException(eventId);
                });

        updateEventFields(event, updateEventDTO);

        Event savedEvent = eventRepository.save(event);

        log.debug("Successfully updated event with ID: {}", event);

        return ResponseEventDTO.fromEntity(savedEvent);
    }

    private void updateEventFields(Event event, UpdateEventDTO updateEventDTO) {
        event.setName(updateEventDTO.name());
        event.setLocation(updateEventDTO.location());
        event.setDateTime(updateEventDTO.dateTime());

        if (updateEventDTO.description() != null) {
            event.setDescription(updateEventDTO.description());
        }
    }

    public void deleteEventById(Long eventId) {
        log.info("Deleting event with ID: {}", eventId);

        validateEvent(eventId);
        eventRepository.deleteById(eventId);

        log.info("Successfully deleted event with ID: {}", eventId);
    }

    public void validateEvent(Long eventId) {
        log.info("Validating event with ID: {}", eventId);

        if (!eventRepository.existsById(eventId)) {
            log.warn("Error in validation. Event with ID: {} not found", eventId);
            throw new EventNotFoundException(eventId);
        }

        log.debug("Validation completed for event with ID: {}", eventId);
    }
}
