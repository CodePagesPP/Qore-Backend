package com.example.Qore.repository;

import com.example.Qore.DTO.ManagerResponseDTO;
import com.example.Qore.model.Client;
import com.example.Qore.model.Manager;
import com.example.Qore.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager,Long> {
    boolean existsByEmail(String email);
    List<ManagerResponseDTO> findByRole(Role role);
    Optional<Manager> findByEmail(String email);
    Optional<Manager> findByDni(String dni);

    @Query("SELECT c FROM Manager c WHERE c.dni = :dni AND c.role.name = 'MANAGER'")
    Optional<Manager> findManagerByDni(@Param("dni") String dni);

    @Query("SELECT c FROM Manager c WHERE c.dni = :dni AND c.role.name = 'MANAGER'")
    Optional<Manager> findManagerByDniAndRole(@Param("dni") String dni);

    @Query("SELECT c FROM Manager c WHERE c.role.name = :roleName")
    List<Manager> findManagerByRoleName(@Param("roleName") String roleName);
}
