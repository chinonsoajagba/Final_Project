package com.chinonso.university_scheduling.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.Enrolment;
import com.chinonso.university_scheduling.service.EnrolmentService;

import java.util.Map;

// ================================================================
// TEMPORARY CONTROLLER FOR TESTING ONLY
// This will be replaced by the full EnrolmentController later
// ================================================================
@RestController
@RequestMapping("/api/test")
public class EnrolmentTestController {

    private final EnrolmentService enrolmentService;

    public EnrolmentTestController(EnrolmentService enrolmentService) {
        this.enrolmentService = enrolmentService;
    }

    // POST /api/test/enrol
    // Body: { "studentId": 1, "classId": 2 }
    @PostMapping("/enrol")
    public ResponseEntity<?> testEnrol(@RequestBody Map<String, Integer> body) {
        Integer studentId = body.get("studentId");
        Integer classId = body.get("classId");
        Enrolment result = enrolmentService.enrolStudent(studentId, classId);
        return ResponseEntity.ok(Map.of(
                "message", "Enrolment successful",
                "enrolmentId", result.getEnrolmentId(),
                "student", result.getStudent().getFirstName() + " " + result.getStudent().getLastName(),
                "class", result.getClassSection().getCourse().getCourseCode() +
                        " Section " + result.getClassSection().getSectionNumber()));
    }

    // POST /api/test/drop
    // Body: { "studentId": 1, "classId": 2 }
    @PostMapping("/drop")
    public ResponseEntity<?> testDrop(@RequestBody Map<String, Integer> body) {
        Integer studentId = body.get("studentId");
        Integer classId = body.get("classId");
        Enrolment result = enrolmentService.dropStudent(studentId, classId);
        return ResponseEntity.ok(Map.of(
                "message", "Student dropped successfully",
                "status", result.getStatus().toString()));
    }
}
