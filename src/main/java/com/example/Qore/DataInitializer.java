package com.example.Qore;

import com.example.Qore.model.Permission;
import com.example.Qore.model.RoleE;
import com.example.Qore.repository.PermissionRepository;
import com.example.Qore.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) throws Exception {
        // PERMISOOOOOOOOSSS
        Permission adminPermission = createPermissionIfNotExists("ADMIN_ACCESS");
        Permission instructorPermission = createPermissionIfNotExists("INSTRUCTOR_ACCESS");
        Permission staffPermission = createPermissionIfNotExists("STAFF_ACCESS");
        Permission clientPermission = createPermissionIfNotExists("CLIENT_ACCESS");
        Permission managerPermission = createPermissionIfNotExists("MANAGER_ACCESS");
        Permission rolePermission = createPermissionIfNotExists("ROLE_ACCESS");
        Permission roomPermission = createPermissionIfNotExists("ROOM_ACCESS");
        Permission disciplinePermission = createPermissionIfNotExists("DISCIPLINE_ACCESS");
        Permission classSessionPermission = createPermissionIfNotExists("CLASS_SESSION_ACCESS");
        // Roles con sus permisos
        createRoleIfNotExists("ADMIN", Set.of(adminPermission));
        createRoleIfNotExists("CLIENT", Set.of(clientPermission));
        createRoleIfNotExists("INSTRUCTOR", Set.of(instructorPermission));
        createRoleIfNotExists("STAFF", Set.of(staffPermission));
        createRoleIfNotExists("MANAGER", Set.of(managerPermission));
    }

    private Permission createPermissionIfNotExists(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(new Permission(name)));
    }

    private void createRoleIfNotExists(String name, Set<Permission> permissions) {
        if (roleRepository.findByName(name).isEmpty()) {
            RoleE role = new RoleE();
            role.setName(name);
            role.setPermissions(permissions);
            roleRepository.save(role);
        }
    }
}