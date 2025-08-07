package com.example.Qore.controller;

import com.example.Qore.DTO.RoomRequestDTO;
import com.example.Qore.DTO.RoomResponseDTO;
import com.example.Qore.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponseDTO> createRoom(@RequestBody RoomRequestDTO dto) {
        return new ResponseEntity<>(roomService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> updateRoom(@PathVariable Long id,
                                                      @RequestBody RoomRequestDTO dto) {
        return ResponseEntity.ok(roomService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAll());
    }
}
