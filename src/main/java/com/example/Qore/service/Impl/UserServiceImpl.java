package com.example.Qore.service.Impl;

import com.example.Qore.DTO.AdminDTO;
import com.example.Qore.DTO.UserDTO;
import com.example.Qore.model.Admin;
import com.example.Qore.model.Role;
import com.example.Qore.model.User;
import com.example.Qore.repository.AdminRepository;
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
    @Override
    public UserDTO registerAdmin(AdminDTO admin) {
        if(userRepository.findByEmail(admin.getEmail()).isPresent()){
            throw new EntityExistsException("User with this email already exists");
        };
        Admin user = Admin.builder()
                .email(admin.getEmail())
                .password(passwordEncoder.encode(admin.getPassword()))
                .role(Role.ADMIN)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        return mapToDTO(userRepository.save(user));
    }

    @Override
    public List<UserDTO> getAllAdmins() {
        return userRepository.findAll().stream().filter(user -> user.getRole()== Role.ADMIN).map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO updateAdmin(long id, AdminDTO admin) {

        Admin adminFound = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with this id does not exist"));
        if(adminFound.getRole() != Role.ADMIN){
            throw new IllegalArgumentException("User is not an admin");
        }
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
        Admin adminFound = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with this id does not exist"));
        if(adminFound.getRole() != Role.ADMIN){
            throw new IllegalArgumentException("User is not an admin");
        }
        userRepository.delete(adminFound);
    }

    private UserDTO mapToDTO(Admin user){
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


}
