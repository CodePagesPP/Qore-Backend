package com.example.Qore.service;

import com.example.Qore.DTO.AdminDTO;
import com.example.Qore.DTO.UserDTO;

public interface UserService {
    UserDTO registerAdmin(AdminDTO admin);
}
