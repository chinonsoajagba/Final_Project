package com.chinonso.university_scheduling.service;

import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.Course;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.CourseRepository;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // GET ALL
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // GET BY ID
    public Course getCourseById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with ID: " + id));
    }

    // CREATE
    public Course createCourse(Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new IllegalArgumentException(
                    "Course code '" + course.getCourseCode() + "' already exists");
        }
        return courseRepository.save(course);
    }

    // UPDATE
    public Course updateCourse(Integer id, Course updatedCourse) {
        Course existing = getCourseById(id);

        if (!existing.getCourseCode().equals(updatedCourse.getCourseCode())
                && courseRepository.existsByCourseCode(updatedCourse.getCourseCode())) {
            throw new IllegalArgumentException(
                    "Course code '" + updatedCourse.getCourseCode() + "' already exists");
        }

        existing.setCourseCode(updatedCourse.getCourseCode());
        existing.setCourseName(updatedCourse.getCourseName());
        existing.setCredits(updatedCourse.getCredits());
        existing.setDepartment(updatedCourse.getDepartment());
        existing.setLevel(updatedCourse.getLevel());

        return courseRepository.save(existing);
    }

    // DELETE
    public void deleteCourse(Integer id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }

    // GET BY DEPARTMENT
    public List<Course> getCoursesByDepartment(String department) {
        return courseRepository.findByDepartment(department);
    }

    // GET BY LEVEL
    public List<Course> getCoursesByLevel(Integer level) {
        return courseRepository.findByLevel(level);
    }
}
