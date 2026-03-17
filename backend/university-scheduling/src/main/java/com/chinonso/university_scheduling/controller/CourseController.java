package com.chinonso.university_scheduling.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.Course;
import com.chinonso.university_scheduling.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // GET /api/courses
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // GET /api/courses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // GET /api/courses/department/{dept}
    @GetMapping("/department/{dept}")
    public ResponseEntity<List<Course>> getByDepartment(@PathVariable String dept) {
        return ResponseEntity.ok(courseService.getCoursesByDepartment(dept));
    }

    // GET /api/courses/level/{level}
    @GetMapping("/level/{level}")
    public ResponseEntity<List<Course>> getByLevel(@PathVariable Integer level) {
        return ResponseEntity.ok(courseService.getCoursesByLevel(level));
    }

    // POST /api/courses
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        Course created = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/courses/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Integer id,
            @Valid @RequestBody Course course) {
        return ResponseEntity.ok(courseService.updateCourse(id, course));
    }

    // DELETE /api/courses/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}