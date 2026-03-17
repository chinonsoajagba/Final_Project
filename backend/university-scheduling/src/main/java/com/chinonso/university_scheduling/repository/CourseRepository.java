package com.chinonso.university_scheduling.repository;

import com.chinonso.university_scheduling.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByDepartment(String department);

    List<Course> findByLevel(Integer level);

    List<Course> findByDepartmentAndLevel(String department, Integer level);

    boolean existsByCourseCode(String courseCode);
}
