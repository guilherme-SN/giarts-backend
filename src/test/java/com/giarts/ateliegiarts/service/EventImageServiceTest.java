package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.enums.EImageFolder;
import com.giarts.ateliegiarts.exception.ImageStoreException;
import com.giarts.ateliegiarts.model.Event;
import com.giarts.ateliegiarts.model.EventImage;
import com.giarts.ateliegiarts.repository.EventImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventImageServiceTest {
    @Value("${server.url}")
    private String serverUrl;

    @Mock
    private EventService eventService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private EventImageRepository eventImageRepository;

    @InjectMocks
    private EventImageService eventImageService;

    @Captor
    private ArgumentCaptor<EventImage> eventImageArgumentCaptor;

    @Nested
    class getAllEventImages {
        @Test
        @DisplayName("Should retrieve all images of an event with success when event exists")
        void shouldGetAllEventImagesWithSuccessWhenEventExists() {
            Long eventId = 1L;

            List<EventImage> eventImages = List.of(
                    createEventImage(1L, "events/1/image_1.png", "image_1", eventId),
                    createEventImage(2L, "events/1/image_2.png", "image_2", eventId)
            );

            doNothing().when(eventService).validateEvent(eventId);
            when(eventImageRepository.findAllByEventId(eventId)).thenReturn(eventImages);

            List<EventImage> eventImagesRetrieved = eventImageService.getAllEventImages(eventId);

            assertNotNull(eventImagesRetrieved);
            assertEventImageDetails(eventImages.get(0), eventImagesRetrieved.get(0));
            assertEventImageDetails(eventImages.get(1), eventImagesRetrieved.get(1));

            verify(eventService, times(1)).validateEvent(eventId);
            verify(eventImageRepository, times(1)).findAllByEventId(eventId);
        }
    }

    @Nested
    class saveUploadedEventImage {
        @Test
        @DisplayName("Should save uploaded image with success")
        void shouldSaveUploadedImageWithSuccess() {
            Long eventId = 1L;
            String fileName = "image.png";
            long fileSize = 1024L;
            String contentType = "image/png";
            String expectedImageUrl = String.format("%s/events/%d/images/%s", serverUrl, eventId, fileName);

            MultipartFile file = createMultipartFileMock(fileName, fileSize, contentType);

            EventImage expectedEventImage = createEventImage(1L, expectedImageUrl, fileName, eventId);

            doNothing().when(eventService).validateEvent(eventId);
            doNothing().when(fileStorageService).storeFileInEntityFolder(any(EImageFolder.class), anyLong(), any(MultipartFile.class));
            when(eventService.getEventById(eventId)).thenReturn(createEvent(eventId));
            when(eventImageRepository.save(eventImageArgumentCaptor.capture()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            eventImageService.saveUploadedEventImage(eventId, file);

            EventImage capturedEventImage = eventImageArgumentCaptor.getValue();
            assertNotNull(capturedEventImage);
            assertEventImageDetails(expectedEventImage, capturedEventImage);

            verify(eventService, times(1)).validateEvent(eventId);
            verify(fileStorageService, times(1)).storeFileInEntityFolder(EImageFolder.EVENT, eventId, file);
            verify(eventImageRepository, times(1)).save(any(EventImage.class));
        }

        @Test
        @DisplayName("Should throw ImageStoreException when file storage fails")
        void shouldThrowExceptionWhenFileStorageFails() {
            Long eventId = 1L;
            MultipartFile file = createMultipartFileMock("image.png", 1024L, "image/png");

            doNothing().when(eventService).validateEvent(eventId);
            doThrow(new ImageStoreException("Failed to store image for event with id: " + eventId))
                    .when(fileStorageService).storeFileInEntityFolder(any(EImageFolder.class), anyLong(), any(MultipartFile.class));

            assertThrows(ImageStoreException.class, () -> eventImageService.saveUploadedEventImage(eventId, file));

            verify(eventService, times(1)).validateEvent(eventId);
            verify(fileStorageService, times(1)).storeFileInEntityFolder(EImageFolder.EVENT, eventId, file);
        }
    }

    @Nested
    class deleteEventImageById {
        @Test
        @DisplayName("Should delete event image from database with success")
        void shouldDeleteEventImageFromDatabaseWithSuccess() {
            Long eventId = 1L;
            Long imageId = 1L;
            String fileName = "image.png";
            EventImage eventImage = EventImage.builder()
                    .id(imageId)
                    .fileName(fileName)
                    .build();

            doNothing().when(eventService).validateEvent(eventId);
            when(eventImageRepository.findById(imageId)).thenReturn(Optional.of(eventImage));
            doNothing().when(fileStorageService).deleteImageFromStorage(any(EImageFolder.class), anyLong(), anyString());

            assertDoesNotThrow(() -> eventImageService.deleteEventImageById(eventId, imageId));

            verify(eventService, times(1)).validateEvent(eventId);
            verify(eventImageRepository, times(1)).findById(imageId);
            verify(fileStorageService, times(1)).deleteImageFromStorage(EImageFolder.EVENT, eventId, fileName);
            verify(eventImageRepository, times(1)).deleteById(imageId);
        }

        @Test
        @DisplayName("Should throw ImageStoreException when image does not exist")
        void shouldThrowExceptionWhenImageDoesNotExists() {
            Long eventId = 1L;
            Long imageId = 1L;

            doNothing().when(eventService).validateEvent(eventId);
            when(eventImageRepository.findById(eventId)).thenReturn(Optional.empty());

            assertThrows(ImageStoreException.class, () -> eventImageService.deleteEventImageById(eventId, imageId));

            verify(eventService, times(1)).validateEvent(eventId);
            verify(fileStorageService, never()).deleteImageFromStorage(any(EImageFolder.class), anyLong(), anyString());
            verify(eventImageRepository, never()).deleteById(anyLong());
        }
    }

    private EventImage createEventImage(Long imageId, String imageUrl, String fileName, Long eventId) {
        return EventImage.builder()
                .id(imageId)
                .imageUrl(imageUrl)
                .fileName(fileName)
                .fileSize(1024L)
                .fileType("image/png")
                .event(createEvent(eventId))
                .build();
    }

    private Event createEvent(Long eventId) {
        return Event.builder().id(eventId).build();
    }

    private MultipartFile createMultipartFileMock(String fileName, Long fileSize, String contentType) {
        MultipartFile file = mock(MultipartFile.class);

        lenient().when(file.getOriginalFilename()).thenReturn(fileName);
        lenient().when(file.getSize()).thenReturn(fileSize);
        lenient().when(file.getContentType()).thenReturn(contentType);

        return file;
    }

    private void assertEventImageDetails(EventImage expected, EventImage actual) {
        assertAll(
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getFileName(), actual.getFileName()),
                () -> assertEquals(expected.getFileSize(), actual.getFileSize()),
                () -> assertEquals(expected.getFileType(), actual.getFileType()),
                () -> assertEquals(expected.getEvent().getId(), actual.getEvent().getId())
        );
    }

}
