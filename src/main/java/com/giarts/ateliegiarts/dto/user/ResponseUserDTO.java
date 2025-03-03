package com.giarts.ateliegiarts.dto.user;

import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.model.UserRole;

import java.time.LocalDateTime;
import java.util.Set;

public record ResponseUserDTO(
        Long id,
        String name,
        String email,
        String password,
        Set<UserRole> userRoles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ResponseUserDTO fromEntity(User user) {
        return new ResponseUserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getUserRoles(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
