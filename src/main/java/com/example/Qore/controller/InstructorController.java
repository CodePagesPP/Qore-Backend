package com.example.Qore.controller;

import com.example.Qore.DTO.InstructorResponseDTO;
import com.example.Qore.DTO.InstructorUpdateDTO;
import com.example.Qore.model.Instructor;
import com.example.Qore.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/instructor")
public class InstructorController {
     private final InstructorService instructorService;

     @GetMapping("/listInstructor")
    public ResponseEntity<List<InstructorResponseDTO>> listInstructor() {
         return ResponseEntity.ok(instructorService.getAllInstructors());
     }

     @PutMapping("/updateInstructor/{dni}")
    public ResponseEntity<InstructorResponseDTO> updateInstructor(@PathVariable("dni") String dni, @RequestBody InstructorUpdateDTO instructor) {
         return ResponseEntity.ok(instructorService.updateInstructor(dni, instructor));
     }

     @DeleteMapping("/deleteInstructor/{dni}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable("dni") String dni) {
         instructorService.deleteInstructor(dni);
         return ResponseEntity.noContent().build();
     }
}
