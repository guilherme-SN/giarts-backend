package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.enums.EImageFolder;
import com.giarts.ateliegiarts.exception.ImageStoreException;
import lombok.RequiredArgsConstructor;
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
public class FileStorageService {
    @Value("${storage.location}")
    private String uploadLocation;

    public void storeFileInEntityFolder(EImageFolder entityFolder, Long entityId, MultipartFile file) {
        try {
            Path uploadDirectory = Paths.get(uploadLocation, entityFolder.getFolderName(), entityId.toString());
            Files.createDirectories(uploadDirectory);

            Path fileLocation = uploadDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new ImageStoreException("Failed to store image for " + entityFolder.name() + " with id: " + entityId, ex);
        }
    }

    public void deleteImageFromStorage(EImageFolder entityFolder, Long entityId, String fileName) {
        Path imagePath = Paths.get(uploadLocation, entityFolder.getFolderName(), entityId.toString(), fileName);

        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException ex) {
            throw new ImageStoreException("Failed to delete image from storage: " + fileName, ex);
        }
    }
}
