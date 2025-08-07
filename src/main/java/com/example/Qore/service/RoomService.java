package com.example.Qore.service;

import com.example.Qore.DTO.RoomRequestDTO;
import com.example.Qore.DTO.RoomResponseDTO;

import java.util.List;

public interface RoomService {
    RoomResponseDTO create(RoomRequestDTO dto);
    RoomResponseDTO update(Long id, RoomRequestDTO dto);
    void delete(Long id);
    RoomResponseDTO getById(Long id);
    List<RoomResponseDTO> getAll();
}
