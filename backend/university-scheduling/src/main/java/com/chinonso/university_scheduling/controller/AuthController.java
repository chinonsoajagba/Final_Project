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

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody Map<String, Object> body) {

        String email = body.get("email").toString();
        String password = body.get("password").toString();
        User.Role role = User.Role.valueOf(body.get("role").toString());
        String firstName = body.get("firstName") != null ? body.get("firstName").toString() : null;
        String lastName = body.get("lastName") != null ? body.get("lastName").toString() : null;
        String program = body.get("program") != null ? body.get("program").toString() : null;
        Integer yearOfStudy = body.get("yearOfStudy") != null && !body.get("yearOfStudy").toString().isEmpty() ? Integer.valueOf(body.get("yearOfStudy").toString()) : null;
        String department = body.get("department") != null ? body.get("department").toString() : null;
        String employeeId = body.get("employeeId") != null ? body.get("employeeId").toString() : null;

        return ResponseEntity.ok(
                authService.register(email, password, role, firstName, lastName, program, yearOfStudy, department, employeeId));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        return ResponseEntity.ok(authService.login(email, password));
    }
}