package com.chinonso.university_scheduling.service;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.Enrolment;
import com.chinonso.university_scheduling.entity.Student;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.ClassSectionRepository;
import com.chinonso.university_scheduling.repository.EnrolmentRepository;
import com.chinonso.university_scheduling.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EnrolmentService {

    private final EnrolmentRepository enrolmentRepository;
    private final StudentRepository studentRepository;
    private final ClassSectionRepository classSectionRepository;

    public EnrolmentService(EnrolmentRepository enrolmentRepository,
                            StudentRepository studentRepository,
                            ClassSectionRepository classSectionRepository) {
        this.enrolmentRepository = enrolmentRepository;
        this.studentRepository = studentRepository;
        this.classSectionRepository = classSectionRepository;
    }

    // ── READ ───────────────────────────────────────────────────────────────────
    public List<Enrolment> findAll() {
        return enrolmentRepository.findAll();
    }

    public Enrolment findById(Integer id) {
        return enrolmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrolment not found with id: " + id));
    }

    public List<Enrolment> findByStudent(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return enrolmentRepository.findByStudent(student);
    }

    public List<Enrolment> findByClassSection(Integer classSectionId) {
        ClassSection cs = classSectionRepository.findById(classSectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class section not found with id: " + classSectionId));
        return enrolmentRepository.findByClassSection(cs);
    }

    // ── ENROL STUDENT (central method — conflict checks added on Day 5) ────────
    /**
     * Entry point for enrolling a student into a class.
     * All conflict checks will be wired in here on Day 5.
     */
    public Enrolment enrolStudent(Integer studentId, Integer classSectionId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        ClassSection classSection = classSectionRepository.findById(classSectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class section not found with id: " + classSectionId));

        // ── TODO Day 5: checkRoomCapacity(classSection) ────────────────────────
        // ── TODO Day 5: checkStudentScheduleClash(student, classSection) ────────
        // ── TODO Day 5: checkCourseLevelVsStudentYear(student, classSection) ────
        // ── TODO Day 5: checkRoomTimeClash(classSection) ─────────────────────
        // ── TODO Day 5: checkDuplicateEnrolment(student, classSection) ─────────

        Enrolment enrolment = Enrolment.builder()
                .student(student)
                .classSection(classSection)
                .enrolmentDate(LocalDate.now())
                .status(Enrolment.EnrolmentStatus.ENROLLED)
                .build();

        return enrolmentRepository.save(enrolment);
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────────
    public Enrolment update(Integer id, Enrolment updated) {
        Enrolment existing = findById(id);
        existing.setStatus(updated.getStatus());
        existing.setGrade(updated.getGrade());
        return enrolmentRepository.save(existing);
    }

    // ── DROP ENROLMENT ─────────────────────────────────────────────────────────
    public Enrolment dropEnrolment(Integer id) {
        Enrolment enrolment = findById(id);
        enrolment.setStatus(Enrolment.EnrolmentStatus.DROPPED);
        return enrolmentRepository.save(enrolment);
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────
    public void delete(Integer id) {
        if (!enrolmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Enrolment not found with id: " + id);
        }
        enrolmentRepository.deleteById(id);
    }
}
