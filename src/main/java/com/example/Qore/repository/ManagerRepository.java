package com.example.Qore.repository;

import com.example.Qore.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager,Long> {
    boolean existsByEmail(String email);
    Optional<Manager> findByEmail(String email);

    @Query("SELECT c FROM Manager c WHERE c.dni = :dni AND c.role.name = 'MANAGER'")
    Optional<Manager> findManagerByDni(@Param("dni") String dni);

    @Query("SELECT c FROM Manager c WHERE c.dni = :dni AND c.role.name = 'MANAGER'")
    Optional<Manager> findManagerByDniAndRole(@Param("dni") String dni);

    @Query("SELECT c FROM Manager c WHERE c.role.name = :roleName")
    List<Manager> findManagerByRoleName(@Param("roleName") String roleName);
}
