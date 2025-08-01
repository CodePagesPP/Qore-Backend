package com.example.Qore.service.Impl;

import com.example.Qore.DTO.ClientRegisterDTO;
import com.example.Qore.DTO.ClientResponseDTO;
import com.example.Qore.DTO.ClientUpdateDTO;
import com.example.Qore.model.Client;
import com.example.Qore.model.Role;
import com.example.Qore.repository.ClientRepository;
import com.example.Qore.service.ClientService;
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
public class ClientServiceImpl implements ClientService {
    private final ClientRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public ClientResponseDTO registerClient(ClientRegisterDTO dto) {


        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }


        Client user = Client.builder()
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
                .role(Role.CLIENT)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .active(true)
                .build();

        Client savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    @Override
    public List<ClientResponseDTO> getAllActiveClients() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.CLIENT && user.isActive())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponseDTO getClientByDni(String dni) {
        Client user = userRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (user.getRole() != Role.CLIENT || !user.isActive()) {
            throw new IllegalArgumentException("User is not an active CLIENT");
        }

        return mapToDTO(user);
    }

    @Override
    public ClientResponseDTO updateClient(String dni, ClientUpdateDTO dto) {
        Client user = userRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (user.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("User is not a CLIENT");
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
    public void disableClient(String id) {
        Client user = userRepository.findByDni(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (user.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("User is not a CLIENT");
        }

        user.setActive(false);
        userRepository.save(user);
    }

    private ClientResponseDTO mapToDTO(Client user){
        return ClientResponseDTO.builder()
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
