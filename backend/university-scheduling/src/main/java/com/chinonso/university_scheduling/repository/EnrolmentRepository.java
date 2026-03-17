package com.chinonso.university_scheduling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chinonso.university_scheduling.entity.Enrolment;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrolmentRepository extends JpaRepository<Enrolment, Integer> {

    // Get all enrolments for a student
    List<Enrolment> findByStudent_StudentId(Integer studentId);

    // Get all enrolments for a class
    List<Enrolment> findByClassSection_ClassId(Integer classId);

    // Check if student is already enrolled in a specific class
    boolean existsByStudent_StudentIdAndClassSection_ClassId(Integer studentId, Integer classId);

    // Find specific enrolment record (for drop/update)
    Optional<Enrolment> findByStudent_StudentIdAndClassSection_ClassId(Integer studentId, Integer classId);

    // Count active enrolments for a class (used to verify current_enrolment)
    int countByClassSection_ClassIdAndStatus(Integer classId, Enrolment.EnrolmentStatus status);
}
