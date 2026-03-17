package com.chinonso.university_scheduling.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.service.ClassSectionService;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassSectionController {

    private final ClassSectionService classSectionService;

    public ClassSectionController(ClassSectionService classSectionService) {
        this.classSectionService = classSectionService;
    }

    // GET /api/classes
    @GetMapping
    public ResponseEntity<List<ClassSection>> getAllClasses() {
        return ResponseEntity.ok(classSectionService.getAllClasses());
    }

    // GET /api/classes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ClassSection> getClassById(@PathVariable Integer id) {
        return ResponseEntity.ok(classSectionService.getClassById(id));
    }

    // GET /api/classes/course/{courseId}
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ClassSection>> getClassesByCourse(
            @PathVariable Integer courseId) {
        return ResponseEntity.ok(classSectionService.getClassesByCourse(courseId));
    }

    // GET /api/classes/room/{roomId}
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ClassSection>> getClassesByRoom(
            @PathVariable Integer roomId) {
        return ResponseEntity.ok(classSectionService.getClassesByRoom(roomId));
    }

    // POST /api/classes?courseId=1&roomId=2
    // courseId is required, roomId is optional
    @PostMapping
    public ResponseEntity<ClassSection> createClass(
            @RequestBody ClassSection classSection, // removed @Valid
            @RequestParam Integer courseId,
            @RequestParam(required = false) Integer roomId) {
        ClassSection created = classSectionService.createClass(classSection, courseId, roomId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/classes/{id}?courseId=1&roomId=2
    @PutMapping("/{id}")
    public ResponseEntity<ClassSection> updateClass(
            @PathVariable Integer id,
            @RequestBody ClassSection classSection, // removed @Valid
            @RequestParam Integer courseId,
            @RequestParam(required = false) Integer roomId) {
        return ResponseEntity.ok(
                classSectionService.updateClass(id, classSection, courseId, roomId));
    }

    // DELETE /api/classes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Integer id) {
        classSectionService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}