package com.chinonso.university_scheduling.service;

import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.Student;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
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
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException(
                    "Email '" + student.getEmail() + "' is already registered");
        }
        return studentRepository.save(student);
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
        studentRepository.delete(student);
    }

    public List<Student> getStudentsByYear(Integer year) {
        return studentRepository.findByYearOfStudy(year);
    }
}
