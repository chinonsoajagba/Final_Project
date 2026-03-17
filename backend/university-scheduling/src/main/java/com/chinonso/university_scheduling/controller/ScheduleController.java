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

    // GET /api/schedules
    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    // GET /api/schedules/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Integer id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    // GET /api/schedules/class/{classId}
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Schedule>> getSchedulesByClass(
            @PathVariable Integer classId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByClass(classId));
    }

    // GET /api/schedules/day/{day} e.g. MON, TUE, WED
    @GetMapping("/day/{day}")
    public ResponseEntity<List<Schedule>> getSchedulesByDay(
            @PathVariable Schedule.DayOfWeek day) {
        return ResponseEntity.ok(scheduleService.getSchedulesByDay(day));
    }

    // POST /api/schedules?classId=1
    @PostMapping
    public ResponseEntity<Schedule> createSchedule(
            @RequestBody Schedule schedule, // removed @Valid
            @RequestParam Integer classId) {
        Schedule created = scheduleService.createSchedule(schedule, classId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/schedules/{id}?classId=1
    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(
            @PathVariable Integer id,
            @RequestBody Schedule schedule, // removed @Valid
            @RequestParam Integer classId) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, schedule, classId));
    }

    // DELETE /api/schedules/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}