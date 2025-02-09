package com.giarts.ateliegiarts.service;

import java.util.List;

import com.giarts.ateliegiarts.dto.UserDTO;
import com.giarts.ateliegiarts.exception.UserNotFoundException;
import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.repository.UserRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public User createUser(UserDTO userDTO) {
        User user = new User(userDTO);
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
        user.setPassword(updatedUserDTO.getPassword());
        user.setUserRole(updatedUserDTO.getUserRole());
    }

    public void deleteUserById(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException(userId);
        }
    }
}
