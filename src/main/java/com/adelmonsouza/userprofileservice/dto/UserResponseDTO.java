package com.adelmonsouza.userprofileservice.dto;

import com.adelmonsouza.userprofileservice.model.Role;
import java.time.LocalDateTime;

public record UserResponseDTO(
    Long id,
    String email,
    String fullName,
    Role role,
    LocalDateTime createdAt
) {}

