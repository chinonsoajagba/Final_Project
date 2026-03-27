package com.chinonso.university_scheduling.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.Teacher;
import com.chinonso.university_scheduling.service.TeacherService;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Integer id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @GetMapping("/department/{dept}")
    public ResponseEntity<List<Teacher>> getByDepartment(@PathVariable String dept) {
        return ResponseEntity.ok(teacherService.getTeachersByDepartment(dept));
    }

    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@RequestBody java.util.Map<String, Object> body) {
        Teacher teacher = new Teacher();
        teacher.setFirstName((String) body.get("firstName"));
        teacher.setLastName((String) body.get("lastName"));
        teacher.setEmployeeId((String) body.get("employeeId"));
        teacher.setEmail((String) body.get("email"));
        teacher.setDepartment((String) body.get("department"));
        teacher.setSpecialization((String) body.getOrDefault("specialization", null));
        Object maxH = body.get("maxHoursPerWeek");
        teacher.setMaxHoursPerWeek(maxH != null ? Integer.parseInt(maxH.toString()) : 20);

        String password = (String) body.getOrDefault("password", null);
        Teacher created = teacherService.createTeacher(teacher, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(
            @PathVariable Integer id,
            @Valid @RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacher));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Integer id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}