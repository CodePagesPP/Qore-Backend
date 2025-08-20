package com.example.Qore.repository;

import com.example.Qore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.role.name <> 'CLIENT'")
    List<User> findAllNonClients();
}

