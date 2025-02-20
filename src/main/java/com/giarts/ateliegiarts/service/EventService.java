package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.EventDTO;
import com.giarts.ateliegiarts.exception.EventNotFoundException;
import com.giarts.ateliegiarts.model.Event;
import com.giarts.ateliegiarts.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
    }

    public Event createEvent(EventDTO eventDTO) {
        Event event = new Event(eventDTO);
        return eventRepository.save(event);
    }

    public Event updateEventById(Long eventId, EventDTO updateEventDTO) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        updateEventFields(event, updateEventDTO);

        return eventRepository.save(event);
    }

    private void updateEventFields(Event event, EventDTO updateEventDTO) {
        event.setName(updateEventDTO.getName());

        if (updateEventDTO.getDescription() != null) {
            event.setDescription(updateEventDTO.getDescription());
        }
    }

    public void deleteEventById(Long eventId) {
        if (eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);
        } else {
            throw new EventNotFoundException(eventId);
        }
    }
}
