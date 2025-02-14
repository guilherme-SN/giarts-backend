package com.giarts.ateliegiarts.repository;

import com.giarts.ateliegiarts.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
