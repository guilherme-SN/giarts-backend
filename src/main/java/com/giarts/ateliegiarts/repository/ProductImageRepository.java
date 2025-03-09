package com.giarts.ateliegiarts.repository;

import com.giarts.ateliegiarts.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query(value = """
            SELECT *
            FROM product_images
            WHERE product_id = :productId
            """, nativeQuery = true)
    List<ProductImage> findAllByProductId(@Param(value = "productId") Long productId);
}
