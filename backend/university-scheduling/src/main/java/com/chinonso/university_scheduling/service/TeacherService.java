package com.chinonso.university_scheduling.service;

import com.chinonso.university_scheduling.entity.Teacher;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    // ── READ ───────────────────────────────────────────────────────────────────
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    public Teacher findById(Integer id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
    }

    public Teacher findByEmployeeId(String employeeId) {
        return teacherRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with employee id: " + employeeId));
    }

    public List<Teacher> findByDepartment(String department) {
        return teacherRepository.findByDepartment(department);
    }

    // ── CREATE ─────────────────────────────────────────────────────────────────
    public Teacher create(Teacher teacher) {
        if (teacherRepository.existsByEmployeeId(teacher.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists: " + teacher.getEmployeeId());
        }
        if (teacherRepository.existsByEmail(teacher.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + teacher.getEmail());
        }
        return teacherRepository.save(teacher);
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────────
    public Teacher update(Integer id, Teacher updated) {
        Teacher existing = findById(id);

        if (!existing.getEmployeeId().equals(updated.getEmployeeId())
                && teacherRepository.existsByEmployeeId(updated.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists: " + updated.getEmployeeId());
        }
        if (!existing.getEmail().equals(updated.getEmail())
                && teacherRepository.existsByEmail(updated.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + updated.getEmail());
        }

        existing.setEmployeeId(updated.getEmployeeId());
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setDepartment(updated.getDepartment());
        existing.setSpecialization(updated.getSpecialization());
        existing.setMaxHoursPerWeek(updated.getMaxHoursPerWeek());

        return teacherRepository.save(existing);
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────
    public void delete(Integer id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }
}
