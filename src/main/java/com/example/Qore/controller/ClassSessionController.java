package com.example.Qore.controller;

import com.example.Qore.DTO.ClassSessionDTO;
import com.example.Qore.DTO.ClassSessionUpdateDTO;
import com.example.Qore.model.ClassSession;
import com.example.Qore.service.ClassSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<ClassSessionDTO> create(@RequestBody ClassSessionDTO dto) {
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

    @GetMapping("/weekly-count")
    public ResponseEntity<Map<String, Long>> getWeeklyClassCount() {
        long count = service.getCurrentWeekClasses();
        Map<String, Long> response = new HashMap<>();
        response.put("weeklyCount", count);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<ClassSessionDTO>> getByInstructor(@PathVariable Long instructorId) {
        List<ClassSessionDTO> sessions = service.getClassesByInstructor(instructorId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/instructors/{instructorId}/pending-today")
    public ResponseEntity<List<ClassSessionDTO>> getPendingToday(@PathVariable Long instructorId) {
        return ResponseEntity.ok(service.getPendingClassesToday(instructorId));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ClassSessionDTO>> getClassesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(service.getClassesForClient(clientId));
    }

    @PostMapping("/{classId}/join/{clientId}")
    public ResponseEntity<Map<String, String>> joinClass(
            @PathVariable Long classId,
            @PathVariable Long clientId
    ) {
        service.joinClassClient(classId, clientId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Cliente inscrito en la clase con éxito");

        return ResponseEntity.ok(response);
    }

}
