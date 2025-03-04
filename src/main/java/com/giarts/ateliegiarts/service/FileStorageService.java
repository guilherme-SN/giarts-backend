package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.enums.EImageFolder;
import com.giarts.ateliegiarts.exception.ImageStoreException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    @Value("${storage.location}")
    private String uploadLocation;

    public void storeFileInEntityFolder(EImageFolder entityFolder, Long entityId, MultipartFile file) {
        try {
            log.info("Storing image {} in {} folder for entity with ID: {}", file.getOriginalFilename(), entityFolder.getFolderName(), entityId);

            Path uploadDirectory = Paths.get(uploadLocation, entityFolder.getFolderName(), entityId.toString());
            Files.createDirectories(uploadDirectory);

            Path fileLocation = uploadDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("Successfully stored image {} in {} folder for entity with ID: {}", file.getOriginalFilename(), entityFolder.getFolderName(), entityId);
        } catch (IOException ex) {
            log.error("Error while trying to store image {} in {} folder for entity with ID: {}", file.getOriginalFilename(), entityFolder.getFolderName(), entityId);
            throw new ImageStoreException("Failed to store image for " + entityFolder.name() + " with id: " + entityId, ex);
        }
    }

    public void deleteImageFromStorage(EImageFolder entityFolder, Long entityId, String fileName) {
        log.info("Deleting image {} for entity with ID: {} from {} folder", fileName, entityFolder, entityFolder.getFolderName());

        Path imagePath = Paths.get(uploadLocation, entityFolder.getFolderName(), entityId.toString(), fileName);

        try {
            Files.deleteIfExists(imagePath);
            log.info("Successfully deleted image {} for entity with ID: {} from {} folder", fileName, entityFolder, entityFolder.getFolderName());
        } catch (IOException ex) {
            log.error("");
            throw new ImageStoreException("Failed to delete image from storage: " + fileName, ex);
        }
    }
}
