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

        @GetMapping("/me")
        public ResponseEntity<Student> getMyInfo(Authentication auth) {
                Long studentId = getLinkedId(auth.getName());
                Student student = studentRepository.findById(studentId.intValue())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Student profile not found."));
                return ResponseEntity.ok(student);
        }

        @GetMapping("/my-enrolments")
        public ResponseEntity<List<Enrolment>> getMyEnrolments(Authentication auth) {
                Long studentId = getLinkedId(auth.getName());
                List<Enrolment> enrolments = enrolmentRepository.findByStudent_StudentId(studentId.intValue());
                return ResponseEntity.ok(enrolments);
        }

        @GetMapping("/my-schedule")
        public ResponseEntity<List<Map<String, Object>>> getMySchedule(
                        Authentication auth) {
                Long studentId = getLinkedId(auth.getName());

                List<Schedule> schedules = scheduleRepository.findSchedulesByStudentId(studentId.intValue());

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

        private Long getLinkedId(String email) {
                Long linkedId = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found: " + email))
                                .getLinkedId();

                if (linkedId == null) {
                        throw new ResourceNotFoundException(
                                        "Your account is not linked to a Student profile. "
                                                        + "Please contact an administrator.");
                }
                return linkedId;
        }
}