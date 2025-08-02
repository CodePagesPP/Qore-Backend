package com.example.Qore.service.Impl;

import com.example.Qore.DTO.ManagerRegisterDTO;
import com.example.Qore.DTO.ManagerResponseDTO;
import com.example.Qore.DTO.ManagerUpdateDTO;
import com.example.Qore.model.Admin;
import com.example.Qore.model.Client;
import com.example.Qore.model.Manager;
import com.example.Qore.model.Role;
import com.example.Qore.repository.ManagerRepository;
import com.example.Qore.service.ManagerService;
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
public class ManagerServiceImpl implements ManagerService {
    private final ManagerRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public ManagerResponseDTO registerManager(ManagerRegisterDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        Manager user = Manager.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .dni(dto.getDni())
                .sex(dto.getSex())
                .country(dto.getCountry())
                .birthday(dto.getBirthday())
                .role(Role.MANAGER)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .active(true)
                .build();

        Manager savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    @Override
    public List<ManagerResponseDTO> getAllManagers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole()== Role.MANAGER)
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public ManagerResponseDTO getManagerByDni(String dni) {
        Manager user = userRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Manager not found"));

        if (user.getRole() != Role.MANAGER || !user.isActive()) {
            throw new IllegalArgumentException("User is not an active MANAGER");
        }

        return mapToDTO(user);
    }

    @Override
    public ManagerResponseDTO updateManager(String dni, ManagerUpdateDTO dto) {
        Manager user = userRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Manager not found"));

        if (user.getRole() != Role.MANAGER) {
            throw new IllegalArgumentException("User is not a MANAGER");
        }

        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getBirthday() != null) user.setBirthday(dto.getBirthday());
        if (dto.getSex() != null) user.setSex(dto.getSex());
        if (dto.getCountry() != null) user.setCountry(dto.getCountry());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getDni() != null) user.setDni(dto.getDni());

        return mapToDTO(userRepository.save(user));
    }

    @Override
    public void disableManager(String dni) {
        Manager adminFound = userRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Manager with this dni does not exist"));

        if(adminFound.getRole() != Role.ADMIN){
            throw new IllegalArgumentException("User is not an Manager");
        }
        userRepository.delete(adminFound);
    }

    private ManagerResponseDTO mapToDTO(Manager user){
        return ManagerResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .birthday(user.getBirthday())
                .dni(user.getDni())
                .country(user.getCountry())
                .sex(user.getSex())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
