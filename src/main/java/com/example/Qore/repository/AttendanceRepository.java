package com.example.Qore.repository;

import com.example.Qore.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByClassSessionId(Long classId);
    List<Attendance> findByClientId(Long clientId);
    Optional<Attendance> findByClassSessionIdAndClientIdAndDate(Long classId, Long clientId, LocalDate date);
}
