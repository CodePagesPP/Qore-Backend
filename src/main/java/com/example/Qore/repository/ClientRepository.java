package com.example.Qore.repository;

import com.example.Qore.DTO.ClientResponseDTO;
import com.example.Qore.DTO.UserDTO;
import com.example.Qore.model.Client;
import com.example.Qore.model.Role;
import com.example.Qore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long> {
    boolean existsByEmail(String email);
    List<ClientResponseDTO> findByRole(Role role);
    Optional<Client> findByEmail(String email);
    Optional<Client> findByDni(String dni);
}
