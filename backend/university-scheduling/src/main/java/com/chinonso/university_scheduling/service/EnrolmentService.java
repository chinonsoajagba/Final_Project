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

    private void checkStudentScheduleClash(Student student, ClassSection newClass) {
        List<Schedule> newClassSchedules = scheduleRepository
                .findByClassSection_ClassId(newClass.getClassId());

        if (newClassSchedules.isEmpty()) {
            return;
        }

        List<Schedule> studentCurrentSchedules = scheduleRepository
                .findSchedulesByStudentId(student.getStudentId());

        for (Schedule newSlot : newClassSchedules) {
            for (Schedule existingSlot : studentCurrentSchedules) {
                if (newSlot.getDayOfWeek() == existingSlot.getDayOfWeek()) {
                    boolean overlap = newSlot.getStartTime().isBefore(existingSlot.getEndTime())
                            && newSlot.getEndTime().isAfter(existingSlot.getStartTime());

                    if (overlap) {
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

    public Enrolment enrolStudent(Integer studentId, Integer classId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with ID: " + studentId));

        ClassSection classSection = classSectionRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class not found with ID: " + classId));

        if (student.getEnrolmentStatus() != Student.EnrolmentStatus.ACTIVE) {
            throw new SchedulingConflictException(
                    "Cannot enrol: " +
                            student.getFirstName() + " " + student.getLastName() +
                            " is not an active student. " +
                            "Current status: " + student.getEnrolmentStatus() + ".");
        }

        if (enrolmentRepository.existsByStudent_StudentIdAndClassSection_ClassId(
                studentId, classId)) {
            throw new SchedulingConflictException(
                    "Cannot enrol: " +
                            student.getFirstName() + " " + student.getLastName() +
                            " is already enrolled in " +
                            classSection.getCourse().getCourseCode() +
                            " Section " + classSection.getSectionNumber() + ".");
        }

        checkRoomCapacity(classSection);
        checkCourseLevelMatch(student, classSection);
        checkStudentScheduleClash(student, classSection);

        Enrolment enrolment = new Enrolment(student, classSection, LocalDate.now());
        Enrolment saved = enrolmentRepository.save(enrolment);
        classSectionService.incrementEnrolment(classId);

        return saved;
    }

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
        classSectionService.decrementEnrolment(classId);

        return updated;
    }

    public void deleteEnrolment(Integer id) {
        Enrolment enrolment = getEnrolmentById(id);
        classSectionService.decrementEnrolment(
                enrolment.getClassSection().getClassId());
        enrolmentRepository.delete(enrolment);
    }
}
