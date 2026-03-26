package com.chinonso.university_scheduling.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.User;
import com.chinonso.university_scheduling.exception.ForbiddenException;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── LIST ──────────────────────────────────────────────────────────────────
    public List<User> getAllUsers(String roleFilter) {
        if (roleFilter != null && !roleFilter.isBlank()) {
            User.Role role = User.Role.valueOf(roleFilter.toUpperCase());
            return userRepository.findAllByRole(role);
        }
        return userRepository.findAll();
    }

    // ── GET ONE ───────────────────────────────────────────────────────────────
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public User updateUser(Long id, Map<String, Object> body) {
        User user = getUserById(id);

        if (body.containsKey("email")) {
            String newEmail = body.get("email").toString().trim();
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email '" + newEmail + "' is already in use.");
            }
            user.setEmail(newEmail);
        }

        if (body.containsKey("role")) {
            user.setRole(User.Role.valueOf(body.get("role").toString().toUpperCase()));
        }

        if (body.containsKey("isActive")) {
            Object val = body.get("isActive");
            if (val instanceof Boolean) {
                user.setIsActive((Boolean) val);
            } else {
                user.setIsActive(Boolean.parseBoolean(val.toString()));
            }
        }

        if (body.containsKey("linkedId")) {
            Object linkedIdObj = body.get("linkedId");
            if (linkedIdObj == null || linkedIdObj.toString().trim().isEmpty()) {
                user.setLinkedId(null);
            } else {
                Long newLinkedId = Long.valueOf(linkedIdObj.toString());
                if (userRepository.existsByRoleAndLinkedIdAndUserIdNot(user.getRole(), newLinkedId, id)) {
                    throw new IllegalArgumentException(
                            "Linked ID " + newLinkedId + " is already assigned to another " + user.getRole()
                                    + " user.");
                }
                user.setLinkedId(newLinkedId);
            }
        }

        return userRepository.save(user);
    }

    // ── RESET PASSWORD ────────────────────────────────────────────────────────
    public void resetPassword(Long id, String newPassword) {
        User user = getUserById(id);
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public void deleteUser(Long id, Long requestingUserId) {
        if (id.equals(requestingUserId)) {
            throw new ForbiddenException("You cannot delete your own account.");
        }
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
