package com.chinonso.university_scheduling.service;

import com.chinonso.university_scheduling.entity.Student;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ── READ ───────────────────────────────────────────────────────────────────
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student findById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }

    public List<Student> findByYearOfStudy(Integer year) {
        return studentRepository.findByYearOfStudy(year);
    }

    public List<Student> findByProgram(String program) {
        return studentRepository.findByProgram(program);
    }

    public List<Student> findActiveStudents() {
        return studentRepository.findByEnrolmentStatus(Student.EnrolmentStatus.ACTIVE);
    }

    // ── CREATE ─────────────────────────────────────────────────────────────────
    public Student create(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + student.getEmail());
        }
        return studentRepository.save(student);
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────────
    public Student update(Integer id, Student updated) {
        Student existing = findById(id);

        if (!existing.getEmail().equals(updated.getEmail())
                && studentRepository.existsByEmail(updated.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + updated.getEmail());
        }

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setProgram(updated.getProgram());
        existing.setYearOfStudy(updated.getYearOfStudy());
        existing.setEnrolmentStatus(updated.getEnrolmentStatus());

        return studentRepository.save(existing);
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────
    public void delete(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }
}
