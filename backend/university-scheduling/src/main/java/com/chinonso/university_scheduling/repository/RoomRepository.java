package com.chinonso.university_scheduling.repository;

import com.chinonso.university_scheduling.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    Optional<Room> findByRoomCode(String roomCode);

    List<Room> findByBuilding(String building);

    List<Room> findByIsActive(Boolean isActive);

    List<Room> findByRoomType(Room.RoomType roomType);

    List<Room> findByCapacityGreaterThanEqual(Integer minCapacity);

    boolean existsByRoomCode(String roomCode);
}
