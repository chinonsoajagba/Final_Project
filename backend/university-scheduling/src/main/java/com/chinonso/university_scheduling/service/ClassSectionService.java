package com.chinonso.university_scheduling.service;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.Course;
import com.chinonso.university_scheduling.entity.Room;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.ClassSectionRepository;
import com.chinonso.university_scheduling.repository.CourseRepository;
import com.chinonso.university_scheduling.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClassSectionService {

    private final ClassSectionRepository classSectionRepository;
    private final CourseRepository courseRepository;
    private final RoomRepository roomRepository;

    public ClassSectionService(ClassSectionRepository classSectionRepository,
                               CourseRepository courseRepository,
                               RoomRepository roomRepository) {
        this.classSectionRepository = classSectionRepository;
        this.courseRepository = courseRepository;
        this.roomRepository = roomRepository;
    }

    // ── READ ───────────────────────────────────────────────────────────────────
    public List<ClassSection> findAll() {
        return classSectionRepository.findAll();
    }

    public ClassSection findById(Integer id) {
        return classSectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class section not found with id: " + id));
    }

    public List<ClassSection> findByCourse(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        return classSectionRepository.findByCourse(course);
    }

    public List<ClassSection> findByRoom(Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));
        return classSectionRepository.findByRoom(room);
    }

    public List<ClassSection> findBySemester(ClassSection.Semester semester) {
        return classSectionRepository.findBySemester(semester);
    }

    public List<ClassSection> findByAcademicYear(String academicYear) {
        return classSectionRepository.findByAcademicYear(academicYear);
    }

    public List<ClassSection> findByStatus(ClassSection.ClassStatus status) {
        return classSectionRepository.findByStatus(status);
    }

    // ── CREATE ─────────────────────────────────────────────────────────────────
    public ClassSection create(ClassSection classSection) {
        // Validate referenced course exists
        if (classSection.getCourse() != null && classSection.getCourse().getCourseId() != null) {
            Course course = courseRepository.findById(classSection.getCourse().getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Course not found with id: " + classSection.getCourse().getCourseId()));
            classSection.setCourse(course);
        }
        // Validate referenced room if provided
        if (classSection.getRoom() != null && classSection.getRoom().getRoomId() != null) {
            Room room = roomRepository.findById(classSection.getRoom().getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Room not found with id: " + classSection.getRoom().getRoomId()));
            classSection.setRoom(room);
        }
        return classSectionRepository.save(classSection);
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────────
    public ClassSection update(Integer id, ClassSection updated) {
        ClassSection existing = findById(id);

        // Resolve course
        if (updated.getCourse() != null && updated.getCourse().getCourseId() != null) {
            Course course = courseRepository.findById(updated.getCourse().getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Course not found with id: " + updated.getCourse().getCourseId()));
            existing.setCourse(course);
        }
        // Resolve room (nullable)
        if (updated.getRoom() != null && updated.getRoom().getRoomId() != null) {
            Room room = roomRepository.findById(updated.getRoom().getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Room not found with id: " + updated.getRoom().getRoomId()));
            existing.setRoom(room);
        } else {
            existing.setRoom(null);
        }

        existing.setSectionNumber(updated.getSectionNumber());
        existing.setAcademicYear(updated.getAcademicYear());
        existing.setSemester(updated.getSemester());
        existing.setMaxEnrolment(updated.getMaxEnrolment());
        existing.setCurrentEnrolment(updated.getCurrentEnrolment());
        existing.setStatus(updated.getStatus());

        return classSectionRepository.save(existing);
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────
    public void delete(Integer id) {
        if (!classSectionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Class section not found with id: " + id);
        }
        classSectionRepository.deleteById(id);
    }
}
