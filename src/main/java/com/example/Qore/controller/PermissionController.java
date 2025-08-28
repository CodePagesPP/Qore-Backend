package com.example.Qore.controller;

import com.example.Qore.model.Permission;
import com.example.Qore.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping("/list")
    public ResponseEntity<List<Permission>> getAllPermission() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }
}
