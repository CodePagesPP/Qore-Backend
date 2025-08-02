package com.example.Qore.repository;

import com.example.Qore.DTO.ClientResponseDTO;
import com.example.Qore.DTO.UserDTO;
import com.example.Qore.model.Admin;
import com.example.Qore.model.Client;
import com.example.Qore.model.Role;
import com.example.Qore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {
    boolean existsByEmail(String email);
    List<UserDTO> findByRole(Role role);
    Optional<User> findByEmail(String email);
}

