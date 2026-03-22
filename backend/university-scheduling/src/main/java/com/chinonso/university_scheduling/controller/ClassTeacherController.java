package com.chinonso.university_scheduling.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.ClassTeacher;
import com.chinonso.university_scheduling.service.ClassTeacherService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/class-teachers")
public class ClassTeacherController {

    private final ClassTeacherService classTeacherService;

    public ClassTeacherController(ClassTeacherService classTeacherService) {
        this.classTeacherService = classTeacherService;
    }

    @GetMapping
    public ResponseEntity<List<ClassTeacher>> getAllAssignments() {
        return ResponseEntity.ok(classTeacherService.getAllAssignments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassTeacher> getAssignmentById(@PathVariable Integer id) {
        return ResponseEntity.ok(classTeacherService.getAssignmentById(id));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ClassTeacher>> getByClass(@PathVariable Integer classId) {
        return ResponseEntity.ok(classTeacherService.getAssignmentsByClass(classId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ClassTeacher>> getByTeacher(@PathVariable Integer teacherId) {
        return ResponseEntity.ok(classTeacherService.getAssignmentsByTeacher(teacherId));
    }

    @PostMapping("/assign")
    public ResponseEntity<ClassTeacher> assignTeacher(
            @RequestBody Map<String, Object> body) {
        Integer classId = Integer.valueOf(body.get("classId").toString());
        Integer teacherId = Integer.valueOf(body.get("teacherId").toString());
        ClassTeacher.TeacherRole role = ClassTeacher.TeacherRole
                .valueOf(body.get("role").toString());

        ClassTeacher assignment = classTeacherService.assignTeacher(classId, teacherId, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ClassTeacher> updateRole(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        ClassTeacher.TeacherRole newRole = ClassTeacher.TeacherRole
                .valueOf(body.get("role"));
        return ResponseEntity.ok(classTeacherService.updateRole(id, newRole));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAssignment(@PathVariable Integer id) {
        classTeacherService.removeAssignment(id);
        return ResponseEntity.noContent().build();
    }
}