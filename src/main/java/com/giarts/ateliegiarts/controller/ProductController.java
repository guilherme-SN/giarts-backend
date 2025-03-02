package com.giarts.ateliegiarts.controller;

import com.giarts.ateliegiarts.dto.product.CreateProductDTO;
import com.giarts.ateliegiarts.dto.product.ResponseProductDTO;
import com.giarts.ateliegiarts.dto.product.UpdateProductDTO;
import com.giarts.ateliegiarts.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Product Controller", description = "Operations related to products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "List all products")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ResponseProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Get a product by ID")
    @ApiResponse(responseCode = "200", description = "Product retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/{productId}")
    public ResponseEntity<ResponseProductDTO> getProductById(@PathVariable("productId") Long productId) {
        ResponseProductDTO response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a product")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid product input")
    @PostMapping
    public ResponseEntity<ResponseProductDTO> createProduct(@RequestBody @Valid CreateProductDTO createProductDTO) {
        ResponseProductDTO response = productService.createProduct(createProductDTO);

        URI location = URI.create("/api/products/" + response.id().toString());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Update a product")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid product input")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{productId}")
    public ResponseEntity<ResponseProductDTO> updateProductById(@PathVariable("productId") Long productId,
                                                     @RequestBody @Valid UpdateProductDTO updateProductDTO) {
        ResponseProductDTO response = productService.updateProductById(productId, updateProductDTO);
        return ResponseEntity.ok(response);
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
