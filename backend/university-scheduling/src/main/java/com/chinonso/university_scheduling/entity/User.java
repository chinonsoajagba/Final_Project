package com.chinonso.university_scheduling.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // Links to student_id or teacher_id depending on role
    @Column(name = "linked_id")
    private Long linkedId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public enum Role {
        ADMIN, ENROLLMENT_OFFICER, CLASS_HANDLER, STUDENT, TEACHER
    }

    public User() {
    }

    public User(String email, String password, Role role, Long linkedId) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.linkedId = linkedId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(Long id) {
        this.linkedId = id;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean a) {
        this.isActive = a;
    }
}
