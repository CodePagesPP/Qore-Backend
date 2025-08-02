package com.example.Qore.service.Impl;

import com.example.Qore.DTO.AdminDTO;
import com.example.Qore.DTO.UserDTO;
import com.example.Qore.model.*;
import com.example.Qore.repository.AdminRepository;
import com.example.Qore.repository.RoleRepository;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {
private final AdminRepository userRepository;
private final PasswordEncoder passwordEncoder;
private final RoleRepository roleRepository;
    @Override
    public UserDTO registerAdmin(AdminDTO admin) {
        if(userRepository.findByEmail(admin.getEmail()).isPresent()){
            throw new EntityExistsException("User with this email already exists");
        };

        RoleE adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no existe en la base de datos"));
        System.out.println("ROL ENCONTRADO: " + adminRole.getName());


        Admin user = Admin.builder()
                .email(admin.getEmail())
                .password(passwordEncoder.encode(admin.getPassword()))
                .role(adminRole)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        System.out.println(user.getRole());
        return mapToDTO(userRepository.save(user));
    }

    @Override
    public List<UserDTO> getAllAdmins() {
        return userRepository.findAdminByRoleName("ADMIN").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateAdmin(long id, AdminDTO admin) {

        Admin adminFound = userRepository.findAdminById(id)
                .orElseThrow(() -> new EntityNotFoundException("ADMIN not found or is not a ADMIN"));
        //Por si se quiere actualizar solo el email o la password
        if(admin.getEmail() != null){
            adminFound.setEmail(admin.getEmail());
        }

        if(admin.getPassword() != null){
            adminFound.setPassword(passwordEncoder.encode(admin.getPassword()));
        }

        adminFound.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return mapToDTO(userRepository.save(adminFound));
    }

    @Override
    public void deleteAdmin(long id) {
        Admin adminFound = userRepository.findAdminById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found or is not a ADMIN"));
        userRepository.delete(adminFound);
    }

    private UserDTO mapToDTO(Admin user){
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


}
