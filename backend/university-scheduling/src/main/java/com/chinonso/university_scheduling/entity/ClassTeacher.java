package com.chinonso.university_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private TeacherRole role = TeacherRole.LECTURER;

    public enum TeacherRole {
        LECTURER, TEACHING_ASSISTANT
    }

    public ClassTeacher(ClassSection classSection, Teacher teacher, TeacherRole role) {
        this.classSection = classSection;
        this.teacher = teacher;
        this.role = role;
    }
}
