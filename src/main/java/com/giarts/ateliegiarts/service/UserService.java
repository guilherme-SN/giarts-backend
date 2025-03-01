package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.UserDTO;
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

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        validateUserAccess(userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public User createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateEmailException(userDTO.getEmail());
        }

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(getOrCreateUserRole(EUserRole.ROLE_CUSTOMER));

        User user = User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .userRoles(userRoles)
                .build();

        return userRepository.save(user);
    }

    private UserRole getOrCreateUserRole(EUserRole userRole) {
        Optional<UserRole> userRoleOptional = userRoleRepository.findByUserRole(userRole);
        if (userRoleOptional.isPresent()) {
            return userRoleOptional.get();
        }

        UserRole newUserRole = UserRole.builder().userRole(userRole).build();
        return userRoleRepository.save(newUserRole);
    }

    public User updateUserById(Long userId, UserDTO updatedUserDTO) {
        validateUserAccess(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        updateUserFields(user, updatedUserDTO);

        return userRepository.save(user);
    }

    private void updateUserFields(User user, UserDTO updatedUserDTO) {
        user.setName(updatedUserDTO.getName());
        user.setEmail(updatedUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(updatedUserDTO.getPassword()));
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
