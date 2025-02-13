package com.giarts.ateliegiarts.exception;

import jakarta.persistence.EntityNotFoundException;

public class ProductNotFoundException extends EntityNotFoundException {
    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId.toString());
    }
}
