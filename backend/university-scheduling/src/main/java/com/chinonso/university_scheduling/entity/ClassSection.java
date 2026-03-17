package com.chinonso.university_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "class")
public class ClassSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Integer classId;

    @NotNull(message = "Course is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotBlank(message = "Section number is required")
    @Column(name = "section_number", nullable = false, length = 10)
    private String sectionNumber;

    @NotBlank(message = "Academic year is required")
    @Column(name = "academic_year", nullable = false, length = 9)
    private String academicYear;

    @NotNull(message = "Semester is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "semester", nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;

    @NotNull(message = "Max enrolment is required")
    @Min(value = 1, message = "Max enrolment must be at least 1")
    @Column(name = "max_enrolment", nullable = false)
    private Integer maxEnrolment;

    @Column(name = "current_enrolment", nullable = false)
    private Integer currentEnrolment = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClassStatus status = ClassStatus.SCHEDULED;

    public enum Semester {
        FALL, SPRING, SUMMER
    }

    public enum ClassStatus {
        SCHEDULED, ONGOING, COMPLETED
    }

    public ClassSection() {
    }

    public ClassSection(Course course, String sectionNumber, String academicYear,
            Semester semester, Room room, Integer maxEnrolment) {
        this.course = course;
        this.sectionNumber = sectionNumber;
        this.academicYear = academicYear;
        this.semester = semester;
        this.room = room;
        this.maxEnrolment = maxEnrolment;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(String sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Integer getMaxEnrolment() {
        return maxEnrolment;
    }

    public void setMaxEnrolment(Integer maxEnrolment) {
        this.maxEnrolment = maxEnrolment;
    }

    public Integer getCurrentEnrolment() {
        return currentEnrolment;
    }

    public void setCurrentEnrolment(Integer currentEnrolment) {
        this.currentEnrolment = currentEnrolment;
    }

    public ClassStatus getStatus() {
        return status;
    }

    public void setStatus(ClassStatus status) {
        this.status = status;
    }
}
