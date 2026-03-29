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

    public List<ClassTeacher> getAllAssignments() {
        return classTeacherRepository.findAll();
    }

    public ClassTeacher getAssignmentById(Integer id) {
        return classTeacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class-teacher assignment not found with ID: " + id));
    }

    public List<ClassTeacher> getAssignmentsByClass(Integer classId) {
        return classTeacherRepository.findByClassSection_ClassId(classId);
    }

    public List<ClassTeacher> getAssignmentsByTeacher(Integer teacherId) {
        return classTeacherRepository.findByTeacher_TeacherId(teacherId);
    }

    public ClassTeacher assignTeacher(Integer classId, Integer teacherId, ClassTeacher.TeacherRole role) {
        ClassSection classSection = classSectionRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class not found with ID: " + classId));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher not found with ID: " + teacherId));

        if (classTeacherRepository.existsByClassSection_ClassIdAndTeacher_TeacherId(classId, teacherId)) {
            throw new IllegalArgumentException(
                    "Teacher " + teacher.getFirstName() + " " + teacher.getLastName() +
                            " is already assigned to this class");
        }

        ClassTeacher assignment = new ClassTeacher(classSection, teacher, role);
        return classTeacherRepository.save(assignment);
    }

    public ClassTeacher updateRole(Integer id, ClassTeacher.TeacherRole newRole) {
        ClassTeacher assignment = getAssignmentById(id);
        assignment.setRole(newRole);
        return classTeacherRepository.save(assignment);
    }

    public void removeAssignment(Integer id) {
        ClassTeacher assignment = getAssignmentById(id);
        classTeacherRepository.delete(assignment);
    }
}
