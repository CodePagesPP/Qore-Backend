package com.example.Qore;

import com.example.Qore.model.Discipline;
import com.example.Qore.model.Permission;
import com.example.Qore.model.Plan;
import com.example.Qore.model.RoleE;
import com.example.Qore.repository.DisciplineRepository;
import com.example.Qore.repository.PermissionRepository;
import com.example.Qore.repository.PlanRepository;
import com.example.Qore.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final PlanRepository planRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final DisciplineRepository disciplineRepository;

    @Override
    public void run(String... args) {
        System.out.println("Iniciando carga de data inicial...");

        try {
            createPermissionsAndRoles();
            createPlans();
            createDisciplines();
            System.out.println("Data inicial cargada correctamente.");
        } catch (Exception e) {
            System.err.println("Error al cargar data inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void createPermissionsAndRoles() {
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
        Permission excelPermission = createPermissionIfNotExists("EXCEL_ACCESS");
        Permission paymentPermission = createPermissionIfNotExists("PAYMENT_ACCESS");
        // Roles con sus permisos
        createRoleIfNotExists("ADMIN", "ROL ADMIN", Set.of(adminPermission));
        createRoleIfNotExists("CLIENT", "ROL CLIENT", Set.of(clientPermission));
        createRoleIfNotExists("INSTRUCTOR", "ROL INSTRUCTOR", Set.of(instructorPermission));
        createRoleIfNotExists("STAFF", "ROL STAFF", Set.of(staffPermission));
        createRoleIfNotExists("MANAGER", "ROL MANAGER", Set.of(managerPermission));

    }


    private void createPlans() {
        if (planRepository.count() > 0) {
            System.out.println("📦 Planes ya existentes. No se insertan duplicados.");
            return;
        }
        //CREACION DE PLANES
        if (planRepository.count() == 0) {
            Plan plan1 = Plan.builder()
                    .name("Pack de 4 Clases")
                    .description("Acceso a 4 sesiones al mes.")
                    .sessions(4)
                    .payMethod("mercado_pago")
                    .duration(30)
                    .price(200.0f)
                    .sellType("subscription")
                    .active(true)
                    .reprograms(1)
                    .build();

            Plan plan2 = Plan.builder()
                    .name("Pack de 8 Clases")
                    .description("Acceso a 8 sesiones al mes.")
                    .sessions(8)
                    .payMethod("mercado_pago")
                    .duration(30)
                    .price(360.0f)
                    .sellType("subscription")
                    .active(true)
                    .reprograms(2)
                    .build();

            Plan plan3 = Plan.builder()
                    .name("Pack de 12 Clases")
                    .description("Acceso a 12 sesiones al mes.")
                    .sessions(12)
                    .payMethod("mercado_pago")
                    .duration(30)
                    .price(450.0f)
                    .sellType("subscription")
                    .active(true)
                    .reprograms(5)
                    .build();

            Plan plan4 = Plan.builder()
                    .name("Clase Suelta")
                    .description("Acceso a una sola clase.")
                    .sessions(1)
                    .payMethod("mercado_pago")
                    .duration(1)
                    .price(60.0f)
                    .sellType("one_time")
                    .active(true)
                    .reprograms(0)
                    .build();

            Plan plan5 = Plan.builder()
                    .name("Clase Suelta Privada")
                    .description("Acceso a una sola clase privada.")
                    .sessions(1)
                    .payMethod("mercado_pago")
                    .duration(1)
                    .price(90.0f)
                    .sellType("one_time")
                    .active(true)
                    .reprograms(0)
                    .build();

            Plan plan6 = Plan.builder()
                    .name("Pack de 4 Clases Privadas")
                    .description("Acceso a 4 clases privadas.")
                    .sessions(4)
                    .payMethod("mercado_pago")
                    .duration(30)
                    .price(340.0f)
                    .sellType("one_time")
                    .active(true)
                    .reprograms(0)
                    .build();

            Plan plan7 = Plan.builder()
                    .name("Pack Mixto")
                    .description("Acceso a 4 clases grupales\nAcceso a 4 clases privadas.")
                    .sessions(8)
                    .payMethod("mercado_pago")
                    .duration(30)
                    .price(485.0f)
                    .sellType("one_time")
                    .active(true)
                    .reprograms(0)
                    .build();

            planRepository.saveAll(List.of(plan1, plan2, plan3, plan4, plan5, plan6, plan7));
        }
    }

    private void createDisciplines() {
        if (disciplineRepository.count() > 0) {
            System.out.println("🧘‍♀️ Disciplinas ya existentes. No se insertan duplicados.");
            return;
        }

        System.out.println("🧘‍♀️ Insertando disciplinas iniciales...");

        List<Discipline> disciplines = List.of(
                Discipline.builder()
                        .name("Pre Pilates")
                        .description("Para alumnas principiantes que recién están teniendo un acercamiento con la disciplina.")
                        .build(),
                Discipline.builder()
                        .name("Pilates Principiante")
                        .description("Para alumnas que ya tienen claros los principios básicos y están reconociendo su cuerpo.")
                        .build(),
                Discipline.builder()
                        .name("Pilates Intermedio")
                        .description("Para alumnas que ya conocen y dominan los principios básicos del Pilates.")
                        .build(),
                Discipline.builder()
                        .name("Pilates Avanzado")
                        .description("Para alumnas que dominan los principios del Pilates y dominan también su cuerpo para poder sostenerse.")
                        .build(),
                Discipline.builder()
                        .name("Pilates Rehabilitación")
                        .description("Para alumnas que por distintas condiciones o patologías necesitan de especial atención y cuidado.")
                        .build(),
                Discipline.builder()
                        .name("Pilates Pre y Post Natal")
                        .description("Para alumnas que están embarazadas o recién han dado a luz (4 meses).")
                        .build(),
                Discipline.builder()
                        .name("Pilates Senior")
                        .description("Para mayores de 70 años.")
                        .build()
        );

        disciplineRepository.saveAll(disciplines);
    }

    private Permission createPermissionIfNotExists(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(new Permission(name)));
    }

    private void createRoleIfNotExists(String name, String description,Set<Permission> permissions) {
        if (roleRepository.findByName(name).isEmpty()) {
            RoleE role = new RoleE();
            role.setName(name);
            role.setDescription(description);
            role.setPermissions(permissions);
            roleRepository.save(role);
        }
    }
}