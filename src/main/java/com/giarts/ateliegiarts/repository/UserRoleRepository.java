package com.giarts.ateliegiarts.repository;

import com.giarts.ateliegiarts.enums.EUserRole;
import com.giarts.ateliegiarts.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUserRole(EUserRole userRole);
}
