package com.chinonso.university_scheduling.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.Teacher;
import com.chinonso.university_scheduling.entity.User;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.TeacherRepository;
import com.chinonso.university_scheduling.repository.UserRepository;

import java.util.List;

@Service
public class TeacherService {

    private static final Logger log = LoggerFactory.getLogger(TeacherService.class);

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(TeacherRepository teacherRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher getTeacherById(Integer id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher not found with ID: " + id));
    }

    public Teacher createTeacher(Teacher teacher) {
        return createTeacher(teacher, "password123");
    }

    public Teacher createTeacher(Teacher teacher, String rawPassword) {
        if (teacherRepository.existsByEmployeeId(teacher.getEmployeeId())) {
            throw new IllegalArgumentException(
                    "Employee ID '" + teacher.getEmployeeId() + "' already exists");
        }
        if (teacherRepository.existsByEmail(teacher.getEmail())) {
            throw new IllegalArgumentException(
                    "Email '" + teacher.getEmail() + "' is already registered");
        }

        Teacher saved = teacherRepository.save(teacher);

        if (userRepository.findByEmail(saved.getEmail()).isEmpty()) {
            User u = new User();
            u.setEmail(saved.getEmail());
            u.setPassword(passwordEncoder.encode(
                    (rawPassword != null && !rawPassword.isBlank()) ? rawPassword : "password123"));
            u.setRole(User.Role.TEACHER);
            u.setLinkedId(saved.getTeacherId().longValue());
            u.setIsActive(true);
            userRepository.save(u);
            log.info("Login account created for new teacher: {}", saved.getEmail());
        }

        return saved;
    }

    public Teacher updateTeacher(Integer id, Teacher updatedTeacher) {
        Teacher existing = getTeacherById(id);

        if (!existing.getEmployeeId().equals(updatedTeacher.getEmployeeId())
                && teacherRepository.existsByEmployeeId(updatedTeacher.getEmployeeId())) {
            throw new IllegalArgumentException(
                    "Employee ID '" + updatedTeacher.getEmployeeId() + "' already exists");
        }
        if (!existing.getEmail().equals(updatedTeacher.getEmail())
                && teacherRepository.existsByEmail(updatedTeacher.getEmail())) {
            throw new IllegalArgumentException(
                    "Email '" + updatedTeacher.getEmail() + "' is already registered");
        }

        existing.setEmployeeId(updatedTeacher.getEmployeeId());
        existing.setFirstName(updatedTeacher.getFirstName());
        existing.setLastName(updatedTeacher.getLastName());
        existing.setEmail(updatedTeacher.getEmail());
        existing.setDepartment(updatedTeacher.getDepartment());
        existing.setSpecialization(updatedTeacher.getSpecialization());
        existing.setMaxHoursPerWeek(updatedTeacher.getMaxHoursPerWeek());

        return teacherRepository.save(existing);
    }

    public void deleteTeacher(Integer id) {
        Teacher teacher = getTeacherById(id);
        userRepository.findByEmail(teacher.getEmail()).ifPresent(userRepository::delete);
        teacherRepository.delete(teacher);
    }

    public List<Teacher> getTeachersByDepartment(String department) {
        return teacherRepository.findByDepartment(department);
    }
}
