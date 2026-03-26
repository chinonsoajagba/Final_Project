package com.chinonso.university_scheduling.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.config.JwtUtil;
import com.chinonso.university_scheduling.entity.User;
import com.chinonso.university_scheduling.exception.ForbiddenException;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.exception.UnauthorizedException;
import com.chinonso.university_scheduling.repository.UserRepository;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ============================================================
    // REGISTER
    // ============================================================
    public Map<String, Object> register(String email, String rawPassword,
            User.Role role, Long linkedId) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email '" + email + "' is already registered.");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(User.Role.STUDENT)
                .linkedId(linkedId)
                .isActive(true)
                .build();
        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(email, role.name());

        return Map.of(
                "message", "Registration successful",
                "token", token,
                "email", email,
                "role", role.name(),
                "userId", saved.getUserId());
    }

    // ============================================================
    // LOGIN
    // ============================================================
    public Map<String, Object> login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No account found with email: " + email));

        if (!user.getIsActive()) {
            throw new ForbiddenException("This account has been deactivated.");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UnauthorizedException("Incorrect password. Please try again.");
        }

        String token = jwtUtil.generateToken(email, user.getRole().name());

        return Map.of(
                "message", "Login successful",
                "token", token,
                "email", email,
                "role", user.getRole().name(),
                "userId", user.getUserId(),
                "linkedId", user.getLinkedId() != null ? user.getLinkedId() : 0);
    }

}