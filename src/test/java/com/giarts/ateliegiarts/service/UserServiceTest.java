package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.UserDTO;
import com.giarts.ateliegiarts.enums.EUserRole;
import com.giarts.ateliegiarts.exception.DuplicateEmailException;
import com.giarts.ateliegiarts.exception.UserNotFoundException;
import com.giarts.ateliegiarts.model.User;
import com.giarts.ateliegiarts.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should get all users with success")
    void shouldGetAllUsersWithSuccess() {
        List<User> users = List.of(
                createUser(1L, "User 1", "email1@email.com", "password1", EUserRole.GUEST),
                createUser(2L, "User 2", "email2@email.com", "password2", EUserRole.ADMIN)
                );

        when(userRepository.findAll()).thenReturn(users);

        List<User> usersRetrieved = userService.getAllUsers();

        assertNotNull(usersRetrieved);
        assertUserDetails(users.get(0), usersRetrieved.get(0));
        assertUserDetails(users.get(1), usersRetrieved.get(1));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get user by ID with success when user exists")
    void shouldGetUserByIdWithSuccessWhenUserExists() {
        User user = createUser(1L, "User", "email@email.com", "password", EUserRole.GUEST);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User userRetrieved = userService.getUserById(user.getId());

        assertNotNull(userRetrieved);
        assertUserDetails(user, userRetrieved);

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exists")
    void shouldThrowExceptionWhenUserDoesNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Should create user with success")
    void shouldCreateUserWithSuccess() {
        UserDTO userDTO = createUserDTO("User", "email@email.com", "password", EUserRole.GUEST);
        User user = createUser(1L, "User", "email@email.com", "password", EUserRole.GUEST);

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(userDTO);

        assertNotNull(createdUser);
        assertUserDetails(userDTO, createdUser);

        verify(userRepository, times(1)).existsByEmail(userDTO.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when email already used")
    void shouldThrowExceptionWhenEmailAlreadyUsed() {
        UserDTO userDTO = createUserDTO("User", "email@email.com", "password", EUserRole.GUEST);

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.createUser(userDTO));

        verify(userRepository, times(1)).existsByEmail(anyString());
    }

    private User createUser(Long id, String name, String email, String password, EUserRole userRole) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .password(password)
                .userRole(userRole)
                .build();
    }

    private UserDTO createUserDTO(String name, String email, String password, EUserRole userRole) {
        return UserDTO.builder()
                .name(name)
                .email(email)
                .password(password)
                .userRole(userRole)
                .build();
    }

    private void assertUserDetails(User expected, User actual) {
        assertAll(
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getEmail(), actual.getEmail()),
                () -> assertEquals(expected.getPassword(), actual.getPassword()),
                () -> assertEquals(expected.getUserRole(), actual.getUserRole())
        );

        if (expected.getId() != null) {
            assertEquals(expected.getId(), actual.getId());
        }
    }

    private void assertUserDetails(UserDTO expected, User actual) {
        assertUserDetails(
                User.builder()
                        .name(expected.getName())
                        .email(expected.getEmail())
                        .password(expected.getPassword())
                        .userRole(expected.getUserRole())
                        .build(),
                actual);
    }
}
