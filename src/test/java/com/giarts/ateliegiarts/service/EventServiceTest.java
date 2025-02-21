package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.EventDTO;
import com.giarts.ateliegiarts.exception.EventNotFoundException;
import com.giarts.ateliegiarts.model.Event;
import com.giarts.ateliegiarts.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Captor
    private ArgumentCaptor<Event> eventArgumentCaptor;

    @Nested
    class getAllEvents {
        @Test
        @DisplayName("Should get all events with success")
        void shouldGetAllEventsWithSuccess() {
            List<Event> events = List.of(
                    createEvent(1L, "Event 1", "Description 1"),
                    createEvent(2L, "Event 2", "Description 2")
            );

            when(eventRepository.findAll()).thenReturn(events);

            List<Event> eventsRetrieved = eventService.getAllEvents();

            assertNotNull(eventsRetrieved);
            assertEventDetails(events.get(0), eventsRetrieved.get(0));
            assertEventDetails(events.get(1), eventsRetrieved.get(1));

            verify(eventRepository, times(1)).findAll();
        }
    }

    @Nested
    class getEventById {
        @Test
        @DisplayName("Should get an event by ID with success when event exists")
        void shouldGetEventByIdWithSuccessWhenEventExists() {
            Event event = createEvent(1L, "Name", "Description");

            when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

            Event eventRetrieved = eventService.getEventById(event.getId());

            assertNotNull(eventRetrieved);
            assertEventDetails(event, eventRetrieved);

            verify(eventRepository, times(1)).findById(event.getId());
        }

        @Test
        @DisplayName("Should throw EventNotFoundException when event does not exists")
        void shouldThrowExceptionWhenEventDoesNotExists() {
            Long eventId = 1L;

            when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(EventNotFoundException.class, () -> eventService.getEventById(eventId));

            verify(eventRepository, times(1)).findById(anyLong());
        }
    }

    @Nested
    class createEvent {
        @Test
        @DisplayName("Should create an event with success")
        void shouldCreateEventWithSuccess() {
            EventDTO eventDTO = createEventDTO("Name", "Description");
            Event event = createEvent(1L , "Name", "Description");

            when(eventRepository.save(eventArgumentCaptor.capture())).thenReturn(event);

            Event createdEvent = eventService.createEvent(eventDTO);

            assertNotNull(createdEvent);
            assertEventDetails(event, createdEvent);

            verify(eventRepository, times(1)).save(any(Event.class));
        }
    }

    @Nested
    class updateEventById {
        @Test
        @DisplayName("Should update event with success")
        void shouldUpdateEventWithSuccess() {
            Event event = createEvent(1L, "Name", "Description");
            EventDTO updateEventDTO = createEventDTO("Name Updated", "Description Updated");

            when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            when(eventRepository.save(eventArgumentCaptor.capture())).thenReturn(event);

            Event updatedEvent = eventService.updateEventById(event.getId(), updateEventDTO);

            assertNotNull(updatedEvent);
            assertEventDetails(updateEventDTO, updatedEvent);

            verify(eventRepository, times(1)).findById(event.getId());
            verify(eventRepository, times(1)).save(any(Event.class));
        }

        @Test
        @DisplayName("Should throw EventNotFoundException when event does not exists")
        void shouldThrowExceptionWhenEventDoesNotExists() {
            Long eventId = 1L;
            EventDTO updateEventDTO = createEventDTO("Name updated", "Description Updated");

            when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(EventNotFoundException.class, () -> eventService.updateEventById(eventId, updateEventDTO));

            verify(eventRepository, times(1)).findById(anyLong());
        }
    }

    @Nested
    class deleteEventById {
        @Test
        @DisplayName("Should delete event with success")
        void shouldDeleteEventWithSuccess() {
            Long eventId = 1L;

            when(eventRepository.existsById(eventId)).thenReturn(true);

            assertDoesNotThrow(() -> eventService.deleteEventById(eventId));

            verify(eventRepository, times(1)).existsById(eventId);
            verify(eventRepository, times(1)).deleteById(eventId);
        }

        @Test
        @DisplayName("Should throw EventNotFoundException when event does not exists")
        void shouldThrowExceptionWhenEventDoesNotExists() {
            Long eventId = 1L;

            when(eventRepository.existsById(anyLong())).thenReturn(false);

            assertThrows(EventNotFoundException.class, () -> eventService.deleteEventById(eventId));

            verify(eventRepository, times(1)).existsById(eventId);
            verify(eventRepository, never()).deleteById(eventId);
        }
    }

    @Nested
    class validateEvent {
        @Test
        @DisplayName("Should not throw exception when event is valid")
        void shouldNotThrowExceptionWhenEventIsValid() {
            Long eventId = 1L;

            when(eventRepository.existsById(eventId)).thenReturn(true);

            assertDoesNotThrow(() -> eventService.validateEvent(eventId));

            verify(eventRepository, times(1)).existsById(eventId);
        }

        @Test
        @DisplayName("Should throw exception when event is invalid")
        void shouldThrowExceptionWhenEventIsInvalid() {
            Long eventId = 1L;

            when(eventRepository.existsById(eventId)).thenReturn(false);

            assertThrows(EventNotFoundException.class, () -> eventService.validateEvent(eventId));

            verify(eventRepository, times(1)).existsById(eventId);
        }
    }

    private Event createEvent(Long id, String name, String description) {
        return Event.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }

    private EventDTO createEventDTO(String name, String description) {
        return EventDTO.builder()
                .name(name)
                .description(description)
                .build();
    }

    private void assertEventDetails(EventDTO expected, Event actual) {
        assertEventDetails(
                Event.builder()
                        .name(expected.getName())
                        .description(expected.getDescription())
                        .build(),
                actual
        );
    }

    private void assertEventDetails(Event expected, Event actual) {
        assertAll(
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription())
        );
    }
}
