package com.example.Qore.controller;

import com.example.Qore.DTO.ClassSessionDTO;
import com.example.Qore.DTO.ClassSessionUpdateDTO;
import com.example.Qore.DTO.ClientResponseDTO;
import com.example.Qore.model.ClassSession;
import com.example.Qore.service.ClassSessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/range")
    public ResponseEntity<List<ClassSessionDTO>> getClassesByRange(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        return ResponseEntity.ok(service.getClassesByDateRange(start, end));
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
    public ResponseEntity<Map<String, String>> addClientToClass(
            @PathVariable Long classId,
            @PathVariable Long clientId
    ) {
        try {
            service.addClientToClass(classId, clientId);
            return ResponseEntity.ok(Map.of("message", "Cliente agregado a la clase exitosamente"));
        } catch (IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado: " + e.getMessage()));
        }
    }


    @DeleteMapping("/{classId}/clients/{clientId}")
    public ResponseEntity<Map<String, String>> removeClientFromClass(
            @PathVariable Long classId,
            @PathVariable Long clientId
    ) {
        service.removeClientFromClass(classId, clientId);
        return ResponseEntity.ok(Map.of("message", "Client removed from class successfully"));
    }




    @GetMapping("/weekly-count")
    public ResponseEntity<Map<String, Long>> getWeeklyClassCount() {
        long count = service.getCurrentWeekClasses();
        Map<String, Long> response = new HashMap<>();
        response.put("weeklyCount", count);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<ClassSessionDTO>> getByInstructor(
            @PathVariable Long instructorId,
            @RequestParam("start") String startStr,
            @RequestParam("end") String endStr
    ) {
        LocalDate start = LocalDate.parse(startStr);
        LocalDate end = LocalDate.parse(endStr);

        List<ClassSessionDTO> sessions = service.getClassesByInstructor(instructorId, start, end);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/instructors/{instructorId}/pending-today")
    public ResponseEntity<List<ClassSessionDTO>> getPendingToday(@PathVariable Long instructorId) {
        return ResponseEntity.ok(service.getPendingClassesToday(instructorId));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ClassSessionDTO>> getClassesByClient(
            @PathVariable Long clientId,
            @RequestParam("start") String startStr,
            @RequestParam("end") String endStr
    ) {
        LocalDate start = LocalDate.parse(startStr);
        LocalDate end = LocalDate.parse(endStr);
        return ResponseEntity.ok(service.getClassesForClient(clientId, start, end));
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

    @GetMapping("/{classId}/clients")
    public ResponseEntity<List<ClientResponseDTO>> getClientsByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(service.getClientsByClass(classId));
    }


}
