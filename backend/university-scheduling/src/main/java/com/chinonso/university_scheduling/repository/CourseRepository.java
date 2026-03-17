package com.chinonso.university_scheduling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chinonso.university_scheduling.entity.Course;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    // Check for duplicate course code
    boolean existsByCourseCode(String courseCode);

    // Find by course code e.g. "CST3350"
    Optional<Course> findByCourseCode(String courseCode);

    // Get all courses for a department
    List<Course> findByDepartment(String department);

    // Get all courses for a specific year level
    List<Course> findByLevel(Integer level);
}
