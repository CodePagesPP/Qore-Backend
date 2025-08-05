package com.example.Qore.service.Impl;

import com.example.Qore.DTO.RoleDTO;
import com.example.Qore.model.Permission;
import com.example.Qore.model.RoleE;
import com.example.Qore.repository.PermissionRepository;
import com.example.Qore.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    @Override
    public RoleE createRole(RoleDTO dto) {
        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(dto.getPermissionIds()));

        RoleE role = RoleE.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .permissions(permissions)
                .build();

        return roleRepository.save(role);
    }

    @Override
    public List<RoleE> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public RoleE getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con ID: " + id));
    }

    @Override
    public RoleE updateRole(Long id, RoleDTO dto) {
        RoleE role = getRoleById(id);
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());

        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(dto.getPermissionIds()));
        role.setPermissions(permissions);

        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("Rol no encontrado con ID: " + id);
        }
        roleRepository.deleteById(id);
    }
}
