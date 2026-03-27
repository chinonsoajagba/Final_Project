package com.chinonso.university_scheduling.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.User;
import com.chinonso.university_scheduling.repository.UserRepository;
import com.chinonso.university_scheduling.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(userService.getAllUsers(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(userService.updateUser(id, body));
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
            
        String requestingEmail = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User requestingUser = userRepository.findByEmail(requestingEmail)
                .orElseThrow(() -> new com.chinonso.university_scheduling.exception.ForbiddenException("User not found"));

        if (requestingUser.getRole() != User.Role.ADMIN && !requestingUser.getUserId().equals(id)) {
            throw new com.chinonso.university_scheduling.exception.ForbiddenException("You can only change your own password");
        }

        userService.resetPassword(id, body.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        String requestingEmail = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Long requestingUserId = userRepository.findByEmail(requestingEmail)
                .map(User::getUserId)
                .orElse(null);
        userService.deleteUser(id, requestingUserId);
        return ResponseEntity.noContent().build();
    }
}
