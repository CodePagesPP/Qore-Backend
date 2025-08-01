package com.example.Qore.repository;

import com.example.Qore.DTO.ManagerResponseDTO;
import com.example.Qore.model.Manager;
import com.example.Qore.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager,Long> {
    boolean existsByEmail(String email);
    List<ManagerResponseDTO> findByRole(Role role);
    Optional<Manager> findByEmail(String email);
    Optional<Manager> findByDni(String dni);
}
