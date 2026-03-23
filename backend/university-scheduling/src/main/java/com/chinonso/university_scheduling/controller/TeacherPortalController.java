package com.chinonso.university_scheduling.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.ClassTeacher;
import com.chinonso.university_scheduling.entity.Enrolment;
import com.chinonso.university_scheduling.entity.Teacher;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.ClassTeacherRepository;
import com.chinonso.university_scheduling.repository.EnrolmentRepository;
import com.chinonso.university_scheduling.repository.TeacherRepository;
import com.chinonso.university_scheduling.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher-portal")
public class TeacherPortalController {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final ClassTeacherRepository classTeacherRepository;
    private final EnrolmentRepository enrolmentRepository;

    public TeacherPortalController(UserRepository userRepository,
            TeacherRepository teacherRepository,
            ClassTeacherRepository classTeacherRepository,
            EnrolmentRepository enrolmentRepository) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.classTeacherRepository = classTeacherRepository;
        this.enrolmentRepository = enrolmentRepository;
    }

    // ============================================================
    // GET /api/teacher-portal/me
    // Returns the logged-in teacher's personal info
    // ============================================================
    @GetMapping("/me")
    public ResponseEntity<Teacher> getMyInfo(Authentication auth) {
        Integer teacherId = Math.toIntExact(getLinkedId(auth.getName()));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher profile not found."));
        return ResponseEntity.ok(teacher);
    }

    // ============================================================
    // GET /api/teacher-portal/my-classes
    // Returns all classes this teacher is assigned to
    // ============================================================
    @GetMapping("/my-classes")
    public ResponseEntity<List<Map<String, Object>>> getMyClasses(
            Authentication auth) {
        Integer teacherId = Math.toIntExact(getLinkedId(auth.getName()));

        List<ClassTeacher> assignments = classTeacherRepository.findByTeacher_TeacherId(teacherId);

        List<Map<String, Object>> classes = assignments.stream()
                .map(a -> {
                    ClassSection cs = a.getClassSection();
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("classId", cs.getClassId());
                    map.put("courseCode", cs.getCourse().getCourseCode());
                    map.put("courseName", cs.getCourse().getCourseName());
                    map.put("section", cs.getSectionNumber());
                    map.put("semester", cs.getSemester().name());
                    map.put("academicYear", cs.getAcademicYear());
                    map.put("room", cs.getRoom() != null
                            ? cs.getRoom().getRoomCode()
                            : "TBA");
                    map.put("enrolmentCount", cs.getCurrentEnrolment());
                    map.put("maxEnrolment", cs.getMaxEnrolment());
                    map.put("role", a.getRole().name());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(classes);
    }

    // ============================================================
    // GET /api/teacher-portal/my-classes/{classId}/students
    // Returns all students enrolled in a specific class
    // ============================================================
    @GetMapping("/my-classes/{classId}/students")
    public ResponseEntity<List<Map<String, Object>>> getStudentsInClass(
            @PathVariable Long classId,
            Authentication auth) {

        // Verify this teacher is actually assigned to this class
        Integer teacherId = Math.toIntExact(getLinkedId(auth.getName()));
        boolean isAssigned = classTeacherRepository
                .existsByClassSection_ClassIdAndTeacher_TeacherId(
                        Math.toIntExact(classId), teacherId);

        if (!isAssigned) {
            throw new ResourceNotFoundException(
                    "You are not assigned to this class.");
        }

        List<Enrolment> enrolments = enrolmentRepository.findByClassSection_ClassId(Math.toIntExact(classId));

        List<Map<String, Object>> students = enrolments.stream()
                .filter(e -> e.getStatus() == Enrolment.EnrolmentStatus.ENROLLED)
                .map(e -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("studentId", e.getStudent().getStudentId());
                    map.put("firstName", e.getStudent().getFirstName());
                    map.put("lastName", e.getStudent().getLastName());
                    map.put("email", e.getStudent().getEmail());
                    map.put("year", e.getStudent().getYearOfStudy());
                    map.put("program", e.getStudent().getProgram());
                    map.put("enrolledOn", e.getEnrolmentDate().toString());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(students);
    }

    // ============================================================
    // HELPER
    // ============================================================
    private Long getLinkedId(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + email))
                .getLinkedId();
    }
}