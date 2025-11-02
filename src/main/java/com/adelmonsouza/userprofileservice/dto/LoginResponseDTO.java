package com.adelmonsouza.userprofileservice.dto;

public record LoginResponseDTO(
    String token,
    String type,
    Long userId,
    String email,
    String fullName
) {}

