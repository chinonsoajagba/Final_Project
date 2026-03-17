package com.chinonso.university_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "class_teacher")
public class ClassTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_teacher_id")
    private Integer classTeacherId;

    @NotNull(message = "Class is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassSection classSection;

    @NotNull(message = "Teacher is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private TeacherRole role = TeacherRole.LECTURER;

    public enum TeacherRole {
        LECTURER, TEACHING_ASSISTANT
    }

    public ClassTeacher() {
    }

    public ClassTeacher(ClassSection classSection, Teacher teacher, TeacherRole role) {
        this.classSection = classSection;
        this.teacher = teacher;
        this.role = role;
    }

    public Integer getClassTeacherId() {
        return classTeacherId;
    }

    public void setClassTeacherId(Integer classTeacherId) {
        this.classTeacherId = classTeacherId;
    }

    public ClassSection getClassSection() {
        return classSection;
    }

    public void setClassSection(ClassSection classSection) {
        this.classSection = classSection;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public TeacherRole getRole() {
        return role;
    }

    public void setRole(TeacherRole role) {
        this.role = role;
    }
}
