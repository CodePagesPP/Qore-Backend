package com.example.Qore.service.Impl;

import com.example.Qore.model.Permission;
import com.example.Qore.model.RoleE;
import com.example.Qore.repository.PermissionRepository;
import com.example.Qore.repository.RoleRepository;
import com.example.Qore.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
}
