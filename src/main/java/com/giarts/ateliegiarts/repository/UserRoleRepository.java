package com.giarts.ateliegiarts.repository;

import com.giarts.ateliegiarts.enums.EUserRole;
import com.giarts.ateliegiarts.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    @Query(value = """
            SELECT *
            FROM roles
            WHERE user_role = :userRole
            """, nativeQuery = true)
    Optional<UserRole> findByUserRole(@Param(value = "userRole") EUserRole userRole);
}
