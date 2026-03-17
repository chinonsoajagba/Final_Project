package com.chinonso.university_scheduling.repository;

import com.chinonso.university_scheduling.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    Optional<Teacher> findByEmployeeId(String employeeId);

    Optional<Teacher> findByEmail(String email);

    List<Teacher> findByDepartment(String department);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);
}
