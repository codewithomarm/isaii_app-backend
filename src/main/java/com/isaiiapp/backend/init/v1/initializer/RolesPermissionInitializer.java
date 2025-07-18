package com.isaiiapp.backend.init.v1.initializer;

import com.isaiiapp.backend.auth.v1.permission.model.Permission;
import com.isaiiapp.backend.auth.v1.permission.repository.PermissionRepository;
import com.isaiiapp.backend.auth.v1.roles.model.Roles;
import com.isaiiapp.backend.auth.v1.roles.repository.RolesRepository;
import com.isaiiapp.backend.auth.v1.rolespermission.model.RolesPermission;
import com.isaiiapp.backend.auth.v1.rolespermission.model.RolesPermissionId;
import com.isaiiapp.backend.auth.v1.rolespermission.repository.RolesPermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class RolesPermissionInitializer implements CommandLineRunner {

    private final RolesRepository rolesRepository;
    private final PermissionRepository permissionRepository;
    private final RolesPermissionRepository rolesPermissionRepository;

    @Override
    public void run(String... args) {
        Roles admin = createRoleIfNotExists("ADMIN", "Administrador del sistema");
        Roles host = createRoleIfNotExists("HOST", "Encargado de atención");
        Roles cook = createRoleIfNotExists("COOK", "Cocinero");

        List<Permission> adminPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_SYSTEM_ADMIN",
                "PERMISSION_USER_READ",
                "PERMISSION_ROLE_READ",
                "PERMISSION_PERMISSION_READ"
        ));

        assignPermissionsToRole(admin, adminPermissions);

        initializeAuthControllerPermissions();
        initializePermissionControllerPermissions();
        initializeRolesControllerPermissions();
        initializeUsersControllerPermissions();
        initializeOrderItemsControllerPermissions();
        initializeOrdersControllerPermissions();
        initializeCategoryControllerPermissions();
        initializeProductControllerPermissions();
        initializeTablesControllerPermissions();

        log.info("Roles y permisos base inicializados correctamente.");
    }

    private Roles createRoleIfNotExists(String name, String description) {
        return rolesRepository.findByName(name)
                .orElseGet(() -> {
                    Roles role = new Roles();
                    role.setName(name);
                    role.setDescription(description);
                    return rolesRepository.save(role);
                });
    }

    private List<Permission> createPermissionsIfNotExist(List<String> permissions) {
        return permissions.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseGet(() -> permissionRepository.save(new Permission(null, name, name.replace('_', ' ')))))
                .toList();
    }

    private void assignPermissionsToRole(Roles role, List<Permission> permissions) {
        for (Permission permission : permissions) {
            RolesPermissionId id = new RolesPermissionId(role.getId(), permission.getId());

            if (rolesPermissionRepository.findById(id).isEmpty()) {
                RolesPermission rp = new RolesPermission();
                rp.setId(id);
                rp.setRole(role);         // ✅ NECESARIO
                rp.setPermission(permission); // ✅ NECESARIO
                rolesPermissionRepository.save(rp);

                log.info("Asignado permiso '{}' al rol '{}'", permission.getName(), role.getName());
            }
        }
    }

    private void initializeAuthControllerPermissions() {
        List<Permission> extraPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_USER_MANAGEMENT",
                "PERMISSION_USER_ACCOUNT_CONTROL",
                "PERMISSION_USER_PASSWORD_RESET"
        ));

        Roles admin = rolesRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("El rol ADMIN no existe."));

        assignPermissionsToRole(admin, extraPermissions);
    }

    private void initializePermissionControllerPermissions() {
        List<Permission> extraPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_PERMISSION_CREATE",
                "PERMISSION_PERMISSION_UPDATE",
                "PERMISSION_PERMISSION_DELETE",
                "PERMISSION_USER_MANAGEMENT",
                "PERMISSION_USER_ACCOUNT_CONTROL",
                "PERMISSION_USER_PASSWORD_RESET"
        ));

        Roles admin = rolesRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("El rol ADMIN no existe."));

        assignPermissionsToRole(admin, extraPermissions);
    }

    private void initializeRolesControllerPermissions() {
        List<Permission> extraPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_ROLE_CREATE",
                "PERMISSION_ROLE_UPDATE",
                "PERMISSION_ROLE_DELETE",
                "PERMISSION_ROLE_READ",
                "PERMISSION_USER_MANAGEMENT",
                "PERMISSION_SYSTEM_ADMIN"
        ));

        Roles admin = rolesRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("El rol ADMIN no existe."));

        assignPermissionsToRole(admin, extraPermissions);
    }

    private void initializeUsersControllerPermissions() {
        // Permisos para ADMIN
        List<Permission> extraAdminPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_USER_MANAGEMENT",
                "PERMISSION_PROFILE_VIEW",
                "PERMISSION_PROFILE_UPDATE",
                "PERMISSION_USER_ACCOUNT_CONTROL"
        ));

        Roles admin = rolesRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("El rol ADMIN no existe."));

        assignPermissionsToRole(admin, extraAdminPermissions);

        // Permisos para HOST
        List<Permission> extraHostPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_PROFILE_VIEW",
                "PERMISSION_PROFILE_UPDATE"
        ));

        Roles host = rolesRepository.findByName("HOST")
                .orElseThrow(() -> new RuntimeException("El rol HOST no existe."));

        assignPermissionsToRole(host, extraHostPermissions);

        // Permisos para COOK
        List<Permission> extraCookPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_PROFILE_VIEW",
                "PERMISSION_PROFILE_UPDATE"
        ));

        Roles cook = rolesRepository.findByName("COOK")
                .orElseThrow(() -> new RuntimeException("El rol COOK no existe."));

        assignPermissionsToRole(cook, extraCookPermissions);
    }

    private void initializeOrderItemsControllerPermissions() {
        // Permisos para ADMIN
        List<Permission> extraAdminPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_ORDER_CREATE",
                "PERMISSION_ORDER_UPDATE",
                "PERMISSION_ORDER_DELETE",
                "PERMISSION_ORDER_READ",
                "PERMISSION_ORDER_STATS"
        ));

        Roles admin = rolesRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("El rol ADMIN no existe."));

        assignPermissionsToRole(admin, extraAdminPermissions);

        // Permisos para HOST
        List<Permission> extraHostPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_ORDER_CREATE",
                "PERMISSION_ORDER_UPDATE",
                "PERMISSION_ORDER_READ",
                "PERMISSION_ORDER_STATS"
        ));

        Roles host = rolesRepository.findByName("HOST")
                .orElseThrow(() -> new RuntimeException("El rol HOST no existe."));

        assignPermissionsToRole(host, extraHostPermissions);

        // Permisos para COOK
        List<Permission> extraCookPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_ORDER_UPDATE",
                "PERMISSION_ORDER_READ",
                "PERMISSION_ORDER_STATS"
        ));

        Roles cook = rolesRepository.findByName("COOK")
                .orElseThrow(() -> new RuntimeException("El rol COOK no existe."));

        assignPermissionsToRole(cook, extraCookPermissions);
    }

    private void initializeOrdersControllerPermissions() {
        // Permisos para ADMIN
        List<Permission> extraAdminPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_ORDER_CREATE",
                "PERMISSION_ORDER_READ",
                "PERMISSION_ORDER_UPDATE",
                "PERMISSION_ORDER_DELETE",
                "PERMISSION_ORDER_STATS",
                "PERMISSION_ORDER_STATUS_MANAGE"
        ));

        Roles admin = rolesRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("El rol ADMIN no existe."));

        assignPermissionsToRole(admin, extraAdminPermissions);

        // Permisos para HOST
        List<Permission> extraHostPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_ORDER_CREATE",
                "PERMISSION_ORDER_READ",
                "PERMISSION_ORDER_UPDATE"
        ));

        Roles host = rolesRepository.findByName("HOST")
                .orElseThrow(() -> new RuntimeException("El rol HOST no existe."));

        assignPermissionsToRole(host, extraHostPermissions);

        // Permisos para COOK
        List<Permission> extraCookPermissions = createPermissionsIfNotExist(List.of(
                "PERMISSION_ORDER_READ",
                "PERMISSION_ORDER_UPDATE"
        ));

        Roles cook = rolesRepository.findByName("COOK")
                .orElseThrow(() -> new RuntimeException("El rol COOK no existe."));

        assignPermissionsToRole(cook, extraCookPermissions);
    }

    private void initializeCategoryControllerPermissions() {
        // Permisos para ADMIN
        List<Permission> extraAdminPermissions = createPermissionsIfNotExist(List.of(
                "CREATE_CATEGORY",
                "READ_CATEGORY",
                "UPDATE_CATEGORY",
                "DELETE_CATEGORY"
        ));

        Roles admin = rolesRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("El rol ADMIN no existe."));

        assignPermissionsToRole(admin, extraAdminPermissions);

        // Permisos para HOST
        List<Permission> extraHostPermissions = createPermissionsIfNotExist(List.of(
                "READ_CATEGORY"
        ));

        Roles host = rolesRepository.findByName("HOST")
                .orElseThrow(() -> new RuntimeException("El rol HOST no existe."));

        assignPermissionsToRole(host, extraHostPermissions);

        // Permisos para COOK
        List<Permission> extraCookPermissions = createPermissionsIfNotExist(List.of(
                "READ_CATEGORY"
        ));

        Roles cook = rolesRepository.findByName("COOK")
                .orElseThrow(() -> new RuntimeException("El rol COOK no existe."));

        assignPermissionsToRole(cook, extraCookPermissions);
    }

    private void initializeProductControllerPermissions() {
        // Permisos para ADMIN
        List<Permission> extraAdminPermissions = createPermissionsIfNotExist(List.of(
                "CREATE_PRODUCT",
                "READ_PRODUCT",
                "UPDATE_PRODUCT",
                "DELETE_PRODUCT",
                "TOGGLE_PRODUCT",
                "STATS_PRODUCT",
                "REPORT_PRODUCT"
        ));

        Roles admin = rolesRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("El rol ADMIN no existe."));

        assignPermissionsToRole(admin, extraAdminPermissions);

        // Permisos para HOST
        List<Permission> extraHostPermissions = createPermissionsIfNotExist(List.of(
                "READ_PRODUCT"
        ));

        Roles host = rolesRepository.findByName("HOST")
                .orElseThrow(() -> new RuntimeException("El rol HOST no existe."));

        assignPermissionsToRole(host, extraHostPermissions);

        // Permisos para COOK
        List<Permission> extraCookPermissions = createPermissionsIfNotExist(List.of(
                "READ_PRODUCT"
        ));

        Roles cook = rolesRepository.findByName("COOK")
                .orElseThrow(() -> new RuntimeException("El rol COOK no existe."));

        assignPermissionsToRole(cook, extraCookPermissions);
    }

    private void initializeTablesControllerPermissions() {
        // Permisos para ADMIN
        List<Permission> extraAdminPermissions = createPermissionsIfNotExist(List.of(
                "CREATE_TABLE",
                "READ_TABLE",
                "UPDATE_TABLE",
                "DELETE_TABLE"
        ));

        Roles admin = rolesRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("El rol ADMIN no existe."));

        assignPermissionsToRole(admin, extraAdminPermissions);

        // Permisos para HOST
        List<Permission> extraHostPermissions = createPermissionsIfNotExist(List.of(
                "READ_TABLE"
        ));

        Roles host = rolesRepository.findByName("HOST")
                .orElseThrow(() -> new RuntimeException("El rol HOST no existe."));

        assignPermissionsToRole(host, extraHostPermissions);

        // Permisos para COOK
        List<Permission> extraCookPermissions = createPermissionsIfNotExist(List.of(
                "READ_TABLE"
        ));

        Roles cook = rolesRepository.findByName("COOK")
                .orElseThrow(() -> new RuntimeException("El rol COOK no existe."));

        assignPermissionsToRole(cook, extraCookPermissions);
    }
}
