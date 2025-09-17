package com.example.Qore.repository;

import com.example.Qore.model.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    boolean existsByRoomId(Long roomId);
    List<ClassSession> findByClients_Id(Long clientId);

    @Query("SELECT COUNT(c) FROM ClassSession c " +
            "WHERE c.startDate BETWEEN :start AND :end")
    long countClassesBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
           SELECT COUNT(cs)
           FROM ClassSession cs
           JOIN cs.clients c
           WHERE c.id = :clientId
             AND cs.startDate >= :start
             AND cs.startDate <= :end
           """)
    long countClassesByClientAndPeriod(@Param("clientId") Long clientId,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);
}
