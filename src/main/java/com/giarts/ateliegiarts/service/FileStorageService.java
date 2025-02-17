package com.giarts.ateliegiarts.service;

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

    public void storeFileInProductFolder(Long productId, MultipartFile file) throws IOException {
        Path uploadDirectory = Paths.get(uploadLocation, productId.toString());
        Files.createDirectories(uploadDirectory);

        Path fileLocation = uploadDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);
    }
}
