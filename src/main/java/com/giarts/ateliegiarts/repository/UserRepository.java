package com.giarts.ateliegiarts.repository;

import com.giarts.ateliegiarts.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
