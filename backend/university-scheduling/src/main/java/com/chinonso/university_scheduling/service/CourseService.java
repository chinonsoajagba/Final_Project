package com.chinonso.university_scheduling.service;

import com.chinonso.university_scheduling.entity.Course;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // ── READ ───────────────────────────────────────────────────────────────────
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    public Course findByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with code: " + courseCode));
    }

    public List<Course> findByDepartment(String department) {
        return courseRepository.findByDepartment(department);
    }

    public List<Course> findByLevel(Integer level) {
        return courseRepository.findByLevel(level);
    }

    public List<Course> findByDepartmentAndLevel(String department, Integer level) {
        return courseRepository.findByDepartmentAndLevel(department, level);
    }

    // ── CREATE ─────────────────────────────────────────────────────────────────
    public Course create(Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new IllegalArgumentException("Course code already exists: " + course.getCourseCode());
        }
        return courseRepository.save(course);
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────────
    public Course update(Integer id, Course updated) {
        Course existing = findById(id);

        if (!existing.getCourseCode().equals(updated.getCourseCode())
                && courseRepository.existsByCourseCode(updated.getCourseCode())) {
            throw new IllegalArgumentException("Course code already exists: " + updated.getCourseCode());
        }

        existing.setCourseCode(updated.getCourseCode());
        existing.setCourseName(updated.getCourseName());
        existing.setCredits(updated.getCredits());
        existing.setDepartment(updated.getDepartment());
        existing.setLevel(updated.getLevel());

        return courseRepository.save(existing);
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────
    public void delete(Integer id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }
}
