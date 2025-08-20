package com.example.Qore.service;

import com.example.Qore.DTO.AdminDTO;
import com.example.Qore.DTO.UserDTO;
import com.example.Qore.DTO.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserDTO registerAdmin(AdminDTO admin);
    List<UserDTO> getAllAdmins();
    UserDTO updateAdmin(long id, AdminDTO admin);
    void deleteAdmin(long id);
    List<UserResponseDTO> getAllNonClients();
}
