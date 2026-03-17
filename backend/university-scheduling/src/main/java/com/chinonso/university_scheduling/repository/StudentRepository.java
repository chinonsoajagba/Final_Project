package com.chinonso.university_scheduling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chinonso.university_scheduling.entity.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    // Check for duplicate email
    boolean existsByEmail(String email);

    // Find student by email
    Optional<Student> findByEmail(String email);

    // Get all students in a specific year
    List<Student> findByYearOfStudy(Integer yearOfStudy);

    // Get all active students (for dropdowns)
    List<Student> findByEnrolmentStatus(Student.EnrolmentStatus status);
}
