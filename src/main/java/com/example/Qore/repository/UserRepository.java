package com.example.Qore.repository;

import com.example.Qore.model.Role;
import com.example.Qore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    Optional<User> findByEmail(String email);
}

