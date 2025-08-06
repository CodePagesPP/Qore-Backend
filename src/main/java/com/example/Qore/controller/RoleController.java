package com.example.Qore.controller;

import com.example.Qore.DTO.RoleDTO;
import com.example.Qore.model.RoleE;
import com.example.Qore.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rol")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<RoleE> createRole(@RequestBody RoleDTO dto) {
        return ResponseEntity.ok(roleService.createRole(dto));
    }

    @GetMapping("/list")
    public ResponseEntity<List<RoleE>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<RoleE> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RoleE> updateRole(@PathVariable Long id, @RequestBody RoleDTO dto) {
        return ResponseEntity.ok(roleService.updateRole(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
