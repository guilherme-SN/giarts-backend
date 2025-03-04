package com.giarts.ateliegiarts.service.unit;

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
import com.giarts.ateliegiarts.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Nested
    class getAllUser {
        @Test
        @DisplayName("Should get all users with success")
        void shouldGetAllUsersWithSuccess() {
            List<User> users = List.of(
                    createUser(1L, "User 1", "email1@email.com", "password1", EUserRole.ROLE_CUSTOMER),
                    createUser(2L, "User 2", "email2@email.com", "password2", EUserRole.ROLE_ADMIN)
            );

            when(userRepository.findAll()).thenReturn(users);

            List<ResponseUserDTO> usersRetrieved = userService.getAllUsers();

            assertNotNull(usersRetrieved);
            assertUserDetails(users.get(0), usersRetrieved.get(0));
            assertUserDetails(users.get(1), usersRetrieved.get(1));

            verify(userRepository, times(1)).findAll();
        }
    }

    @Nested
    class getUserById {
        @Test
        @DisplayName("Should get user by ID with success when user exists")
        void shouldGetUserByIdWithSuccessWhenUserExists() {
            User user = createUser(1L, "User", "email@email.com", "password", EUserRole.ROLE_CUSTOMER);

            when(securityService.canAccessUser(anyLong())).thenReturn(true);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            ResponseUserDTO userRetrieved = userService.getUserById(user.getId());

            assertNotNull(userRetrieved);
            assertUserDetails(user, userRetrieved);

            verify(securityService, times(1)).canAccessUser(anyLong());
            verify(userRepository, times(1)).findById(user.getId());
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exists")
        void shouldThrowExceptionWhenUserDoesNotExists() {
            when(securityService.canAccessUser(anyLong())).thenReturn(true);
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));

            verify(securityService, times(1)).canAccessUser(anyLong());
            verify(userRepository, times(1)).findById(anyLong());
        }
    }

    @Nested
    class createUser {
        @Test
        @DisplayName("Should create user with success")
        void shouldCreateUserWithSuccess() {
            CreateUserDTO userDTO = new CreateUserDTO("User", "email@email.com", "password");
            User user = createUser(1L, "User", "email@email.com", "password", EUserRole.ROLE_CUSTOMER);
            UserRole userRole = new UserRole(1L, EUserRole.ROLE_CUSTOMER);

            when(userRepository.existsByEmail(userDTO.email())).thenReturn(false);
            when(userRoleRepository.findByUserRole(EUserRole.ROLE_CUSTOMER)).thenReturn(Optional.of(userRole));
            when(passwordEncoder.encode(userDTO.password())).thenReturn(userDTO.password());
            when(userRepository.save(userArgumentCaptor.capture())).thenReturn(user);

            ResponseUserDTO createdUser = userService.createUser(userDTO);

            assertNotNull(createdUser);
            assertUserDetails(user, ResponseUserDTO.fromEntity(userArgumentCaptor.getValue()));

            verify(userRepository, times(1)).existsByEmail(userDTO.email());
            verify(userRoleRepository, times(1)).findByUserRole(EUserRole.ROLE_CUSTOMER);
            verify(passwordEncoder, times(1)).encode(userDTO.password());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when email is already used")
        void shouldThrowExceptionWhenEmailAlreadyUsed() {
            CreateUserDTO userDTO = new CreateUserDTO("User", "email@email.com", "password");

            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            assertThrows(DuplicateEmailException.class, () -> userService.createUser(userDTO));

            verify(userRepository, times(1)).existsByEmail(anyString());
        }
    }

    @Nested
    class updateUser {
        @Test
        @DisplayName("Should update user with success")
        void shouldUpdateUserWithSuccess() {
            User user = createUser(1L, "Name", "email@email.com", "encrypted password", EUserRole.ROLE_CUSTOMER);
            UpdateUserDTO updateUserDTO = new UpdateUserDTO("Name Updated", "emailupdated@email.com",
                    "password updated", Set.of(new UserRole(1L, EUserRole.ROLE_CUSTOMER)));

            when(securityService.canAccessUser(anyLong())).thenReturn(true);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(passwordEncoder.encode(updateUserDTO.password())).thenReturn(updateUserDTO.password());
            when(userRepository.save(userArgumentCaptor.capture())).thenReturn(user);

            ResponseUserDTO updatedUser = userService.updateUserById(user.getId(), updateUserDTO);

            assertNotNull(updatedUser);
            assertUserDetails(updateUserDTO, ResponseUserDTO.fromEntity(userArgumentCaptor.getValue()));

            verify(securityService, times(1)).canAccessUser(anyLong());
            verify(userRepository, times(1)).findById(user.getId());
            verify(passwordEncoder, times(1)).encode(updateUserDTO.password());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exists")
        void shouldThrowExceptionWhenUserDoesNotExists() {
            Long userId = 1L;
            UpdateUserDTO updateUserDTO = new UpdateUserDTO("Name Updated", "emailupdated@email.com",
                    "password updated", Set.of());

            when(securityService.canAccessUser(anyLong())).thenReturn(true);
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.updateUserById(userId, updateUserDTO));

            verify(userRepository, times(1)).findById(anyLong());
        }
    }

    @Nested
    class deleteUserById {
        @Test
        @DisplayName("Should delete user with success")
        void shouldDeleteUserWithSuccess() {
            Long userId = 1L;

            when(securityService.canAccessUser(anyLong())).thenReturn(true);
            when(userRepository.existsById(userId)).thenReturn(true);

            assertDoesNotThrow(() -> userService.deleteUserById(userId));

            verify(securityService, times(1)).canAccessUser(anyLong());
            verify(userRepository, times(1)).existsById(userId);
            verify(userRepository, times(1)).deleteById(userId);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exists")
        void shouldThrowExceptionWhenUserDoesNotExists() {
            when(securityService.canAccessUser(anyLong())).thenReturn(true);
            when(userRepository.existsById(anyLong())).thenReturn(false);

            assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(anyLong()));

            verify(userRepository, times(1)).existsById(anyLong());
            verify(userRepository, never()).deleteById(anyLong());
        }
    }

    private User createUser(Long id, String name, String email, String password, EUserRole userRole) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .password(password)
                .userRoles(Set.of(new UserRole(1L, userRole)))
                .build();
    }

    private void assertUserDetails(User expected, ResponseUserDTO actual) {
        assertAll(
                () -> assertEquals(expected.getName(), actual.name()),
                () -> assertEquals(expected.getEmail(), actual.email()),
                () -> assertEquals(expected.getPassword(), actual.password()),
                () -> assertUserRoles(expected.getUserRoles(), actual.userRoles())
        );
    }

    private void assertUserDetails(UpdateUserDTO expected, ResponseUserDTO actual) {
        assertUserDetails(
                User.builder()
                        .name(expected.name())
                        .email(expected.email())
                        .password(expected.password())
                        .userRoles(expected.userRoles())
                        .build(),
                actual);
    }

    private void assertUserRoles(Set<UserRole> expected, Set<UserRole> actual) {
        for (UserRole expectedUserRole : expected) {
            assertTrue(actual.contains(expectedUserRole));
        }
    }
}
