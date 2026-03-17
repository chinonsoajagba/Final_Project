package com.chinonso.university_scheduling.repository;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.Course;
import com.chinonso.university_scheduling.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassSectionRepository extends JpaRepository<ClassSection, Integer> {

    List<ClassSection> findByCourse(Course course);

    List<ClassSection> findByRoom(Room room);

    List<ClassSection> findBySemester(ClassSection.Semester semester);

    List<ClassSection> findByAcademicYear(String academicYear);

    List<ClassSection> findByStatus(ClassSection.ClassStatus status);

    List<ClassSection> findByCourseAndSemesterAndAcademicYear(
            Course course, ClassSection.Semester semester, String academicYear);
}
