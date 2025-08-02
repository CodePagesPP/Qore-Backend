package com.example.Qore.controller;

import com.example.Qore.DTO.StaffResponseDTO;
import com.example.Qore.DTO.StaffUpdateDTO;
import com.example.Qore.model.Staff;
import com.example.Qore.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/staff")
public class StaffController {
    private final StaffService staffService;

    @GetMapping("/listStaff")
    public ResponseEntity<List<StaffResponseDTO>> listStaff(){
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @PutMapping("/updateStaff/{dni}")
    public ResponseEntity<StaffResponseDTO> updateStaff(@PathVariable("dni") String dni ,@RequestBody StaffUpdateDTO staff) {
        return ResponseEntity.ok(staffService.updateStaff(dni, staff));
    }

    @DeleteMapping("/deleteStaff/{dni}")
    public ResponseEntity<Void> deleteStaff(@PathVariable("dni") String dni) {
        staffService.deleteStaff(dni);
        return ResponseEntity.noContent().build();
    }
}
