package com.chinonso.university_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Program is required")
    @Column(name = "program", nullable = false, length = 100)
    private String program;

    @NotNull(message = "Year of study is required")
    @Min(value = 1, message = "Year of study must be at least 1")
    @Max(value = 4, message = "Year of study must not exceed 4")
    @Column(name = "year_of_study", nullable = false)
    private Integer yearOfStudy;

    @Enumerated(EnumType.STRING)
    @Column(name = "enrolment_status", nullable = false)
    private EnrolmentStatus enrolmentStatus = EnrolmentStatus.ACTIVE;

    public enum EnrolmentStatus {
        ACTIVE, GRADUATED, SUSPENDED
    }

    public Student() {
    }

    public Student(String firstName, String lastName, String email,
            String program, Integer yearOfStudy) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.program = program;
        this.yearOfStudy = yearOfStudy;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public EnrolmentStatus getEnrolmentStatus() {
        return enrolmentStatus;
    }

    public void setEnrolmentStatus(EnrolmentStatus enrolmentStatus) {
        this.enrolmentStatus = enrolmentStatus;
    }
}
