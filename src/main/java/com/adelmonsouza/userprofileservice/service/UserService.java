package com.adelmonsouza.userprofileservice.service;

import com.adelmonsouza.userprofileservice.dto.LoginRequestDTO;
import com.adelmonsouza.userprofileservice.dto.LoginResponseDTO;
import com.adelmonsouza.userprofileservice.dto.UserCreateDTO;
import com.adelmonsouza.userprofileservice.dto.UserResponseDTO;
import com.adelmonsouza.userprofileservice.exception.EmailAlreadyExistsException;
import com.adelmonsouza.userprofileservice.exception.InvalidCredentialsException;
import com.adelmonsouza.userprofileservice.exception.UserNotFoundException;
import com.adelmonsouza.userprofileservice.model.User;
import com.adelmonsouza.userprofileservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO create(UserCreateDTO dto) {
        // Validação de negócio: email único
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        // Criar entidade
        User user = User.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .fullName(dto.fullName())
                .role(com.adelmonsouza.userprofileservice.model.Role.USER)
                .build();

        User saved = userRepository.save(user);
        return toResponseDTO(saved);
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user);

        return new LoginResponseDTO(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFullName()
        );
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}

