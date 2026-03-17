package com.chinonso.university_scheduling.service;

import com.chinonso.university_scheduling.entity.Room;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // ── READ ───────────────────────────────────────────────────────────────────
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public Room findById(Integer id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    public Room findByRoomCode(String roomCode) {
        return roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with code: " + roomCode));
    }

    public List<Room> findByBuilding(String building) {
        return roomRepository.findByBuilding(building);
    }

    public List<Room> findActiveRooms() {
        return roomRepository.findByIsActive(true);
    }

    public List<Room> findByType(Room.RoomType roomType) {
        return roomRepository.findByRoomType(roomType);
    }

    public List<Room> findByMinCapacity(Integer minCapacity) {
        return roomRepository.findByCapacityGreaterThanEqual(minCapacity);
    }

    // ── CREATE ─────────────────────────────────────────────────────────────────
    public Room create(Room room) {
        if (roomRepository.existsByRoomCode(room.getRoomCode())) {
            throw new IllegalArgumentException("Room code already exists: " + room.getRoomCode());
        }
        return roomRepository.save(room);
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────────
    public Room update(Integer id, Room updated) {
        Room existing = findById(id);

        // Check uniqueness if the room code has changed
        if (!existing.getRoomCode().equals(updated.getRoomCode())
                && roomRepository.existsByRoomCode(updated.getRoomCode())) {
            throw new IllegalArgumentException("Room code already exists: " + updated.getRoomCode());
        }

        existing.setRoomCode(updated.getRoomCode());
        existing.setBuilding(updated.getBuilding());
        existing.setCapacity(updated.getCapacity());
        existing.setHasProjector(updated.getHasProjector());
        existing.setHasComputers(updated.getHasComputers());
        existing.setRoomType(updated.getRoomType());
        existing.setIsActive(updated.getIsActive());

        return roomRepository.save(existing);
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────
    public void delete(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
    }
}
