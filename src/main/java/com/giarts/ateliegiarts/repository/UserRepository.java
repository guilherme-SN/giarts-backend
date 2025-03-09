package com.giarts.ateliegiarts.repository;

import com.giarts.ateliegiarts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = """
            SELECT *
            FROM users u
            WHERE u.email = :email
            """, nativeQuery = true)
    Optional<User> findByEmail(@Param(value = "email") String email);

    @Query(value = """
            SELECT COUNT(u) > 0
            FROM User u
            WHERE u.email = :email
            """)
    Boolean existsByEmail(@Param(value = "email") String email);
}
