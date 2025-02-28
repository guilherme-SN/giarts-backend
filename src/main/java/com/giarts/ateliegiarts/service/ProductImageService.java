package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.enums.EImageFolder;
import com.giarts.ateliegiarts.exception.ImageStoreException;
import com.giarts.ateliegiarts.model.Product;
import com.giarts.ateliegiarts.model.ProductImage;
import com.giarts.ateliegiarts.repository.ProductImageRepository;
import com.giarts.ateliegiarts.util.ImageUrlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    @Value("${storage.location}")
    private String uploadLocation;

    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final ProductImageRepository productImageRepository;

    public List<ProductImage> getAllProductImages(Long productId) {
        productService.validateProduct(productId);

        return productImageRepository.findAllByProductId(productId);
    }

    public ProductImage saveUploadedProductImage(Long productId, MultipartFile file, boolean isMainImage) {
        productService.validateProduct(productId);

        fileStorageService.storeFileInEntityFolder(EImageFolder.PRODUCT, productId, file);

        String imageUrl = ImageUrlGenerator.generateImageUrl(EImageFolder.PRODUCT, productId, file.getOriginalFilename());

        ProductImage productImage = buildProductImage(productService.getProductById(productId), file, imageUrl, isMainImage);
        return productImageRepository.save(productImage);
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
        productService.validateProduct(productId);

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ImageStoreException("Image with id " + imageId + " does not exists"));

        fileStorageService.deleteImageFromStorage(EImageFolder.PRODUCT, productId, productImage.getFileName());
        productImageRepository.deleteById(imageId);
    }
}