package com.example.Qore.repository;

import com.example.Qore.DTO.InstructorRegisterDTO;
import com.example.Qore.DTO.InstructorResponseDTO;
import com.example.Qore.model.Instructor;
import com.example.Qore.model.Manager;
import com.example.Qore.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
    List<InstructorResponseDTO> findByRole(Role role);
    Optional<Instructor> findByDni(String dni);
    Optional<Instructor> findByEmail(String email);

    @Query("SELECT i FROM Instructor i WHERE i.dni = :dni AND i.role.name = 'INSTRUCTOR'")
    Optional<Instructor> findInstructorByDni(@Param("dni") String dni);

    @Query("SELECT i FROM Instructor i WHERE i.dni = :dni AND i.role.name = 'INSTRUCTOR'")
    Optional<Instructor> findInstructorByDniAndRole(@Param("dni") String dni);

    @Query("SELECT i FROM Instructor i WHERE i.role.name = :roleName")
    List<Instructor> findInstructorByRoleName(@Param("roleName") String roleName);
}
