package com.example.Qore.controller;

import com.example.Qore.model.Discipline;
import com.example.Qore.service.DisciplineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/disciplines")
@RequiredArgsConstructor
public class DisciplineController {
    private final DisciplineService disciplineService;

    @PostMapping
    public ResponseEntity<Discipline> create(@RequestBody Discipline discipline) {
        return ResponseEntity.ok(disciplineService.create(discipline));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Discipline> update(@PathVariable Long id, @RequestBody Discipline discipline) {
        return ResponseEntity.ok(disciplineService.update(id, discipline));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        disciplineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Discipline>> getAll() {
        return ResponseEntity.ok(disciplineService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Discipline> getById(@PathVariable Long id) {
        return ResponseEntity.ok(disciplineService.getById(id));
    }
}
