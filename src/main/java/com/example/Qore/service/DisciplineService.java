package com.example.Qore.service;

import com.example.Qore.model.Discipline;

import java.util.List;

public interface DisciplineService {
    Discipline create(Discipline discipline);
    Discipline update(Long id, Discipline discipline);
    void delete(Long id);
    List<Discipline> getAll();
    Discipline getById(Long id);
}
