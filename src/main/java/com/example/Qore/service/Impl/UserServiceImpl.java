package com.example.Qore.service.Impl;

import com.example.Qore.DTO.AdminDTO;
import com.example.Qore.DTO.UserDTO;
import com.example.Qore.model.User;
import com.example.Qore.repository.UserRepository;
import com.example.Qore.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
    @Override
    public UserDTO registerAdmin(AdminDTO admin) {
        if(userRepository.findByEmail(admin.getEmail()).isPresent()){
            throw new EntityExistsException("User with this email already exists");
        };
        User user = User.builder()
                .email(admin.getEmail())
                .password(passwordEncoder.encode(admin.getPassword()))
                .role(admin.getRole())
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        return mapToDTO(userRepository.save(user));
    }

    private UserDTO mapToDTO(User user){
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
