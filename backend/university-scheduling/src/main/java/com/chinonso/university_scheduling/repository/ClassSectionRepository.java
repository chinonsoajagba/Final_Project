package com.chinonso.university_scheduling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.chinonso.university_scheduling.entity.ClassSection;

import java.util.List;

@Repository
public interface ClassSectionRepository extends JpaRepository<ClassSection, Integer> {

    // Get all classes for a specific course
    List<ClassSection> findByCourse_CourseId(Integer courseId);

    // Get all classes in a specific room
    List<ClassSection> findByRoom_RoomId(Integer roomId);

    // Get all classes for a semester and year
    List<ClassSection> findBySemesterAndAcademicYear(
            ClassSection.Semester semester, String academicYear);

    // Get classes that are not yet full (for enrolment)
    @Query("SELECT c FROM ClassSection c WHERE c.currentEnrolment < c.maxEnrolment")
    List<ClassSection> findAvailableSections();
}