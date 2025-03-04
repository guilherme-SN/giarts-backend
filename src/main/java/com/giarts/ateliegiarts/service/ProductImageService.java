package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.enums.EImageFolder;
import com.giarts.ateliegiarts.exception.ImageStoreException;
import com.giarts.ateliegiarts.model.Product;
import com.giarts.ateliegiarts.model.ProductImage;
import com.giarts.ateliegiarts.repository.ProductImageRepository;
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
public class ProductImageService {
    @Value("${server.url}")
    private String serverUrl;

    @Value("${storage.location}")
    private String uploadLocation;

    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final ProductImageRepository productImageRepository;

    public List<ProductImage> getAllProductImages(Long productId) {
        log.info("Retrieving all product images for product ID: {}", productId);

        productService.validateProduct(productId);
        List<ProductImage> productImages = productImageRepository.findAllByProductId(productId);

        log.debug("Found {} images for product ID: {}", productImages.size(), productId);

        return productImages;
    }

    public ProductImage saveUploadedProductImage(Long productId, MultipartFile file, boolean isMainImage) {
        log.info("Saving uploaded image for product ID: {}. Image name: {}", productId, file.getOriginalFilename());

        productService.validateProduct(productId);

        fileStorageService.storeFileInEntityFolder(EImageFolder.PRODUCT, productId, file);

        String imageUrl = ImageUrlGenerator.generateImageUrl(serverUrl, EImageFolder.PRODUCT, productId, file.getOriginalFilename());
        log.debug("Generated image URL: {}", imageUrl);

        ProductImage productImage = buildProductImage(productService.getProductEntityById(productId), file, imageUrl, isMainImage);
        ProductImage savedProductImage = productImageRepository.save(productImage);

        log.info("Successfully saved image for product ID: {}. Image ID: {}", productId, savedProductImage.getId());

        return savedProductImage;
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

    public void deleteProductImageById(Long productId, Long imageId) {
        log.info("Deleting image with ID: {} for product ID: {}", imageId, productId);

        productService.validateProduct(productId);

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> {
                    log.warn("Image with ID: {} not found for product ID: {}", imageId, productId);
                    return new ImageStoreException("Image with id " + imageId + " does not exists");
                });

        fileStorageService.deleteImageFromStorage(EImageFolder.PRODUCT, productId, productImage.getFileName());
        productImageRepository.deleteById(imageId);

        log.info("Successfully deleted image with ID: {} for product ID: {}", imageId, productId);
    }
}