package com.example.Qore.service;

import com.example.Qore.DTO.*;

import java.util.List;

public interface ManagerService {
    ManagerResponseDTO registerManager(ManagerRegisterDTO dto);
    List<ManagerResponseDTO> getAllManagers();
    ManagerResponseDTO getManagerByDni(String dni);
    ManagerResponseDTO updateManager(String dni, ManagerUpdateDTO dto);
    void deleteManager(String id);
}
