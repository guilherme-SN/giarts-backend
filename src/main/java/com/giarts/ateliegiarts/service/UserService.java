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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;

    public List<ResponseUserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(ResponseUserDTO::fromEntity).collect(Collectors.toList());
    }

    public ResponseUserDTO getUserById(Long userId) {
        validateUserAccess(userId);

        return userRepository.findById(userId).map(ResponseUserDTO::fromEntity)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public ResponseUserDTO createUser(CreateUserDTO createUserDTO) {
        if (userRepository.existsByEmail(createUserDTO.email())) {
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
        validateUserAccess(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        updateUserFields(user, updateUserDTO);

        User savedUser = userRepository.save(user);
        return ResponseUserDTO.fromEntity(savedUser);
    }

    private void updateUserFields(User user, UpdateUserDTO updateUserDTO) {
        user.setName(updateUserDTO.name());
        user.setEmail(updateUserDTO.email());
        user.setPassword(passwordEncoder.encode(updateUserDTO.password()));
    }

    public void deleteUserById(Long userId) {
        validateUserAccess(userId);

        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException(userId);
        }
    }

    private void validateUserAccess(Long expectedUserId) {
        if (!securityService.canAccessUser(expectedUserId)) {
            throw new AccessDeniedException("Access denied: You can only access your own information");
        }
    }
}
