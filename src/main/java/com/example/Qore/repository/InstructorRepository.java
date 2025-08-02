package com.example.Qore.repository;

import com.example.Qore.DTO.InstructorRegisterDTO;
import com.example.Qore.DTO.InstructorResponseDTO;
import com.example.Qore.model.Instructor;
import com.example.Qore.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
    List<InstructorResponseDTO> findByRole(Role role);
    Optional<Instructor> findByDni(String dni);
    Optional<Instructor> findByEmail(String email);
    String dni(String dni);
}
