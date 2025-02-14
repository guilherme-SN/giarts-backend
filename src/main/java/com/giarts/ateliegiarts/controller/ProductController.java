package com.giarts.ateliegiarts.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import com.giarts.ateliegiarts.dto.ProductDTO;
import com.giarts.ateliegiarts.model.Product;
import com.giarts.ateliegiarts.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@Tag(name = "Product Controller", description = "Operations related to products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "List all products")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Get a product by ID")
    @ApiResponse(responseCode = "200", description = "Product retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable("productId") Long productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Create a product")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid product input")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        Product createdProduct = productService.createProduct(productDTO);

        URI location = URI.create("/api/products/" + createdProduct.getId().toString());
        return ResponseEntity.created(location).body(createdProduct);
    }

    @Operation(summary = "Update a product")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid product input")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProductById(@PathVariable("productId") Long productId,
                                                     @RequestBody @Valid ProductDTO updateProductDTO) {
        Product updatedProduct = productService.updateProductById(productId, updateProductDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Delete a product")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProductById(@PathVariable("productId") Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.noContent().build();
    }
}
