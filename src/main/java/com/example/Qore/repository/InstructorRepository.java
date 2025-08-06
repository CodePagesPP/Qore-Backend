package com.example.Qore.repository;

import com.example.Qore.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByEmail(String email);

    @Query("SELECT i FROM Instructor i WHERE i.dni = :dni AND i.role.name = 'INSTRUCTOR'")
    Optional<Instructor> findInstructorByDni(@Param("dni") String dni);

    @Query("SELECT i FROM Instructor i WHERE i.dni = :dni AND i.role.name = 'INSTRUCTOR'")
    Optional<Instructor> findInstructorByDniAndRole(@Param("dni") String dni);

    @Query("SELECT i FROM Instructor i WHERE i.role.name = :roleName")
    List<Instructor> findInstructorByRoleName(@Param("roleName") String roleName);
}
