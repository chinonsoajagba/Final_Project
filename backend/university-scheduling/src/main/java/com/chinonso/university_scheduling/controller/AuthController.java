package com.chinonso.university_scheduling.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.User;
import com.chinonso.university_scheduling.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register
    // Body: { "email": "x", "password": "x", "role": "ADMIN", "linkedId": null }
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody Map<String, Object> body) {

        String email = body.get("email").toString();
        String password = body.get("password").toString();
        User.Role role = User.Role.valueOf(body.get("role").toString());
        Long linkedId = body.get("linkedId") != null
                && !body.get("linkedId").toString().equals("null")
                        ? Long.valueOf(body.get("linkedId").toString())
                        : null;

        return ResponseEntity.ok(
                authService.register(email, password, role, linkedId));
    }

    // POST /api/auth/login
    // Body: { "email": "x", "password": "x" }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        return ResponseEntity.ok(authService.login(email, password));
    }
}