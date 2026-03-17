package com.chinonso.university_scheduling.repository;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.Enrolment;
import com.chinonso.university_scheduling.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrolmentRepository extends JpaRepository<Enrolment, Integer> {

    List<Enrolment> findByStudent(Student student);

    List<Enrolment> findByClassSection(ClassSection classSection);

    Optional<Enrolment> findByStudentAndClassSection(Student student, ClassSection classSection);

    boolean existsByStudentAndClassSection(Student student, ClassSection classSection);

    long countByClassSectionAndStatus(ClassSection classSection, Enrolment.EnrolmentStatus status);

    List<Enrolment> findByStatus(Enrolment.EnrolmentStatus status);
}
