package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.enums.EImageFolder;
import com.giarts.ateliegiarts.exception.ImageStoreException;
import com.giarts.ateliegiarts.model.Event;
import com.giarts.ateliegiarts.model.EventImage;
import com.giarts.ateliegiarts.repository.EventImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventImageService {
    @Value("${storage.location}")
    private String uploadLocation;

    @Value("${server.url}")
    private String serverUrl;

    private final EventService eventService;
    private final FileStorageService fileStorageService;
    private final EventImageRepository eventImageRepository;

    public List<EventImage> getAllEventImages(Long eventId) {
        eventService.validateEvent(eventId);

        return eventImageRepository.findAllByEventId(eventId);
    }

    public EventImage saveUploadedEventImage(Long eventId, MultipartFile file) {
        eventService.validateEvent(eventId);

        fileStorageService.storeFileInEntityFolder(EImageFolder.EVENT, eventId, file);

        String imageUrl = generateImageUrl(eventId, file.getOriginalFilename());

        EventImage eventImage = buildEventImage(eventService.getEventById(eventId), file, imageUrl);
        return eventImageRepository.save(eventImage);
    }

    private String generateImageUrl(Long eventId, String fileName) {
        return String.format("%s/events/%d/images/%s", serverUrl, eventId, fileName);
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
        eventService.validateEvent(eventId);

        EventImage eventImage = eventImageRepository.findById(eventId)
                .orElseThrow(() -> new ImageStoreException("Image with id" + imageId + " does not exists"));

        fileStorageService.deleteImageFromStorage(EImageFolder.EVENT, eventId, eventImage.getFileName());
        eventImageRepository.deleteById(imageId);
    }
}
