package com.giarts.ateliegiarts.controller;

import com.giarts.ateliegiarts.dto.ImageUploadResponse;
import com.giarts.ateliegiarts.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/images")
@Tag(name = "ProductImage Controller", description = "Operations related to the images of the products")
@RequiredArgsConstructor
public class ProductImageController {
    private final ProductImageService productImageService;

    @Operation(summary = "List all images of a product")
    @ApiResponse(responseCode = "200", description = "Images retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping
    public ResponseEntity<List<String>> getAllProductImages(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(productImageService.getAllProducts(productId));
    }

    @Operation(summary = "Upload an image to a product")
    @ApiResponse(responseCode = "200", description = "Image uploaded successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PostMapping
    public ResponseEntity<ImageUploadResponse> uploadImage(@PathVariable("productId") Long productId,
                                                           @RequestParam("file") MultipartFile file) {
        String imageUrl = productImageService.uploadImage(productId, file);

        return ResponseEntity.ok(new ImageUploadResponse(
                imageUrl,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
        ));
    }
}
