package com.example.Qore.service.Impl;

import com.example.Qore.model.Discipline;
import com.example.Qore.repository.DisciplineRepository;
import com.example.Qore.service.DisciplineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplineServiceImpl implements DisciplineService {

    private final DisciplineRepository disciplineRepository;

    @Override
    public Discipline create(Discipline discipline) {
        return disciplineRepository.save(discipline);
    }

    @Override
    public Discipline update(Long id, Discipline discipline) {
        Discipline existing = disciplineRepository.findById(id).orElseThrow();
        existing.setName(discipline.getName());
        existing.setDescription(discipline.getDescription());
        return disciplineRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        disciplineRepository.deleteById(id);
    }

    @Override
    public List<Discipline> getAll() {
        return disciplineRepository.findAll();
    }

    @Override
    public Discipline getById(Long id) {
        return disciplineRepository.findById(id).orElseThrow();
    }
}
