package com.example.Qore.repository;

import com.example.Qore.DTO.ClientResponseDTO;
import com.example.Qore.DTO.UserDTO;
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
public interface ClientRepository extends JpaRepository<Client,Long> {
    boolean existsByEmail(String email);
    List<ClientResponseDTO> findByRole(Role role);
    Optional<Client> findByEmail(String email);


    @Query("SELECT c FROM Client c WHERE c.dni = :dni AND c.role.name = 'CLIENT' AND c.active = true")
    Optional<Client> findActiveClientByDni(@Param("dni") String dni);

    @Query("SELECT c FROM Client c WHERE c.dni = :dni AND c.role.name = 'CLIENT'")
    Optional<Client> findClientByDniAndRole(@Param("dni") String dni);

    @Query("SELECT c FROM Client c WHERE c.role.name = :roleName AND c.active = true")
    List<Client> findActiveClientsByRoleName(@Param("roleName") String roleName);

}
