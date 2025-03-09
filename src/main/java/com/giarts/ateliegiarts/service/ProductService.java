package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.product.CreateProductDTO;
import com.giarts.ateliegiarts.dto.product.ResponseProductDTO;
import com.giarts.ateliegiarts.dto.product.UpdateProductDTO;
import com.giarts.ateliegiarts.exception.ProductNotFoundException;
import com.giarts.ateliegiarts.model.Product;
import com.giarts.ateliegiarts.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public Page<ResponseProductDTO> getAllProducts(Pageable pageable) {
        log.info("Retrieving all products from page: {} with size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<ResponseProductDTO> products = productRepository.findAllProductsPaginated(pageable).map(ResponseProductDTO::fromEntity);

        log.debug("Found {} products in page: {}", products.getNumberOfElements(), pageable.getPageNumber());

        return products;
    }

    public Product getProductEntityById(Long productId) {
        log.info("Retrieving product entity by ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product entity with ID: {} not found", productId);
                    return new ProductNotFoundException(productId);
                });

        log.debug("Successfully retrieved product entity with ID: {}", productId);

        return product;
    }

    public ResponseProductDTO getProductById(Long productId) {
        log.info("Retrieving product by ID: {}", productId);

        ResponseProductDTO product = productRepository.findById(productId).map(ResponseProductDTO::fromEntity)
                .orElseThrow(() -> {
                    log.warn("Product with ID: {} not found", productId);
                    return new ProductNotFoundException(productId);
                });

        log.debug("Successfully retrieved product with ID: {}", product);

        return product;
    }

    public ResponseProductDTO createProduct(CreateProductDTO createProductDTO) {
        log.info("Creating new product with name: {}", createProductDTO.name());

        Product product = new Product(createProductDTO);
        Product savedProduct = productRepository.save(product);

        log.debug("Successfully created product with ID: {}", savedProduct.getId());

        return ResponseProductDTO.fromEntity(savedProduct);
    }

    public ResponseProductDTO updateProductById(Long productId, UpdateProductDTO updateProductDTO) {
        log.info("Updating product with ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product with ID: {} not found while updating", productId);
                    return new ProductNotFoundException(productId);
                });

        updateProductFields(product, updateProductDTO);

        Product savedProduct = productRepository.save(product);

        log.debug("Successfully updated product with ID: {}", savedProduct.getId());

        return ResponseProductDTO.fromEntity(savedProduct);
    }

    private void updateProductFields(Product product, UpdateProductDTO updateProductDTO) {
        product.setName(updateProductDTO.name());
        product.setDescription(updateProductDTO.description());
        product.setProductType(updateProductDTO.productType());

    }

    public void deleteProductById(Long productId) {
        log.info("Deleting product with ID: {}", productId);

        validateProduct(productId);
        productRepository.deleteById(productId);

        log.info("Successfully deleted product with ID: {}", productId);
    }

    public void validateProduct(Long productId) {
        log.info("Validating product with ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            log.warn("Error in validation. Product with ID: {} not found", productId);
            throw new ProductNotFoundException(productId);
        }

        log.debug("Validation completed for product with ID: {}", productId);
    }
}
