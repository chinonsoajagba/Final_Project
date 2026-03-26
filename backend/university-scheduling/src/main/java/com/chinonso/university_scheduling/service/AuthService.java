package com.chinonso.university_scheduling.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.config.JwtUtil;
import com.chinonso.university_scheduling.entity.User;
import com.chinonso.university_scheduling.exception.ForbiddenException;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.exception.UnauthorizedException;
import com.chinonso.university_scheduling.repository.UserRepository;
import com.chinonso.university_scheduling.repository.StudentRepository;
import com.chinonso.university_scheduling.repository.TeacherRepository;
import com.chinonso.university_scheduling.entity.Student;
import com.chinonso.university_scheduling.entity.Teacher;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    // ============================================================
    // REGISTER
    // ============================================================
    public Map<String, Object> register(String email, String rawPassword,
            User.Role role, String firstName, String lastName, String program, Integer yearOfStudy, String department, String employeeId) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email '" + email + "' is already registered.");
        }

        Long linkedId = null;

        if (role == User.Role.STUDENT) {
            Student student = Student.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .program(program)
                .yearOfStudy(yearOfStudy)
                .enrolmentStatus(Student.EnrolmentStatus.ACTIVE)
                .build();
            Student savedStudent = studentRepository.save(student);
            linkedId = savedStudent.getStudentId().longValue();
        } else if (role == User.Role.TEACHER) {
            Teacher teacher = Teacher.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .department(department)
                .employeeId(employeeId)
                .build();
            Teacher savedTeacher = teacherRepository.save(teacher);
            linkedId = savedTeacher.getTeacherId().longValue();
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .linkedId(linkedId)
                .isActive(true)
                .build();
        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(email, role.name());

        return Map.of(
                "message", "Registration successful",
                "token", token,
                "email", email,
                "role", role.name(),
                "userId", saved.getUserId());
    }

    // ============================================================
    // LOGIN
    // ============================================================
    public Map<String, Object> login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No account found with email: " + email));

        if (!user.getIsActive()) {
            throw new ForbiddenException("This account has been deactivated.");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UnauthorizedException("Incorrect password. Please try again.");
        }

        String token = jwtUtil.generateToken(email, user.getRole().name());

        return Map.of(
                "message", "Login successful",
                "token", token,
                "email", email,
                "role", user.getRole().name(),
                "userId", user.getUserId(),
                "linkedId", user.getLinkedId() != null ? user.getLinkedId() : 0);
    }

}