package com.chinonso.university_scheduling.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.Enrolment;
import com.chinonso.university_scheduling.entity.Schedule;
import com.chinonso.university_scheduling.entity.Student;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.EnrolmentRepository;
import com.chinonso.university_scheduling.repository.ScheduleRepository;
import com.chinonso.university_scheduling.repository.StudentRepository;
import com.chinonso.university_scheduling.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student-portal")
public class StudentPortalController {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final EnrolmentRepository enrolmentRepository;
    private final ScheduleRepository scheduleRepository;

    public StudentPortalController(UserRepository userRepository,
            StudentRepository studentRepository,
            EnrolmentRepository enrolmentRepository,
            ScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.enrolmentRepository = enrolmentRepository;
        this.scheduleRepository = scheduleRepository;
    }

    // ============================================================
    // GET /api/student-portal/me
    // Returns the logged-in student's personal info
    // ============================================================
    @GetMapping("/me")
    public ResponseEntity<Student> getMyInfo(Authentication auth) {
        Long studentId = getLinkedId(auth.getName());
        Student student = studentRepository.findById(studentId.intValue())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student profile not found."));
        return ResponseEntity.ok(student);
    }

    // ============================================================
    // GET /api/student-portal/my-enrolments
    // Returns all classes the student is enrolled in
    // ============================================================
    @GetMapping("/my-enrolments")
    public ResponseEntity<List<Enrolment>> getMyEnrolments(Authentication auth) {
        Long studentId = getLinkedId(auth.getName());
        List<Enrolment> enrolments = enrolmentRepository.findByStudent_StudentId(studentId.intValue());
        return ResponseEntity.ok(enrolments);
    }

    // ============================================================
    // GET /api/student-portal/my-schedule
    // Returns the student's full weekly timetable
    // ============================================================
    @GetMapping("/my-schedule")
    public ResponseEntity<List<Map<String, Object>>> getMySchedule(
            Authentication auth) {
        Long studentId = getLinkedId(auth.getName());

        // Get all schedules for classes this student is enrolled in
        List<Schedule> schedules = scheduleRepository.findSchedulesByStudentId(studentId.intValue());

        // Map to a clean response showing day, time, course, room
        List<Map<String, Object>> timetable = schedules.stream()
                .map(s -> Map.<String, Object>of(
                        "day", s.getDayOfWeek().name(),
                        "startTime", s.getStartTime().toString(),
                        "endTime", s.getEndTime().toString(),
                        "courseCode", s.getClassSection().getCourse().getCourseCode(),
                        "courseName", s.getClassSection().getCourse().getCourseName(),
                        "section", s.getClassSection().getSectionNumber(),
                        "room", s.getClassSection().getRoom() != null
                                ? s.getClassSection().getRoom().getRoomCode()
                                : "TBA",
                        "frequency", s.getFrequency().name()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(timetable);
    }

    // ============================================================
    // HELPER — get linkedId from email via users table
    // ============================================================
    private Long getLinkedId(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + email))
                .getLinkedId();
    }
}