package com.giarts.ateliegiarts.dto.product;

import com.giarts.ateliegiarts.enums.EProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProductDTO(
        @NotBlank(message = "Name is required")
        String name,
        String description,
        @NotNull(message = "Product Type is required")
        EProductType productType
) {
}
