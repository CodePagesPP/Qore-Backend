package com.example.Qore.service;

import com.example.Qore.DTO.*;

import java.util.List;

public interface UserService {
    UserDTO registerAdmin(AdminDTO admin);
    List<UserDTO> getAllAdmins();
    UserDTO updateAdmin(long id, AdminDTO admin);
    void deleteAdmin(long id);
    List<UserResponseDTO> getAllNonClients();
    UserResponseDTO getUserById(long id);
    UserResponseDTO registerWorker(UserRegisterDTO dto);
    void deleteWorker(long id);
    UserResponseDTO updateWorker(String dni, UserUpdateDTO dto);
    long countUsersNotAdminOrClient();
}
