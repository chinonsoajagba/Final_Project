package com.chinonso.university_scheduling.service;

import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.Teacher;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.TeacherRepository;

import java.util.List;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher getTeacherById(Integer id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Teacher not found with ID: " + id));
    }

    public Teacher createTeacher(Teacher teacher) {
        if (teacherRepository.existsByEmployeeId(teacher.getEmployeeId())) {
            throw new IllegalArgumentException(
                    "Employee ID '" + teacher.getEmployeeId() + "' already exists");
        }
        if (teacherRepository.existsByEmail(teacher.getEmail())) {
            throw new IllegalArgumentException(
                    "Email '" + teacher.getEmail() + "' is already registered");
        }
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Integer id, Teacher updatedTeacher) {
        Teacher existing = getTeacherById(id);

        if (!existing.getEmployeeId().equals(updatedTeacher.getEmployeeId())
                && teacherRepository.existsByEmployeeId(updatedTeacher.getEmployeeId())) {
            throw new IllegalArgumentException(
                    "Employee ID '" + updatedTeacher.getEmployeeId() + "' already exists");
        }
        if (!existing.getEmail().equals(updatedTeacher.getEmail())
                && teacherRepository.existsByEmail(updatedTeacher.getEmail())) {
            throw new IllegalArgumentException(
                    "Email '" + updatedTeacher.getEmail() + "' is already registered");
        }

        existing.setEmployeeId(updatedTeacher.getEmployeeId());
        existing.setFirstName(updatedTeacher.getFirstName());
        existing.setLastName(updatedTeacher.getLastName());
        existing.setEmail(updatedTeacher.getEmail());
        existing.setDepartment(updatedTeacher.getDepartment());
        existing.setSpecialization(updatedTeacher.getSpecialization());
        existing.setMaxHoursPerWeek(updatedTeacher.getMaxHoursPerWeek());

        return teacherRepository.save(existing);
    }

    public void deleteTeacher(Integer id) {
        Teacher teacher = getTeacherById(id);
        teacherRepository.delete(teacher);
    }

    public List<Teacher> getTeachersByDepartment(String department) {
        return teacherRepository.findByDepartment(department);
    }
}
