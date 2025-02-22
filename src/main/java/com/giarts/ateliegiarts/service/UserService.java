package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.UserDTO;
import com.giarts.ateliegiarts.exception.DuplicateEmailException;
import com.giarts.ateliegiarts.exception.UserNotFoundException;
import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public User createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateEmailException(userDTO.getEmail());
        }

        User user = User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getEmail()))
                .build();

        return userRepository.save(user);
    }

    public User updateUserById(Long userId, UserDTO updatedUserDTO) {
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
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException(userId);
        }
    }
}
