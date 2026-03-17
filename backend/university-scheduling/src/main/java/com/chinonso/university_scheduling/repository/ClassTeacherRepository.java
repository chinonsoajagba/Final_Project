package com.chinonso.university_scheduling.repository;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.ClassTeacher;
import com.chinonso.university_scheduling.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, Integer> {

    List<ClassTeacher> findByClassSection(ClassSection classSection);

    List<ClassTeacher> findByTeacher(Teacher teacher);

    boolean existsByClassSectionAndTeacher(ClassSection classSection, Teacher teacher);
}
