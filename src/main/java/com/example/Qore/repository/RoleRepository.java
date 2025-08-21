package com.example.Qore.repository;

import com.example.Qore.DTO.RoleDTO;
import com.example.Qore.model.Client;
import com.example.Qore.model.RoleE;
import com.example.Qore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleE, Long> {
    Optional<RoleE> findByName(String name);
    Optional<RoleE> findById(long id);
    @Query("SELECT u FROM RoleE u WHERE u.name <> 'CLIENT'")
    List<RoleE> findAllNoRoleClient();
}

