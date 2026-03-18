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

    @GetMapping
    public ResponseEntity<List<ClassSection>> getAllClasses() {
        return ResponseEntity.ok(classSectionService.getAllClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassSection> getClassById(@PathVariable Integer id) {
        return ResponseEntity.ok(classSectionService.getClassById(id));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ClassSection>> getClassesByCourse(
            @PathVariable Integer courseId) {
        return ResponseEntity.ok(classSectionService.getClassesByCourse(courseId));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ClassSection>> getClassesByRoom(
            @PathVariable Integer roomId) {
        return ResponseEntity.ok(classSectionService.getClassesByRoom(roomId));
    }

    @PostMapping
    public ResponseEntity<ClassSection> createClass(
            @RequestBody ClassSection classSection,
            @RequestParam Integer courseId,
            @RequestParam(required = false) Integer roomId) {
        ClassSection created = classSectionService.createClass(classSection, courseId, roomId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassSection> updateClass(
            @PathVariable Integer id,
            @RequestBody ClassSection classSection,
            @RequestParam Integer courseId,
            @RequestParam(required = false) Integer roomId) {
        return ResponseEntity.ok(
                classSectionService.updateClass(id, classSection, courseId, roomId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Integer id) {
        classSectionService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}