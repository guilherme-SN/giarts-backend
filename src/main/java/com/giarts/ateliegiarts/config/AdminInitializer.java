package com.giarts.ateliegiarts.config;

import com.giarts.ateliegiarts.enums.EUserRole;
import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.model.UserRole;
import com.giarts.ateliegiarts.repository.UserRepository;
import com.giarts.ateliegiarts.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminInitializer {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    public void createFirstAdminUser() {
        Optional<User> optionalUser = userRepository.findByEmail(adminEmail);
        if (optionalUser.isPresent()) {
            return;
        }

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(getOrCreateUserRole(EUserRole.ROLE_ADMIN));

        User user = User.builder()
                .name("ADMIN")
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .userRoles(userRoles)
                .build();

        userRepository.save(user);
    }

    private UserRole getOrCreateUserRole(EUserRole userRole) {
        Optional<UserRole> userRoleOptional = userRoleRepository.findByUserRole(userRole);
        if (userRoleOptional.isPresent()) {
            return userRoleOptional.get();
        }

        UserRole newUserRole = UserRole.builder().userRole(userRole).build();
        return userRoleRepository.save(newUserRole);
    }
}
