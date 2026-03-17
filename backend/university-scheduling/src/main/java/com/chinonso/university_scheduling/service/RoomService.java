package com.chinonso.university_scheduling.service;

import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.Room;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.RoomRepository;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // GET ALL
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // GET ACTIVE ONLY (for dropdowns in frontend)
    public List<Room> getActiveRooms() {
        return roomRepository.findByIsActiveTrue();
    }

    // GET BY ID
    public Room getRoomById(Integer id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + id));
    }

    // CREATE
    public Room createRoom(Room room) {
        if (roomRepository.existsByRoomCode(room.getRoomCode())) {
            throw new IllegalArgumentException(
                    "Room code '" + room.getRoomCode() + "' already exists");
        }
        return roomRepository.save(room);
    }

    // UPDATE
    public Room updateRoom(Integer id, Room updatedRoom) {
        Room existing = getRoomById(id);

        // Only check code uniqueness if it actually changed
        if (!existing.getRoomCode().equals(updatedRoom.getRoomCode())
                && roomRepository.existsByRoomCode(updatedRoom.getRoomCode())) {
            throw new IllegalArgumentException(
                    "Room code '" + updatedRoom.getRoomCode() + "' already exists");
        }

        existing.setRoomCode(updatedRoom.getRoomCode());
        existing.setBuilding(updatedRoom.getBuilding());
        existing.setCapacity(updatedRoom.getCapacity());
        existing.setHasProjector(updatedRoom.getHasProjector());
        existing.setHasComputers(updatedRoom.getHasComputers());
        existing.setRoomType(updatedRoom.getRoomType());
        existing.setIsActive(updatedRoom.getIsActive());

        return roomRepository.save(existing);
    }

    // DELETE
    public void deleteRoom(Integer id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }

    // GET ROOMS BY MINIMUM CAPACITY
    public List<Room> getRoomsByMinCapacity(Integer capacity) {
        return roomRepository.findByCapacityGreaterThanEqual(capacity);
    }
}
