package com.giarts.ateliegiarts.repository;

import com.giarts.ateliegiarts.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = """
            SELECT *
            FROM products
            """, countQuery = """
            SELECT COUNT(*)
            FROM products
            """, nativeQuery = true)
    Page<Product> findAllProductsPaginated(Pageable pageable);
}
