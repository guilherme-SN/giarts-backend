package com.giarts.ateliegiarts.dto.product;

import com.giarts.ateliegiarts.enums.EProductType;
import com.giarts.ateliegiarts.model.Product;

import java.time.LocalDateTime;

public record ResponseProductDTO(
        Long id,
        String name,
        String description,
        EProductType productType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResponseProductDTO fromEntity(Product product) {
        return new ResponseProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getProductType(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
