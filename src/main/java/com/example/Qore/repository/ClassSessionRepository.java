package com.example.Qore.repository;

import com.example.Qore.model.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    boolean existsByRoomId(Long roomId);
    List<ClassSession> findByClients_Id(Long clientId);
}
