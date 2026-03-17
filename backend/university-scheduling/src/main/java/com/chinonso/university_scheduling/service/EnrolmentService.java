package com.chinonso.university_scheduling.service;

import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.Enrolment;
import com.chinonso.university_scheduling.entity.Student;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.exception.SchedulingConflictException;
import com.chinonso.university_scheduling.repository.ClassSectionRepository;
import com.chinonso.university_scheduling.repository.EnrolmentRepository;
import com.chinonso.university_scheduling.repository.StudentRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrolmentService {

    private final EnrolmentRepository enrolmentRepository;
    private final StudentRepository studentRepository;
    private final ClassSectionRepository classSectionRepository;
    private final ClassSectionService classSectionService;

    public EnrolmentService(EnrolmentRepository enrolmentRepository,
            StudentRepository studentRepository,
            ClassSectionRepository classSectionRepository,
            ClassSectionService classSectionService) {
        this.enrolmentRepository = enrolmentRepository;
        this.studentRepository = studentRepository;
        this.classSectionRepository = classSectionRepository;
        this.classSectionService = classSectionService;
    }

    // GET ALL ENROLMENTS
    public List<Enrolment> getAllEnrolments() {
        return enrolmentRepository.findAll();
    }

    // GET ENROLMENTS BY STUDENT
    public List<Enrolment> getEnrolmentsByStudent(Integer studentId) {
        return enrolmentRepository.findByStudent_StudentId(studentId);
    }

    // GET ENROLMENTS BY CLASS
    public List<Enrolment> getEnrolmentsByClass(Integer classId) {
        return enrolmentRepository.findByClassSection_ClassId(classId);
    }

    // GET BY ID
    public Enrolment getEnrolmentById(Integer id) {
        return enrolmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrolment not found with ID: " + id));
    }

    public Enrolment enrolStudent(Integer studentId, Integer classId) {

        // 1. Fetch student and class — throw 404 if not found
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + studentId));

        ClassSection classSection = classSectionRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class not found with ID: " + classId));

        // 2. Check student is ACTIVE
        if (student.getEnrolmentStatus() != Student.EnrolmentStatus.ACTIVE) {
            throw new SchedulingConflictException(
                    "Student " + student.getFirstName() + " " + student.getLastName() +
                            " is not active. Current status: " + student.getEnrolmentStatus());
        }

        // 3. Check student is not already enrolled in this class
        if (enrolmentRepository.existsByStudent_StudentIdAndClassSection_ClassId(studentId, classId)) {
            throw new SchedulingConflictException(
                    "Student is already enrolled in this class");
        }

        // 4. Save the enrolment
        Enrolment enrolment = new Enrolment(student, classSection, LocalDate.now());
        Enrolment saved = enrolmentRepository.save(enrolment);

        // 5. Increment class enrolment count
        classSectionService.incrementEnrolment(classId);

        return saved;
    }

    // DROP A STUDENT FROM A CLASS
    public Enrolment dropStudent(Integer studentId, Integer classId) {
        Enrolment enrolment = enrolmentRepository
                .findByStudent_StudentIdAndClassSection_ClassId(studentId, classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrolment not found for student " + studentId +
                                " and class " + classId));

        if (enrolment.getStatus() == Enrolment.EnrolmentStatus.DROPPED) {
            throw new IllegalArgumentException("Student is already dropped from this class");
        }

        enrolment.setStatus(Enrolment.EnrolmentStatus.DROPPED);
        Enrolment updated = enrolmentRepository.save(enrolment);

        // Decrement class enrolment count
        classSectionService.decrementEnrolment(classId);

        return updated;
    }

    // DELETE ENROLMENT RECORD
    public void deleteEnrolment(Integer id) {
        Enrolment enrolment = getEnrolmentById(id);
        classSectionService.decrementEnrolment(
                enrolment.getClassSection().getClassId());
        enrolmentRepository.delete(enrolment);
    }
}
