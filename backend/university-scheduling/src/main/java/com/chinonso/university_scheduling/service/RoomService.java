package com.chinonso.university_scheduling.service;

import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.Room;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.ClassSectionRepository;
import com.chinonso.university_scheduling.repository.RoomRepository;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ClassSectionRepository classSectionRepository;

    public RoomService(RoomRepository roomRepository,
                       ClassSectionRepository classSectionRepository) {
        this.roomRepository = roomRepository;
        this.classSectionRepository = classSectionRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getActiveRooms() {
        return roomRepository.findByIsActiveTrue();
    }

    public Room getRoomById(Integer id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + id));
    }

    public Room createRoom(Room room) {
        if (roomRepository.existsByRoomCode(room.getRoomCode())) {
            throw new IllegalArgumentException(
                    "Room code '" + room.getRoomCode() + "' already exists");
        }
        return roomRepository.save(room);
    }

    public Room updateRoom(Integer id, Room updatedRoom) {
        Room existing = getRoomById(id);

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

    public void deleteRoom(Integer id) {
        Room room = getRoomById(id);
        // Unlink any classes referencing this room before deleting
        classSectionRepository.findByRoom_RoomId(id).forEach(cs -> {
            cs.setRoom(null);
            classSectionRepository.save(cs);
        });
        roomRepository.delete(room);
    }

    public List<Room> getRoomsByMinCapacity(Integer capacity) {
        return roomRepository.findByCapacityGreaterThanEqual(capacity);
    }
}
