package com.giarts.ateliegiarts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotNull(message = "The 'isMainImage' field is required")
    private Boolean isMainImage;

    @NotNull(message = "Product ID is required")
    private Long productId;
}
