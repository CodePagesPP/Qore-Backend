package com.example.Qore.repository;

import com.example.Qore.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff,Integer> {

    Optional<Staff> findByEmail(String email);

    @Query("SELECT s FROM Staff s WHERE s.dni = :dni AND s.role.name = 'STAFF'")
    Optional<Staff> findStaffByDni(@Param("dni") String dni);

    @Query("SELECT s FROM Staff s WHERE s.dni = :dni AND s.role.name = 'STAFF'")
    Optional<Staff> findStaffByDniAndRole(@Param("dni") String dni);

    @Query("SELECT s FROM Staff s WHERE s.role.name = :roleName")
    List<Staff> findStaffByRoleName(@Param("roleName") String roleName);
}
