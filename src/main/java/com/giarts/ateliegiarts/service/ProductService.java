package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.ProductDTO;
import com.giarts.ateliegiarts.exception.ProductNotFoundException;
import com.giarts.ateliegiarts.model.Product;
import com.giarts.ateliegiarts.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product(productDTO);
        return productRepository.save(product);
    }

    public Product updateProductById(Long productId, ProductDTO updateProductDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        updateProductFields(product, updateProductDTO);

        return productRepository.save(product);
    }

    private void updateProductFields(Product product, ProductDTO updateProductDTO) {
        product.setName(updateProductDTO.getName());
        product.setDescription(updateProductDTO.getDescription());
        product.setProductType(updateProductDTO.getProductType());

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
