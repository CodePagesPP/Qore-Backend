package com.example.Qore.service;

import com.example.Qore.DTO.RoleDTO;
import com.example.Qore.model.RoleE;

import java.util.List;

public interface RoleService {
    RoleE createRole(RoleDTO dto);
    List<RoleE> getAllRoles();
    RoleE getRoleById(Long id);
    RoleE updateRole(Long id, RoleDTO dto);
    void deleteRole(Long id);
}
