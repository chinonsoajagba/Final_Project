package com.chinonso.university_scheduling.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.Student;
import com.chinonso.university_scheduling.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Student>> getActiveStudents() {
        return ResponseEntity.ok(studentService.getActiveStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Integer id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<Student>> getByYear(@PathVariable Integer year) {
        return ResponseEntity.ok(studentService.getStudentsByYear(year));
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody java.util.Map<String, Object> body) {
        Student student = new Student();
        student.setFirstName((String) body.get("firstName"));
        student.setLastName((String) body.get("lastName"));
        student.setEmail((String) body.get("email"));
        student.setProgram((String) body.get("program"));
        Object year = body.get("yearOfStudy");
        student.setYearOfStudy(year != null ? Integer.parseInt(year.toString()) : 1);
        String status = (String) body.getOrDefault("enrolmentStatus", "ACTIVE");
        student.setEnrolmentStatus(com.chinonso.university_scheduling.entity.Student.EnrolmentStatus.valueOf(status));

        String password = (String) body.getOrDefault("password", null);
        Student created = studentService.createStudent(student, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Integer id,
            @Valid @RequestBody Student student) {
        return ResponseEntity.ok(studentService.updateStudent(id, student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}