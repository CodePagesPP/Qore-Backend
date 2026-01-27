package com.example.Qore.repository;

import com.example.Qore.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByClassSessionId(Long classId);
    Optional<Attendance> findByClassSessionIdAndClientIdAndDate(Long classId, Long clientId, LocalDate date);
    @Query("SELECT a FROM Attendance a " +
            "JOIN FETCH a.classSession " +
            "WHERE a.client.id = :clientId " +
            "AND a.date BETWEEN :start AND :end")
    List<Attendance> findByClientIdAndDateBetween(
            @Param("clientId") Long clientId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
