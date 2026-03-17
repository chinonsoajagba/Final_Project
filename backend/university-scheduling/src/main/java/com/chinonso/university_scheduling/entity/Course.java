package com.chinonso.university_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer courseId;

    @NotBlank(message = "Course code is required")
    @Column(name = "course_code", nullable = false, unique = true, length = 20)
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Column(name = "credits", nullable = false)
    private Integer credits;

    @NotBlank(message = "Department is required")
    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @NotNull(message = "Level is required")
    @Min(value = 1, message = "Level must be at least 1")
    @Max(value = 4, message = "Level must not exceed 4")
    @Column(name = "level", nullable = false)
    private Integer level;

    public Course() {
    }

    public Course(String courseCode, String courseName, Integer credits,
            String department, Integer level) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.department = department;
        this.level = level;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
