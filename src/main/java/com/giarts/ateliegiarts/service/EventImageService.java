package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.enums.EImageFolder;
import com.giarts.ateliegiarts.exception.ImageStoreException;
import com.giarts.ateliegiarts.model.Event;
import com.giarts.ateliegiarts.model.EventImage;
import com.giarts.ateliegiarts.repository.EventImageRepository;
import com.giarts.ateliegiarts.util.ImageUrlGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventImageService {
    @Value("${server.url}")
    private String serverUrl;

    @Value("${storage.location}")
    private String uploadLocation;

    private final EventService eventService;
    private final FileStorageService fileStorageService;
    private final EventImageRepository eventImageRepository;

    public List<EventImage> getAllEventImages(Long eventId) {
        log.info("Retrieving all event images for event ID: {}", eventId);

        eventService.validateEvent(eventId);
        List<EventImage> eventImages = eventImageRepository.findAllByEventId(eventId);

        log.debug("Found {} images for event ID: {}", eventImages.size(), eventId);

        return eventImages;
    }

    public EventImage saveUploadedEventImage(Long eventId, MultipartFile file) {
        log.info("Saving uploaded image for event ID: {}. Image name: {}", eventId, file.getOriginalFilename());

        eventService.validateEvent(eventId);

        fileStorageService.storeFileInEntityFolder(EImageFolder.EVENT, eventId, file);

        String imageUrl = ImageUrlGenerator.generateImageUrl(serverUrl, EImageFolder.EVENT, eventId, file.getOriginalFilename());
        log.debug("Generated image URL: {}", imageUrl);

        EventImage eventImage = buildEventImage(eventService.getEventEntityById(eventId), file, imageUrl);
        EventImage savedEventImage = eventImageRepository.save(eventImage);

        log.info("Successfully saved image for event ID: {}. Image ID: {}", eventId, savedEventImage.getId());

        return savedEventImage;
    }

    private EventImage buildEventImage(Event event, MultipartFile file, String imageUrl) {
        return EventImage.builder()
                .event(event)
                .imageUrl(imageUrl)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .build();
    }

    public void deleteEventImageById(Long eventId, Long imageId) {
        log.info("Deleting image with ID: {} for event ID: {}", imageId, eventId);

        eventService.validateEvent(eventId);

        EventImage eventImage = eventImageRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Image with ID: {} not found for event ID: {}", imageId, eventId);
                    return new ImageStoreException("Image with id " + imageId + " does not exists");
                });

        fileStorageService.deleteImageFromStorage(EImageFolder.EVENT, eventId, eventImage.getFileName());
        eventImageRepository.deleteById(imageId);

        log.info("Successfully deleted image with ID: {} for event ID: {}", imageId, eventId);
    }
}
