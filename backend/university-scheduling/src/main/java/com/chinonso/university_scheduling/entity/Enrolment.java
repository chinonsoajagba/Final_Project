package com.chinonso.university_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrolmentStatus status = EnrolmentStatus.ENROLLED;

    @Column(name = "grade", length = 2)
    private String grade;

    public enum EnrolmentStatus {
        ENROLLED, DROPPED, COMPLETED
    }
}
