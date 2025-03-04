package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.user.CreateUserDTO;
import com.giarts.ateliegiarts.dto.user.ResponseUserDTO;
import com.giarts.ateliegiarts.dto.user.UpdateUserDTO;
import com.giarts.ateliegiarts.enums.EUserRole;
import com.giarts.ateliegiarts.exception.DuplicateEmailException;
import com.giarts.ateliegiarts.exception.UserNotFoundException;
import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.model.UserRole;
import com.giarts.ateliegiarts.repository.UserRepository;
import com.giarts.ateliegiarts.repository.UserRoleRepository;
import com.giarts.ateliegiarts.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;

    public List<ResponseUserDTO> getAllUsers() {
        log.info("Retrieving all users");

        List<ResponseUserDTO> users = userRepository.findAll().stream().map(ResponseUserDTO::fromEntity).toList();

        log.debug("Found {} users", users.size());

        return users;
    }

    public ResponseUserDTO getUserById(Long userId) {
        log.info("Retrieving user by ID: {}", userId);

        validateUserAccess(userId);

        ResponseUserDTO user = userRepository.findById(userId).map(ResponseUserDTO::fromEntity)
                .orElseThrow(() -> {
                    log.warn("User with ID: {} not found", userId);
                    return new UserNotFoundException(userId);
                });

        log.debug("Successfully retrieved user with ID: {}", userId);

        return user;
    }

    public ResponseUserDTO createUser(CreateUserDTO createUserDTO) {
        log.info("Creating new user with name: {}", createUserDTO.name());

        if (userRepository.existsByEmail(createUserDTO.email())) {
            log.warn("Email \"{}\" already in use", createUserDTO.email());
            throw new DuplicateEmailException(createUserDTO.email());
        }

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(getOrCreateUserRole(EUserRole.ROLE_CUSTOMER));

        User user = User.builder()
                .name(createUserDTO.name())
                .email(createUserDTO.email())
                .password(passwordEncoder.encode(createUserDTO.password()))
                .userRoles(userRoles)
                .build();

        User savedUser = userRepository.save(user);

        log.debug("Successfully created user with ID: {}", savedUser.getId());

        return ResponseUserDTO.fromEntity(savedUser);
    }

    private UserRole getOrCreateUserRole(EUserRole userRole) {
        Optional<UserRole> userRoleOptional = userRoleRepository.findByUserRole(userRole);
        if (userRoleOptional.isPresent()) {
            return userRoleOptional.get();
        }

        UserRole newUserRole = UserRole.builder().userRole(userRole).build();
        return userRoleRepository.save(newUserRole);
    }

    public ResponseUserDTO updateUserById(Long userId, UpdateUserDTO updateUserDTO) {
        log.info("Updating user by ID: {}", userId);

        validateUserAccess(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with ID: {} not found while updating", userId);
                    return new UserNotFoundException(userId);
                });

        updateUserFields(user, updateUserDTO);

        User savedUser = userRepository.save(user);

        log.debug("Successfully updated user with ID: {}", savedUser.getId());

        return ResponseUserDTO.fromEntity(savedUser);
    }

    private void updateUserFields(User user, UpdateUserDTO updateUserDTO) {
        user.setName(updateUserDTO.name());
        user.setEmail(updateUserDTO.email());
        user.setPassword(passwordEncoder.encode(updateUserDTO.password()));
    }

    public void deleteUserById(Long userId) {
        log.info("Deleting user with ID: {}", userId);

        validateUserAccess(userId);

        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            log.info("Successfully deleted user with ID: {}", userId);
        } else {
            log.warn("User with ID: {} not found while deleting", userId);
            throw new UserNotFoundException(userId);
        }
    }

    private void validateUserAccess(Long expectedUserId) {
        log.info("Validating user with ID: {}", expectedUserId);

        if (!securityService.canAccessUser(expectedUserId)) {
            log.warn("User with ID: {} does not have permission to access this resource", expectedUserId);
            throw new AccessDeniedException("Access denied: You can only access your own information");
        }

        log.debug("Validation completed for user with ID: {}", expectedUserId);
    }
}
