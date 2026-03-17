package com.chinonso.university_scheduling.service;

import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.ClassTeacher;
import com.chinonso.university_scheduling.entity.Teacher;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.ClassSectionRepository;
import com.chinonso.university_scheduling.repository.ClassTeacherRepository;
import com.chinonso.university_scheduling.repository.TeacherRepository;

import java.util.List;

@Service
public class ClassTeacherService {

    private final ClassTeacherRepository classTeacherRepository;
    private final ClassSectionRepository classSectionRepository;
    private final TeacherRepository teacherRepository;

    public ClassTeacherService(ClassTeacherRepository classTeacherRepository,
            ClassSectionRepository classSectionRepository,
            TeacherRepository teacherRepository) {
        this.classTeacherRepository = classTeacherRepository;
        this.classSectionRepository = classSectionRepository;
        this.teacherRepository = teacherRepository;
    }

    // GET ALL
    public List<ClassTeacher> getAllAssignments() {
        return classTeacherRepository.findAll();
    }

    // GET BY ID
    public ClassTeacher getAssignmentById(Integer id) {
        return classTeacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class-teacher assignment not found with ID: " + id));
    }

    // GET ALL ASSIGNMENTS FOR A CLASS
    public List<ClassTeacher> getAssignmentsByClass(Integer classId) {
        return classTeacherRepository.findByClassSection_ClassId(classId);
    }

    // GET ALL ASSIGNMENTS FOR A TEACHER
    public List<ClassTeacher> getAssignmentsByTeacher(Integer teacherId) {
        return classTeacherRepository.findByTeacher_TeacherId(teacherId);
    }

    // ASSIGN TEACHER TO CLASS
    public ClassTeacher assignTeacher(Integer classId, Integer teacherId, ClassTeacher.TeacherRole role) {
        ClassSection classSection = classSectionRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class not found with ID: " + classId));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher not found with ID: " + teacherId));

        // Prevent duplicate assignment of same teacher to same class
        if (classTeacherRepository.existsByClassSection_ClassIdAndTeacher_TeacherId(classId, teacherId)) {
            throw new IllegalArgumentException(
                    "Teacher " + teacher.getFirstName() + " " + teacher.getLastName() +
                            " is already assigned to this class");
        }

        ClassTeacher assignment = new ClassTeacher(classSection, teacher, role);
        return classTeacherRepository.save(assignment);
    }

    // UPDATE ROLE (e.g. promote TA to Lecturer)
    public ClassTeacher updateRole(Integer id, ClassTeacher.TeacherRole newRole) {
        ClassTeacher assignment = getAssignmentById(id);
        assignment.setRole(newRole);
        return classTeacherRepository.save(assignment);
    }

    // REMOVE TEACHER FROM CLASS
    public void removeAssignment(Integer id) {
        ClassTeacher assignment = getAssignmentById(id);
        classTeacherRepository.delete(assignment);
    }
}
