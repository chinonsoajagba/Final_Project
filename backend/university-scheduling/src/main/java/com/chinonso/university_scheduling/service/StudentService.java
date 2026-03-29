package com.chinonso.university_scheduling.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.Student;
import com.chinonso.university_scheduling.entity.User;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.EnrolmentRepository;
import com.chinonso.university_scheduling.repository.StudentRepository;
import com.chinonso.university_scheduling.repository.UserRepository;

import java.util.List;

@Service
public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnrolmentRepository enrolmentRepository;

    public StudentService(StudentRepository studentRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          EnrolmentRepository enrolmentRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.enrolmentRepository = enrolmentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getActiveStudents() {
        return studentRepository.findByEnrolmentStatus(Student.EnrolmentStatus.ACTIVE);
    }

    public Student getStudentById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + id));
    }

    public Student createStudent(Student student) {
        return createStudent(student, "password123");
    }

    public Student createStudent(Student student, String rawPassword) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException(
                    "Email '" + student.getEmail() + "' is already registered");
        }

        Student saved = studentRepository.save(student);

        if (userRepository.findByEmail(saved.getEmail()).isEmpty()) {
            User u = new User();
            u.setEmail(saved.getEmail());
            u.setPassword(passwordEncoder.encode(
                    (rawPassword != null && !rawPassword.isBlank()) ? rawPassword : "password123"));
            u.setRole(User.Role.STUDENT);
            u.setLinkedId(saved.getStudentId().longValue());
            u.setIsActive(true);
            userRepository.save(u);
            log.info("Login account created for new student: {}", saved.getEmail());
        }

        return saved;
    }

    public Student updateStudent(Integer id, Student updatedStudent) {
        Student existing = getStudentById(id);

        if (!existing.getEmail().equals(updatedStudent.getEmail())
                && studentRepository.existsByEmail(updatedStudent.getEmail())) {
            throw new IllegalArgumentException(
                    "Email '" + updatedStudent.getEmail() + "' is already registered");
        }

        existing.setFirstName(updatedStudent.getFirstName());
        existing.setLastName(updatedStudent.getLastName());
        existing.setEmail(updatedStudent.getEmail());
        existing.setProgram(updatedStudent.getProgram());
        existing.setYearOfStudy(updatedStudent.getYearOfStudy());
        existing.setEnrolmentStatus(updatedStudent.getEnrolmentStatus());

        return studentRepository.save(existing);
    }

    public void deleteStudent(Integer id) {
        Student student = getStudentById(id);
        // Remove FK-dependent enrolment records first
        enrolmentRepository.deleteAll(
                enrolmentRepository.findByStudent_StudentId(id));
        // Remove the linked login account
        userRepository.findByEmail(student.getEmail()).ifPresent(userRepository::delete);
        studentRepository.delete(student);
    }

    public List<Student> getStudentsByYear(Integer year) {
        return studentRepository.findByYearOfStudy(year);
    }
}
