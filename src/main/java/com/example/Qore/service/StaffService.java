package com.example.Qore.service;

import com.example.Qore.DTO.StaffRegisterDTO;
import com.example.Qore.DTO.StaffResponseDTO;
import com.example.Qore.DTO.StaffUpdateDTO;
import com.example.Qore.model.Staff;

import java.util.List;

public interface StaffService {
    StaffResponseDTO registerStaff(StaffRegisterDTO staff);
    List<StaffResponseDTO> getAllStaff();
    StaffResponseDTO updateStaff(String dni, StaffUpdateDTO staff);
    void deleteStaff(String dni);
}
