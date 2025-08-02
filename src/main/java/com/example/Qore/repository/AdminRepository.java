package com.example.Qore.repository;

import com.example.Qore.DTO.ClientResponseDTO;
import com.example.Qore.DTO.UserDTO;
import com.example.Qore.model.Admin;
import com.example.Qore.model.Client;
import com.example.Qore.model.Role;
import com.example.Qore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {
    boolean existsByEmail(String email);
    List<UserDTO> findByRole(Role role);
    Optional<User> findByEmail(String email);

    @Query("SELECT c FROM Admin c WHERE c.id = :id AND c.role.name = 'ADMIN'")
    Optional<Admin> findAdminById(@Param("id") long id);

    @Query("SELECT c FROM Admin c WHERE c.id = :id AND c.role.name = 'ADMIN'")
    Optional<Admin> findAdminByIdAndRole(@Param("dni") String dni);

    @Query("SELECT c FROM Admin c WHERE c.role.name = :roleName")
    List<Admin> findAdminByRoleName(@Param("roleName") String role);
}

