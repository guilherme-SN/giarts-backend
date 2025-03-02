package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.product.CreateProductDTO;
import com.giarts.ateliegiarts.dto.product.ResponseProductDTO;
import com.giarts.ateliegiarts.dto.product.UpdateProductDTO;
import com.giarts.ateliegiarts.exception.ProductNotFoundException;
import com.giarts.ateliegiarts.model.Product;
import com.giarts.ateliegiarts.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ResponseProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(ResponseProductDTO::fromEntity).collect(Collectors.toList());
    }

    public Product getProductEntityById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public ResponseProductDTO getProductById(Long productId) {
        return productRepository.findById(productId).map(ResponseProductDTO::fromEntity)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public ResponseProductDTO createProduct(CreateProductDTO createProductDTO) {
        Product product = new Product(createProductDTO);
        Product savedProduct = productRepository.save(product);

        return ResponseProductDTO.fromEntity(savedProduct);
    }

    public ResponseProductDTO updateProductById(Long productId, UpdateProductDTO updateProductDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        updateProductFields(product, updateProductDTO);

        Product savedProduct = productRepository.save(product);
        return ResponseProductDTO.fromEntity(savedProduct);
    }

    private void updateProductFields(Product product, UpdateProductDTO updateProductDTO) {
        product.setName(updateProductDTO.name());
        product.setDescription(updateProductDTO.description());
        product.setProductType(updateProductDTO.productType());

    }

    public void deleteProductById(Long productId) {
        validateProduct(productId);
        productRepository.deleteById(productId);
    }

    public void validateProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }
    }
}
