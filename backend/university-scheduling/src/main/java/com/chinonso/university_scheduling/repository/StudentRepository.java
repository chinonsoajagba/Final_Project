package com.chinonso.university_scheduling.repository;

import com.chinonso.university_scheduling.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    Optional<Student> findByEmail(String email);

    List<Student> findByYearOfStudy(Integer yearOfStudy);

    List<Student> findByProgram(String program);

    List<Student> findByEnrolmentStatus(Student.EnrolmentStatus enrolmentStatus);

    boolean existsByEmail(String email);
}
