package com.chinonso.university_scheduling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chinonso.university_scheduling.entity.Teacher;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    // Check for duplicate employee ID or email
    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    // Find teacher by email
    Optional<Teacher> findByEmail(String email);

    // Get all teachers in a department
    List<Teacher> findByDepartment(String department);
}
