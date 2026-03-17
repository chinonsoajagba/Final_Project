package com.chinonso.university_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    @Column(name = "has_projector", nullable = false)
    private Boolean hasProjector = false;

    @Column(name = "has_computers", nullable = false)
    private Boolean hasComputers = false;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public enum RoomType {
        LECTURE_HALL, LAB, SEMINAR_ROOM
    }

    public Room() {
    }

    public Room(String roomCode, String building, Integer capacity,
            Boolean hasProjector, Boolean hasComputers, RoomType roomType) {
        this.roomCode = roomCode;
        this.building = building;
        this.capacity = capacity;
        this.hasProjector = hasProjector;
        this.hasComputers = hasComputers;
        this.roomType = roomType;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getHasProjector() {
        return hasProjector;
    }

    public void setHasProjector(Boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public Boolean getHasComputers() {
        return hasComputers;
    }

    public void setHasComputers(Boolean hasComputers) {
        this.hasComputers = hasComputers;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
