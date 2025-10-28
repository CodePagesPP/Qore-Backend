package com.example.Qore.repository;

import com.example.Qore.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long> {
    boolean existsByEmail(String email);

    Optional<Client> findByEmail(String email);


    @Query("SELECT c FROM Client c WHERE c.dni = :dni AND c.role.name = 'CLIENT' AND c.active = true")
    Optional<Client> findActiveClientByDni(@Param("dni") String dni);

    @Query("SELECT c FROM Client c WHERE c.dni = :dni AND c.role.name = 'CLIENT'")
    Optional<Client> findClientByDniAndRole(@Param("dni") String dni);

    @Query("SELECT c FROM Client c WHERE c.role.name = :roleName AND c.active = true ORDER BY c.name ASC, c.lastName ASC")
    List<Client> findActiveClientsByRoleName(@Param("roleName") String roleName);

    boolean existsByDni(String dni);

    List<Client> findBySubscriptionEndIsNotNull();


    @Query("SELECT c FROM Client c WHERE MONTH(c.createdAt) = :month AND YEAR(c.createdAt) = :year")
    List<Client> findClientsRegisteredInMonth(@Param("month") int month, @Param("year") int year);


    @Query("SELECT YEAR(c.createdAt) as year, MONTH(c.createdAt) as month, COUNT(c) as total " +
            "FROM Client c GROUP BY YEAR(c.createdAt), MONTH(c.createdAt) ORDER BY year DESC, month DESC")
    List<Object[]> countClientsByMonth();

    List<Client> findBySubscriptionEndBefore(LocalDate date);

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.plan")
    List<Client> findAllWithPlan();

}
