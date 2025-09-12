package com.example.Qore.repository;

import com.example.Qore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    @Query("SELECT s FROM User s WHERE s.dni = :dni")
    Optional<User> findWorkerByDni(@Param("dni") String dni);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserById(@Param("id") long id);

    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.role.name <> 'CLIENT'")
    List<User> findAllNonClients();
}

