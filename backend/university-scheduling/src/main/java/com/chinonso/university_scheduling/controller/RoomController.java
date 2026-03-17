package com.chinonso.university_scheduling.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chinonso.university_scheduling.entity.Room;
import com.chinonso.university_scheduling.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // GET /api/rooms — get all rooms
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    // GET /api/rooms/active — get only active rooms (for dropdowns)
    @GetMapping("/active")
    public ResponseEntity<List<Room>> getActiveRooms() {
        return ResponseEntity.ok(roomService.getActiveRooms());
    }

    // GET /api/rooms/{id} — get single room
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    // GET /api/rooms/capacity/{min} — get rooms with minimum capacity
    @GetMapping("/capacity/{min}")
    public ResponseEntity<List<Room>> getRoomsByCapacity(@PathVariable Integer min) {
        return ResponseEntity.ok(roomService.getRoomsByMinCapacity(min));
    }

    // POST /api/rooms — create new room
    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        Room created = roomService.createRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/rooms/{id} — update room
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable Integer id,
            @Valid @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    // DELETE /api/rooms/{id} — delete room
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}