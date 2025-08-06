package com.example.Qore.repository;

import com.example.Qore.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline,Long> {
    Optional<Discipline> findByName(String name);
}
