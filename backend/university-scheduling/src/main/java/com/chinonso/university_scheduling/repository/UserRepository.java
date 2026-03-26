package com.chinonso.university_scheduling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chinonso.university_scheduling.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRole(User.Role role);

    List<User> findByEmailContainingIgnoreCase(String email);

    boolean existsByRoleAndLinkedIdAndUserIdNot(User.Role role, Long linkedId, Long userId);
}