package com.chinonso.university_scheduling.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.Schedule;
import com.chinonso.university_scheduling.service.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Integer id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Schedule>> getSchedulesByClass(
            @PathVariable Integer classId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByClass(classId));
    }

    @GetMapping("/day/{day}")
    public ResponseEntity<List<Schedule>> getSchedulesByDay(
            @PathVariable Schedule.DayOfWeek day) {
        return ResponseEntity.ok(scheduleService.getSchedulesByDay(day));
    }

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(
            @RequestBody Schedule schedule,
            @RequestParam Integer classId) {
        Schedule created = scheduleService.createSchedule(schedule, classId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(
            @PathVariable Integer id,
            @RequestBody Schedule schedule,
            @RequestParam Integer classId) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, schedule, classId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}