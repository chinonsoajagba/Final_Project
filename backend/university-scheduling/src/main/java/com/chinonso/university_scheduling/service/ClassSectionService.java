package com.chinonso.university_scheduling.service;

import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.Course;
import com.chinonso.university_scheduling.entity.Room;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.ClassSectionRepository;
import com.chinonso.university_scheduling.repository.CourseRepository;
import com.chinonso.university_scheduling.repository.RoomRepository;

import java.util.List;

@Service
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

    // GET ALL
    public List<ClassSection> getAllClasses() {
        return classSectionRepository.findAll();
    }

    // GET BY ID
    public ClassSection getClassById(Integer id) {
        return classSectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class not found with ID: " + id));
    }

    // GET BY COURSE
    public List<ClassSection> getClassesByCourse(Integer courseId) {
        return classSectionRepository.findByCourse_CourseId(courseId);
    }

    // GET BY ROOM
    public List<ClassSection> getClassesByRoom(Integer roomId) {
        return classSectionRepository.findByRoom_RoomId(roomId);
    }

    // GET BY SEMESTER AND YEAR
    public List<ClassSection> getClassesBySemesterAndYear(
            ClassSection.Semester semester, String academicYear) {
        return classSectionRepository.findBySemesterAndAcademicYear(semester, academicYear);
    }

    // CREATE
    public ClassSection createClass(ClassSection classSection, Integer courseId, Integer roomId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with ID: " + courseId));
        classSection.setCourse(course);

        if (roomId != null) {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Room not found with ID: " + roomId));

            if (classSection.getMaxEnrolment() > room.getCapacity()) {
                throw new IllegalArgumentException(
                        "Max enrolment (" + classSection.getMaxEnrolment() +
                                ") cannot exceed room capacity (" + room.getCapacity() + ")");
            }
            classSection.setRoom(room);
        }

        classSection.setCurrentEnrolment(0);
        return classSectionRepository.save(classSection);
    }

    // UPDATE
    public ClassSection updateClass(Integer id, ClassSection updated, Integer courseId, Integer roomId) {
        ClassSection existing = getClassById(id);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with ID: " + courseId));
        existing.setCourse(course);

        if (roomId != null) {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Room not found with ID: " + roomId));

            if (updated.getMaxEnrolment() > room.getCapacity()) {
                throw new IllegalArgumentException(
                        "Max enrolment (" + updated.getMaxEnrolment() +
                                ") cannot exceed room capacity (" + room.getCapacity() + ")");
            }
            existing.setRoom(room);
        } else {
            existing.setRoom(null);
        }

        existing.setSectionNumber(updated.getSectionNumber());
        existing.setAcademicYear(updated.getAcademicYear());
        existing.setSemester(updated.getSemester());
        existing.setMaxEnrolment(updated.getMaxEnrolment());
        existing.setStatus(updated.getStatus());

        return classSectionRepository.save(existing);
    }

    // DELETE
    public void deleteClass(Integer id) {
        ClassSection classSection = getClassById(id);
        classSectionRepository.delete(classSection);
    }

    public void incrementEnrolment(Integer classId) {
        ClassSection cs = getClassById(classId);
        cs.setCurrentEnrolment(cs.getCurrentEnrolment() + 1);
        classSectionRepository.save(cs);
    }

    public void decrementEnrolment(Integer classId) {
        ClassSection cs = getClassById(classId);
        int current = cs.getCurrentEnrolment();
        if (current > 0) {
            cs.setCurrentEnrolment(current - 1);
            classSectionRepository.save(cs);
        }
    }
}
