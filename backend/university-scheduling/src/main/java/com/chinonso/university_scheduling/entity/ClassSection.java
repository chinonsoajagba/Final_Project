package com.chinonso.university_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Builder.Default
    @Column(name = "current_enrolment", nullable = false)
    private Integer currentEnrolment = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClassStatus status = ClassStatus.SCHEDULED;

    public enum Semester {
        FALL, SPRING, SUMMER
    }

    public enum ClassStatus {
        SCHEDULED, ONGOING, COMPLETED
    }
}
