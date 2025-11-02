package com.adelmonsouza.userprofileservice.service;

import com.adelmonsouza.userprofileservice.dto.UserCreateDTO;
import com.adelmonsouza.userprofileservice.exception.EmailAlreadyExistsException;
import com.adelmonsouza.userprofileservice.exception.UserNotFoundException;
import com.adelmonsouza.userprofileservice.model.Role;
import com.adelmonsouza.userprofileservice.model.User;
import com.adelmonsouza.userprofileservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserCreateDTO testCreateDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .fullName("Test User")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testCreateDTO = new UserCreateDTO(
                "test@example.com",
                "password123",
                "Test User"
        );
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(testCreateDTO.email())).thenReturn(false);
        when(passwordEncoder.encode(testCreateDTO.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        var result = userService.create(testCreateDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(testCreateDTO.email());
        assertThat(result.fullName()).isEqualTo(testCreateDTO.fullName());
        verify(userRepository).existsByEmail(testCreateDTO.email());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(testCreateDTO.password());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(testCreateDTO.email())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.create(testCreateDTO))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(testCreateDTO.email());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldFindUserById() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        var result = userService.findById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(testUser.getId());
        assertThat(result.email()).isEqualTo(testUser.getEmail());
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(String.valueOf(userId));
    }

    @Test
    void shouldFindAllUsers() {
        // Given
        var users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        var result = userService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo(testUser.getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void shouldDeleteUser() {
        // Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // When
        userService.delete(userId);

        // Then
        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).deleteById(userId);
    }
}

