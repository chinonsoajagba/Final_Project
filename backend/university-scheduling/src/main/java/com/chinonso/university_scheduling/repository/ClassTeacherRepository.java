package com.chinonso.university_scheduling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chinonso.university_scheduling.entity.ClassTeacher;

import java.util.List;

@Repository
public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, Integer> {

    // Get all class assignments for a teacher
    List<ClassTeacher> findByTeacher_TeacherId(Integer teacherId);

    // Get all teachers assigned to a class
    List<ClassTeacher> findByClassSection_ClassId(Integer classId);

    // Check if teacher is already assigned to this class
    boolean existsByClassSection_ClassIdAndTeacher_TeacherId(Integer classId, Integer teacherId);
}
