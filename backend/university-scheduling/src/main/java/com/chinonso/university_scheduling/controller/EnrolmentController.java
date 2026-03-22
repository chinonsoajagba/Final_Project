package com.chinonso.university_scheduling.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.Enrolment;
import com.chinonso.university_scheduling.service.EnrolmentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrolments")
public class EnrolmentController {

    private final EnrolmentService enrolmentService;

    public EnrolmentController(EnrolmentService enrolmentService) {
        this.enrolmentService = enrolmentService;
    }

    @GetMapping
    public ResponseEntity<List<Enrolment>> getAllEnrolments() {
        return ResponseEntity.ok(enrolmentService.getAllEnrolments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enrolment> getEnrolmentById(@PathVariable Integer id) {
        return ResponseEntity.ok(enrolmentService.getEnrolmentById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrolment>> getByStudent(@PathVariable Integer studentId) {
        return ResponseEntity.ok(enrolmentService.getEnrolmentsByStudent(studentId));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Enrolment>> getByClass(@PathVariable Integer classId) {
        return ResponseEntity.ok(enrolmentService.getEnrolmentsByClass(classId));
    }

    @PostMapping("/enrol")
    public ResponseEntity<Map<String, Object>> enrolStudent(
            @RequestBody Map<String, Integer> body) {
        Integer studentId = body.get("studentId");
        Integer classId = body.get("classId");

        Enrolment enrolment = enrolmentService.enrolStudent(studentId, classId);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Student enrolled successfully",
                "enrolmentId", enrolment.getEnrolmentId(),
                "student", enrolment.getStudent().getFirstName() + " "
                        + enrolment.getStudent().getLastName(),
                "class", enrolment.getClassSection().getCourse().getCourseCode()
                        + " Section " + enrolment.getClassSection().getSectionNumber(),
                "date", enrolment.getEnrolmentDate().toString(),
                "status", enrolment.getStatus().toString()));
    }

    @PostMapping("/drop")
    public ResponseEntity<Map<String, Object>> dropStudent(
            @RequestBody Map<String, Integer> body) {
        Integer studentId = body.get("studentId");
        Integer classId = body.get("classId");

        Enrolment enrolment = enrolmentService.dropStudent(studentId, classId);

        return ResponseEntity.ok(Map.of(
                "message", "Student dropped successfully",
                "status", enrolment.getStatus().toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrolment(@PathVariable Integer id) {
        enrolmentService.deleteEnrolment(id);
        return ResponseEntity.noContent().build();
    }
}