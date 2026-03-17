package com.chinonso.university_scheduling.service;

import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.Enrolment;
import com.chinonso.university_scheduling.entity.Student;
import com.chinonso.university_scheduling.entity.Schedule;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.exception.SchedulingConflictException;
import com.chinonso.university_scheduling.repository.ClassSectionRepository;
import com.chinonso.university_scheduling.repository.EnrolmentRepository;
import com.chinonso.university_scheduling.repository.StudentRepository;
import com.chinonso.university_scheduling.repository.ScheduleRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrolmentService {

    private final EnrolmentRepository enrolmentRepository;
    private final StudentRepository studentRepository;
    private final ClassSectionRepository classSectionRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClassSectionService classSectionService;

    public EnrolmentService(EnrolmentRepository enrolmentRepository,
            StudentRepository studentRepository,
            ClassSectionRepository classSectionRepository,
            ScheduleRepository scheduleRepository,
            ClassSectionService classSectionService) {
        this.enrolmentRepository = enrolmentRepository;
        this.studentRepository = studentRepository;
        this.classSectionRepository = classSectionRepository;
        this.scheduleRepository = scheduleRepository;
        this.classSectionService = classSectionService;
    }

    // ================================================================
    // GET METHODS
    // ================================================================

    public List<Enrolment> getAllEnrolments() {
        return enrolmentRepository.findAll();
    }

    public List<Enrolment> getEnrolmentsByStudent(Integer studentId) {
        return enrolmentRepository.findByStudent_StudentId(studentId);
    }

    public List<Enrolment> getEnrolmentsByClass(Integer classId) {
        return enrolmentRepository.findByClassSection_ClassId(classId);
    }

    public Enrolment getEnrolmentById(Integer id) {
        return enrolmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrolment not found with ID: " + id));
    }

    // ================================================================
    // CONFLICT CHECK 1: ROOM CAPACITY
    // Throws error if the class is already full
    // ================================================================
    private void checkRoomCapacity(ClassSection classSection) {
        if (classSection.getCurrentEnrolment() >= classSection.getMaxEnrolment()) {
            throw new SchedulingConflictException(
                    "Cannot enrol: Class is full. " +
                            "Maximum capacity of " + classSection.getMaxEnrolment() +
                            " students has been reached for " +
                            classSection.getCourse().getCourseCode() +
                            " Section " + classSection.getSectionNumber() + ".");
        }
    }

    // ================================================================
    // CONFLICT CHECK 2: COURSE LEVEL vs STUDENT YEAR
    // The supervisor's key requirement — prevents wrong year enrolment
    // ================================================================
    private void checkCourseLevelMatch(Student student, ClassSection classSection) {
        int courseLevel = classSection.getCourse().getLevel();
        int studentYear = student.getYearOfStudy();

        if (courseLevel != studentYear) {
            throw new SchedulingConflictException(
                    "Cannot enrol: This course (" +
                            classSection.getCourse().getCourseCode() + " - " +
                            classSection.getCourse().getCourseName() +
                            ") is for Year " + courseLevel + " students only. " +
                            student.getFirstName() + " " + student.getLastName() +
                            " is a Year " + studentYear + " student.");
        }
    }

    // ================================================================
    // CONFLICT CHECK 3: STUDENT SCHEDULE CLASH
    // Checks if the student already has a class at the same time/day
    // ================================================================
    private void checkStudentScheduleClash(Student student, ClassSection newClass) {
        // Get all schedule slots for the new class
        List<Schedule> newClassSchedules = scheduleRepository
                .findByClassSection_ClassId(newClass.getClassId());

        // If the new class has no schedule yet, no clash is possible
        if (newClassSchedules.isEmpty()) {
            return;
        }

        // Get all current schedules the student is already enrolled in
        List<Schedule> studentCurrentSchedules = scheduleRepository
                .findSchedulesByStudentId(student.getStudentId());

        // Compare each new slot against every existing student slot
        for (Schedule newSlot : newClassSchedules) {
            for (Schedule existingSlot : studentCurrentSchedules) {

                // Only compare if they are on the same day
                if (newSlot.getDayOfWeek() == existingSlot.getDayOfWeek()) {

                    // Check time overlap:
                    // Two slots overlap if one starts before the other ends
                    boolean overlap = newSlot.getStartTime().isBefore(existingSlot.getEndTime())
                            && newSlot.getEndTime().isAfter(existingSlot.getStartTime());

                    if (overlap) {
                        // Get the conflicting class name for a helpful message
                        String conflictingClass = existingSlot.getClassSection().getCourse().getCourseCode() +
                                " Section " +
                                existingSlot.getClassSection().getSectionNumber();

                        throw new SchedulingConflictException(
                                "Cannot enrol: Schedule clash detected. " +
                                        student.getFirstName() + " " + student.getLastName() +
                                        " already has " + conflictingClass +
                                        " on " + existingSlot.getDayOfWeek() +
                                        " from " + existingSlot.getStartTime() +
                                        " to " + existingSlot.getEndTime() + ". " +
                                        "The new class (" +
                                        newClass.getCourse().getCourseCode() +
                                        " Section " + newClass.getSectionNumber() +
                                        ") conflicts at that time.");
                    }
                }
            }
        }
    }

    // ================================================================
    // CENTRAL ENROL METHOD — runs ALL checks in order before saving
    // This is the method the controller will call
    // ================================================================
    public Enrolment enrolStudent(Integer studentId, Integer classId) {

        // STEP 1: Fetch student — 404 if not found
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + studentId));

        // STEP 2: Fetch class section — 404 if not found
        ClassSection classSection = classSectionRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class not found with ID: " + classId));

        // STEP 3: Student must be ACTIVE to enrol
        if (student.getEnrolmentStatus() != Student.EnrolmentStatus.ACTIVE) {
            throw new SchedulingConflictException(
                    "Cannot enrol: " +
                            student.getFirstName() + " " + student.getLastName() +
                            " is not an active student. " +
                            "Current status: " + student.getEnrolmentStatus() + ".");
        }

        // STEP 4: Student must not already be enrolled in this exact class
        if (enrolmentRepository.existsByStudent_StudentIdAndClassSection_ClassId(
                studentId, classId)) {
            throw new SchedulingConflictException(
                    "Cannot enrol: " +
                            student.getFirstName() + " " + student.getLastName() +
                            " is already enrolled in " +
                            classSection.getCourse().getCourseCode() +
                            " Section " + classSection.getSectionNumber() + ".");
        }

        // STEP 5: Check room capacity
        checkRoomCapacity(classSection);

        // STEP 6: Check course level matches student year
        checkCourseLevelMatch(student, classSection);

        // STEP 7: Check no schedule clash with student's existing classes
        checkStudentScheduleClash(student, classSection);

        // ALL CHECKS PASSED — save the enrolment
        Enrolment enrolment = new Enrolment(student, classSection, LocalDate.now());
        Enrolment saved = enrolmentRepository.save(enrolment);

        // Update the class enrolment count
        classSectionService.incrementEnrolment(classId);

        return saved;
    }

    // ================================================================
    // DROP A STUDENT FROM A CLASS
    // ================================================================
    public Enrolment dropStudent(Integer studentId, Integer classId) {
        Enrolment enrolment = enrolmentRepository
                .findByStudent_StudentIdAndClassSection_ClassId(studentId, classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrolment not found for student " + studentId +
                                " and class " + classId));

        if (enrolment.getStatus() == Enrolment.EnrolmentStatus.DROPPED) {
            throw new IllegalArgumentException(
                    "Student is already dropped from this class");
        }

        enrolment.setStatus(Enrolment.EnrolmentStatus.DROPPED);
        Enrolment updated = enrolmentRepository.save(enrolment);

        // Decrement class enrolment count
        classSectionService.decrementEnrolment(classId);

        return updated;
    }

    // ================================================================
    // DELETE ENROLMENT RECORD COMPLETELY
    // ================================================================
    public void deleteEnrolment(Integer id) {
        Enrolment enrolment = getEnrolmentById(id);
        classSectionService.decrementEnrolment(
                enrolment.getClassSection().getClassId());
        enrolmentRepository.delete(enrolment);
    }
}
