package com.example.Qore.service.Impl;

import com.example.Qore.DTO.*;
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
private final UserRepository userRepository2;
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

    public UserResponseDTO registerWorker(UserRegisterDTO dto) {


        if (userRepository2.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        if(userRepository2.existsByDni(dto.getDni())){
            throw new IllegalArgumentException("El dni ya esta en uso");
        }


        RoleE userRole = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("El id de este rol no existe en la base de datos"));


        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .dni(dto.getDni())
                .sex(dto.getSex())
                .country(dto.getCountry())
                .city(dto.getCity())
                .birthday(dto.getBirthday())
                .role(userRole)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        User savedUser = userRepository2.save(user);
        return mapToDTO(savedUser);
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


    public List<UserResponseDTO> getAllNonClients() {
        return userRepository2.findAllNonClients().stream()
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .dni(user.getDni())
                        .role(user.getRole().getName())
                        .createdAt(user.getCreatedAt())
                        .sex(user.getSex())
                        .birthday(user.getBirthday())
                        .phoneNumber(user.getPhoneNumber())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
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

    private UserResponseDTO mapToDTO(User dto){
        return UserResponseDTO.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .name(dto.getName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .dni(dto.getDni())
                .sex(dto.getSex())
                .country(dto.getCountry())
                .city(dto.getCity())
                .birthday(dto.getBirthday())
                .role(dto.getRole().getName())
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }
}
