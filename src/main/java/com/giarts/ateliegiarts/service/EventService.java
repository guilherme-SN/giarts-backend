package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.event.CreateEventDTO;
import com.giarts.ateliegiarts.dto.event.ResponseEventDTO;
import com.giarts.ateliegiarts.dto.event.UpdateEventDTO;
import com.giarts.ateliegiarts.exception.EventNotFoundException;
import com.giarts.ateliegiarts.model.Event;
import com.giarts.ateliegiarts.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<ResponseEventDTO> getAllEvents() {
        return eventRepository.findAll().stream().map(ResponseEventDTO::fromEntity).collect(Collectors.toList());
    }

    public Event getEventEntityById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    public ResponseEventDTO getEventById(Long eventId) {
        return eventRepository.findById(eventId).map(ResponseEventDTO::fromEntity)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    public ResponseEventDTO createEvent(CreateEventDTO createEventDTO) {
        Event event = new Event(createEventDTO);
        Event savedEvent = eventRepository.save(event);

        return ResponseEventDTO.fromEntity(savedEvent);
    }

    public ResponseEventDTO updateEventById(Long eventId, UpdateEventDTO updateEventDTO) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        updateEventFields(event, updateEventDTO);

        Event savedEvent = eventRepository.save(event);
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
        validateEvent(eventId);
        eventRepository.deleteById(eventId);
    }

    public void validateEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }
    }
}
