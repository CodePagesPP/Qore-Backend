package com.example.Qore.controller;

import com.example.Qore.DTO.ClassSessionDTO;
import com.example.Qore.DTO.ClassSessionUpdateDTO;
import com.example.Qore.service.ClassSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class-sessions")
@RequiredArgsConstructor
public class ClassSessionController {

    private final ClassSessionService service;

    @GetMapping("/getAll")
    public List<ClassSessionDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/getById/{id}")
    public ClassSessionDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("/create")
    public ClassSessionDTO create(@RequestBody ClassSessionDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/update/{id}")
    public ClassSessionDTO update(@PathVariable Long id, @RequestBody ClassSessionUpdateDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{classId}/clients/{clientId}")
    public ResponseEntity<String> addClientToClass(@PathVariable Long classId, @PathVariable Long clientId) {
        service.addClientToClass(classId, clientId);
        return ResponseEntity.ok("Client added to class successfully");
    }
}
