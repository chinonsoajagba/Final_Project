package com.chinonso.university_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @NotBlank(message = "Room code is required")
    @Column(name = "room_code", nullable = false, unique = true, length = 20)
    private String roomCode;

    @NotBlank(message = "Building name is required")
    @Column(name = "building", nullable = false, length = 50)
    private String building;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be greater than 0")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Builder.Default
    @Column(name = "has_projector", nullable = false)
    private Boolean hasProjector = false;

    @Builder.Default
    @Column(name = "has_computers", nullable = false)
    private Boolean hasComputers = false;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public enum RoomType {
        LECTURE_HALL, LAB, SEMINAR_ROOM
    }
}
