package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.exception.ProductNotFoundException;
import com.giarts.ateliegiarts.repository.ProductImageRepository;
import com.giarts.ateliegiarts.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    @Value("${storage.location}")
    private String uploadLocation;

    @Value("${server.url}")
    private String serverUrl;

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    public List<String> getAllProducts(Long productId) {
        this.validateProduct(productId);

        Path uploadDirectory = Paths.get(uploadLocation, productId.toString());

        if (!Files.exists(uploadDirectory)) {
            return Collections.emptyList();
        }

        try (Stream<Path> stream = Files.list(uploadDirectory)) {
            return stream
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(fileName -> serverUrl + "/api/products/" + productId.toString() + "/images/" + fileName)
                    .toList();
        } catch (IOException ex) {
            throw new RuntimeException("Error while trying to get all images from product with id: " + productId.toString(), ex);
        }
    }

    public String uploadImage(Long productId, MultipartFile file) {
        this.validateProduct(productId);

        Path uploadDirectory = Paths.get(uploadLocation, productId.toString());

        try {
            Files.createDirectories(uploadDirectory);

            Path filePath = uploadDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename()));

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return serverUrl + "/api/products/" + productId.toString() + "/images/" + file.getOriginalFilename();
        } catch (IOException ex) {
            throw new RuntimeException("Error while trying to save image", ex);
        }
    }

    private void validateProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }
    }
}
