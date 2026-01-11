package com.example.Qore.repository;

import com.example.Qore.model.ClientPlanHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientPlanHistoryRepository extends JpaRepository<ClientPlanHistory, Long> {
    List<ClientPlanHistory> findByClientIdOrderByAssignedAtDesc(Long clientId);
}
