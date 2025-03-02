package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.event.CreateEventDTO;
import com.giarts.ateliegiarts.dto.event.ResponseEventDTO;
import com.giarts.ateliegiarts.dto.event.UpdateEventDTO;
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

import java.time.LocalDateTime;
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
                    createEvent(1L, "Event 1", "Description 1", "Location 1", LocalDateTime.now()),
                    createEvent(2L, "Event 2", "Description 2", "Location 2", LocalDateTime.now())
            );

            when(eventRepository.findAll()).thenReturn(events);

            List<ResponseEventDTO> eventsRetrieved = eventService.getAllEvents();

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
            Event event = createEvent(1L, "Name", "Description", "Location", LocalDateTime.now());

            when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

            ResponseEventDTO eventRetrieved = eventService.getEventById(event.getId());

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
            LocalDateTime dateTime = LocalDateTime.now();
            CreateEventDTO eventDTO = new CreateEventDTO("Name", "Description", "Location", dateTime);
            Event event = createEvent(1L , "Name", "Description", "Location", dateTime);

            when(eventRepository.save(eventArgumentCaptor.capture())).thenReturn(event);

            ResponseEventDTO createdEvent = eventService.createEvent(eventDTO);

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
            Event event = createEvent(1L, "Name", "Description", "Location", LocalDateTime.now());
            UpdateEventDTO updateEventDTO = new UpdateEventDTO("Name Updated", "Description Updated", "Location", LocalDateTime.now());

            when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
            when(eventRepository.save(eventArgumentCaptor.capture())).thenReturn(event);

            ResponseEventDTO updatedEvent = eventService.updateEventById(event.getId(), updateEventDTO);

            assertNotNull(updatedEvent);
            assertEventDetails(updateEventDTO, updatedEvent);

            verify(eventRepository, times(1)).findById(event.getId());
            verify(eventRepository, times(1)).save(any(Event.class));
        }

        @Test
        @DisplayName("Should throw EventNotFoundException when event does not exists")
        void shouldThrowExceptionWhenEventDoesNotExists() {
            Long eventId = 1L;
            UpdateEventDTO updateEventDTO = new UpdateEventDTO("Name updated", "Description Updated",
                    "Location", LocalDateTime.now());

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

    private Event createEvent(Long id, String name, String description, String location, LocalDateTime dateTime) {
        return Event.builder()
                .id(id)
                .name(name)
                .description(description)
                .location(location)
                .dateTime(dateTime)
                .build();
    }

    private void assertEventDetails(UpdateEventDTO expected, ResponseEventDTO actual) {
        assertEventDetails(
                Event.builder()
                        .name(expected.name())
                        .description(expected.description())
                        .location(expected.location())
                        .dateTime(expected.dateTime())
                        .build(),
                actual
        );
    }

    private void assertEventDetails(Event expected, ResponseEventDTO actual) {
        assertAll(
                () -> assertEquals(expected.getName(), actual.name()),
                () -> assertEquals(expected.getDescription(), actual.description()),
                () -> assertEquals(expected.getLocation(), actual.location()),
                () -> assertEquals(expected.getDateTime(), actual.dateTime())
        );
    }
}
