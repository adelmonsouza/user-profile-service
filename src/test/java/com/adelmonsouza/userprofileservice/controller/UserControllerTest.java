package com.adelmonsouza.userprofileservice.controller;

import com.adelmonsouza.userprofileservice.dto.UserCreateDTO;
import com.adelmonsouza.userprofileservice.dto.UserResponseDTO;
import com.adelmonsouza.userprofileservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        UserCreateDTO createDTO = new UserCreateDTO(
                "test@example.com",
                "password123",
                "Test User"
        );

        UserResponseDTO responseDTO = new UserResponseDTO(
                1L,
                "test@example.com",
                "Test User",
                com.adelmonsouza.userprofileservice.model.Role.USER,
                java.time.LocalDateTime.now()
        );

        when(userService.create(any(UserCreateDTO.class))).thenReturn(responseDTO);

        // When/Then
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.fullName").value("Test User"));
    }

    @Test
    @WithMockUser
    void shouldGetAllUsers() throws Exception {
        // Given
        UserResponseDTO user = new UserResponseDTO(
                1L,
                "test@example.com",
                "Test User",
                com.adelmonsouza.userprofileservice.model.Role.USER,
                java.time.LocalDateTime.now()
        );

        when(userService.findAll()).thenReturn(List.of(user));

        // When/Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    void shouldGetUserById() throws Exception {
        // Given
        Long userId = 1L;
        UserResponseDTO user = new UserResponseDTO(
                userId,
                "test@example.com",
                "Test User",
                com.adelmonsouza.userprofileservice.model.Role.USER,
                java.time.LocalDateTime.now()
        );

        when(userService.findById(userId)).thenReturn(user);

        // When/Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}

