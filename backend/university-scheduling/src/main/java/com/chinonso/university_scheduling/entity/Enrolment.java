package com.chinonso.university_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "enrolment")
public class Enrolment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrolment_id")
    private Integer enrolmentId;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Class is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassSection classSection;

    @NotNull(message = "Enrolment date is required")
    @Column(name = "enrolment_date", nullable = false)
    private LocalDate enrolmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrolmentStatus status = EnrolmentStatus.ENROLLED;

    @Column(name = "grade", length = 2)
    private String grade;

    public enum EnrolmentStatus {
        ENROLLED, DROPPED, COMPLETED
    }

    public Enrolment() {
    }

    public Enrolment(Student student, ClassSection classSection, LocalDate enrolmentDate) {
        this.student = student;
        this.classSection = classSection;
        this.enrolmentDate = enrolmentDate;
    }

    public Integer getEnrolmentId() {
        return enrolmentId;
    }

    public void setEnrolmentId(Integer enrolmentId) {
        this.enrolmentId = enrolmentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public ClassSection getClassSection() {
        return classSection;
    }

    public void setClassSection(ClassSection classSection) {
        this.classSection = classSection;
    }

    public LocalDate getEnrolmentDate() {
        return enrolmentDate;
    }

    public void setEnrolmentDate(LocalDate enrolmentDate) {
        this.enrolmentDate = enrolmentDate;
    }

    public EnrolmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrolmentStatus status) {
        this.status = status;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
