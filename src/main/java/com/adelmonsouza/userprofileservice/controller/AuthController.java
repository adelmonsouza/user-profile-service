package com.adelmonsouza.userprofileservice.controller;

import com.adelmonsouza.userprofileservice.dto.LoginRequestDTO;
import com.adelmonsouza.userprofileservice.dto.LoginResponseDTO;
import com.adelmonsouza.userprofileservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}

