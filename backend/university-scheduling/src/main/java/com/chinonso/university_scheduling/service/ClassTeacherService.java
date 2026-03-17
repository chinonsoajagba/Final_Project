package com.chinonso.university_scheduling.service;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.ClassTeacher;
import com.chinonso.university_scheduling.entity.Teacher;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.ClassSectionRepository;
import com.chinonso.university_scheduling.repository.ClassTeacherRepository;
import com.chinonso.university_scheduling.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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

    // ── READ ───────────────────────────────────────────────────────────────────
    public List<ClassTeacher> findAll() {
        return classTeacherRepository.findAll();
    }

    public ClassTeacher findById(Integer id) {
        return classTeacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassTeacher assignment not found with id: " + id));
    }

    public List<ClassTeacher> findByClassSection(Integer classSectionId) {
        ClassSection cs = classSectionRepository.findById(classSectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class section not found with id: " + classSectionId));
        return classTeacherRepository.findByClassSection(cs);
    }

    public List<ClassTeacher> findByTeacher(Integer teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));
        return classTeacherRepository.findByTeacher(teacher);
    }

    // ── CREATE ─────────────────────────────────────────────────────────────────
    public ClassTeacher assignTeacher(Integer classSectionId, Integer teacherId,
                                      ClassTeacher.TeacherRole role) {
        ClassSection cs = classSectionRepository.findById(classSectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class section not found with id: " + classSectionId));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + teacherId));

        if (classTeacherRepository.existsByClassSectionAndTeacher(cs, teacher)) {
            throw new IllegalArgumentException(
                    "Teacher is already assigned to this class section.");
        }

        ClassTeacher assignment = ClassTeacher.builder()
                .classSection(cs)
                .teacher(teacher)
                .role(role != null ? role : ClassTeacher.TeacherRole.LECTURER)
                .build();

        return classTeacherRepository.save(assignment);
    }

    // ── UPDATE (change role only) ───────────────────────────────────────────────
    public ClassTeacher updateRole(Integer id, ClassTeacher.TeacherRole newRole) {
        ClassTeacher existing = findById(id);
        existing.setRole(newRole);
        return classTeacherRepository.save(existing);
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────
    public void delete(Integer id) {
        if (!classTeacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("ClassTeacher assignment not found with id: " + id);
        }
        classTeacherRepository.deleteById(id);
    }
}
