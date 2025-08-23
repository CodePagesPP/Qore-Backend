package com.example.Qore.service.Impl;

import com.example.Qore.DTO.RoomRequestDTO;
import com.example.Qore.DTO.RoomResponseDTO;
import com.example.Qore.model.Room;
import com.example.Qore.repository.ClassSessionRepository;
import com.example.Qore.repository.RoomRepository;
import com.example.Qore.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ClassSessionRepository classSessionRepository;

    @Override
    public RoomResponseDTO create(RoomRequestDTO dto) {
        if (roomRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("La sala ya existe");
        }

        Room room = Room.builder()
                .name(dto.getName())
                .build();

        return mapToDTO(roomRepository.save(room));
    }

    @Override
    public RoomResponseDTO update(Long id, RoomRequestDTO dto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        room.setName(dto.getName());
        return mapToDTO(roomRepository.save(room));
    }

    @Override
    public void delete(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Sala no encontrada");
        }

        if (classSessionRepository.existsByRoomId(id)) {
            throw new IllegalStateException("No se puede eliminar la sala porque está asignada a clases.");
        }
        roomRepository.deleteById(id);
    }

    @Override
    public RoomResponseDTO getById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        return mapToDTO(room);
    }

    @Override
    public List<RoomResponseDTO> getAll() {
        return roomRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private RoomResponseDTO mapToDTO(Room room) {
        return RoomResponseDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .build();
    }
}
