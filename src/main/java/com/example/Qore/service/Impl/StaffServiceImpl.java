package com.example.Qore.service.Impl;

import com.example.Qore.DTO.StaffRegisterDTO;
import com.example.Qore.DTO.StaffResponseDTO;
import com.example.Qore.DTO.StaffUpdateDTO;
import com.example.Qore.model.RoleE;
import com.example.Qore.model.Staff;
import com.example.Qore.repository.RoleRepository;
import com.example.Qore.repository.StaffRepository;
import com.example.Qore.service.StaffService;
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
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public StaffResponseDTO registerStaff(StaffRegisterDTO staff) {
        if(staffRepository.findByEmail(staff.getEmail()).isPresent()){
            throw new EntityExistsException("User with this email already exists");
        };

        RoleE staffRole = roleRepository.findByName("STAFF")
                .orElseThrow(() -> new EntityExistsException("Staff Role not found"));

        Staff staffCreate = Staff.builder()
                .name(staff.getName())
                .lastName(staff.getLastName())
                .email(staff.getEmail())
                .password(passwordEncoder.encode(staff.getPassword()))
                .sex(staff.getSex())
                .phoneNumber(staff.getPhoneNumber())
                .dni(staff.getDni())
                .birthday(staff.getBirthday())
                .country(staff.getAddress())
                .city(staff.getCity())
                .address(staff.getAddress())
                .role(staffRole)
                .area(staff.getArea())
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        return mapToDTO(staffRepository.save(staffCreate));
    }

    @Override
    public List<StaffResponseDTO> getAllStaff() {
        return staffRepository.findStaffByRoleName("STAFF").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StaffResponseDTO updateStaff(String dni, StaffUpdateDTO staff) {
        Staff staffFound = staffRepository.findStaffByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found or is not an Staff"));

        if(staff.getName() != null) staffFound.setName(staff.getName());
        if(staff.getLastName() != null) staffFound.setLastName(staff.getLastName());
        if(staff.getEmail() != null) staffFound.setEmail(staff.getEmail());
        if(staff.getPassword() != null) staffFound.setPassword(passwordEncoder.encode(staff.getPassword()));
        if(staff.getSex() != null) staffFound.setSex(staff.getSex());
        if(staff.getPhoneNumber()!= null) staffFound.setPhoneNumber(staff.getPhoneNumber());
        if(staff.getDni() != null) staffFound.setDni(staff.getDni());
        if(staff.getBirthday() != null) staffFound.setBirthday(staff.getBirthday());
        if(staff.getCountry() != null) staffFound.setAddress(staff.getAddress());
        if(staff.getCity() != null) staffFound.setCity(staff.getCity());
        if(staff.getAddress() != null) staffFound.setAddress(staff.getAddress());
        if(staff.getArea() != null) staffFound.setArea(staff.getArea());
        staffFound.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        return mapToDTO(staffRepository.save(staffFound));
    }

    @Override
    public void deleteStaff(String dni) {
        Staff staffFound = staffRepository.findStaffByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));
        staffRepository.delete(staffFound);
    }

    private StaffResponseDTO mapToDTO(Staff staff){
        return StaffResponseDTO.builder()
                .dni(staff.getDni())
                .email(staff.getEmail())
                .role(staff.getRole())
                .id(staff.getId())
                .name(staff.getName())
                .sex(staff.getSex())
                .lastName(staff.getLastName())
                .address(staff.getAddress())
                .phoneNumber(staff.getPhoneNumber())
                .birthday(staff.getBirthday())
                .country(staff.getAddress())
                .city(staff.getCity())
                .area(staff.getArea())
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .build();
    }
}
