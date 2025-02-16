package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.exception.ImageStoreException;
import com.giarts.ateliegiarts.model.Product;
import com.giarts.ateliegiarts.model.ProductImage;
import com.giarts.ateliegiarts.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    @Value("${storage.location}")
    private String uploadLocation;

    @Value("${server.url}")
    private String serverUrl;

    private final ProductService productService;
    private final ProductImageRepository productImageRepository;

    public List<ProductImage> getAllProductImages(Long productId) {
        productService.validateProduct(productId);

        return productImageRepository.findAllByProductId(productId);
    }

    public ProductImage saveUploadedImage(Long productId, MultipartFile file, boolean isMainImage) {
        productService.validateProduct(productId);

        try {
            this.storeFileInProductFolder(productId, file);

            String imageUrl = generateImageUrl(productId, file.getOriginalFilename());

            ProductImage productImage = buildProductImage(productService.getProductById(productId), file, imageUrl, isMainImage);
            return productImageRepository.save(productImage);
        } catch (IOException ex) {
            throw new ImageStoreException("Failed to store image for product with id: " + productId, ex);
        }
    }

    private void storeFileInProductFolder(Long productId, MultipartFile file) throws IOException {
        Path uploadDirectory = Paths.get(uploadLocation, productId.toString());
        Files.createDirectories(uploadDirectory);

        Path fileLocation = uploadDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    private String generateImageUrl(Long productId, String fileName) {
        return String.format("%s/api/products/%d/images/%s", serverUrl, productId, fileName);
    }

    private ProductImage buildProductImage(Product product, MultipartFile file, String imageUrl, boolean isMainImage) {
        return ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .isMainImage(isMainImage)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .build();
    }

    public void deleteProductImage(Long productId, Long imageId) {
        productService.validateProduct(productId);

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ImageStoreException("Image with id " + imageId + " does not exists"));

        productImageRepository.deleteById(imageId);
        this.deleteImageFromStorage(productId, productImage.getFileName());
    }

    private void deleteImageFromStorage(Long productId, String fileName) {
        Path imagePath = Paths.get(uploadLocation, productId.toString(), fileName);

        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException ex) {
            throw new ImageStoreException("Failed to delete image from storage: " + fileName, ex);
        }
    }
}