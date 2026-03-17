package com.chinonso.university_scheduling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chinonso.university_scheduling.entity.Room;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    // Check if room code already exists (for duplicate validation)
    boolean existsByRoomCode(String roomCode);

    // Find room by its code e.g. "SCI-101"
    Optional<Room> findByRoomCode(String roomCode);

    // Get only active rooms (for dropdowns in frontend)
    List<Room> findByIsActiveTrue();

    // Find rooms that can fit a certain number of students
    List<Room> findByCapacityGreaterThanEqual(Integer capacity);
}
