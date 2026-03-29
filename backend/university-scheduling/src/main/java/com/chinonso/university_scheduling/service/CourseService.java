package com.chinonso.university_scheduling.service;

import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.Course;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.ClassSectionRepository;
import com.chinonso.university_scheduling.repository.CourseRepository;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ClassSectionRepository classSectionRepository;
    private final ClassSectionService classSectionService;

    public CourseService(CourseRepository courseRepository,
                         ClassSectionRepository classSectionRepository,
                         ClassSectionService classSectionService) {
        this.courseRepository = courseRepository;
        this.classSectionRepository = classSectionRepository;
        this.classSectionService = classSectionService;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with ID: " + id));
    }

    public Course createCourse(Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new IllegalArgumentException(
                    "Course code '" + course.getCourseCode() + "' already exists");
        }
        return courseRepository.save(course);
    }

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

    public void deleteCourse(Integer id) {
        Course course = getCourseById(id);
        // Cascade-delete all class sections (and their dependents) first
        classSectionRepository.findByCourse_CourseId(id)
                .forEach(cs -> classSectionService.deleteClass(cs.getClassId()));
        courseRepository.delete(course);
    }

    public List<Course> getCoursesByDepartment(String department) {
        return courseRepository.findByDepartment(department);
    }

    public List<Course> getCoursesByLevel(Integer level) {
        return courseRepository.findByLevel(level);
    }
}
