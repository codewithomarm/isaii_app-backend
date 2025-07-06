package com.isaiiapp.backend.auth.v1.config;

import com.isaiiapp.backend.auth.v1.auth.dto.request.CreateAuthRequest;
import com.isaiiapp.backend.auth.v1.auth.service.AuthService;
import com.isaiiapp.backend.auth.v1.permission.dto.request.CreatePermissionRequest;
import com.isaiiapp.backend.auth.v1.permission.service.PermissionService;
import com.isaiiapp.backend.auth.v1.roles.dto.request.CreateRolesRequest;
import com.isaiiapp.backend.auth.v1.roles.service.RolesService;
import com.isaiiapp.backend.auth.v1.rolespermission.dto.request.AssignRolePermissionRequest;
import com.isaiiapp.backend.auth.v1.rolespermission.service.RolesPermissionService;
import com.isaiiapp.backend.auth.v1.users.dto.request.CreateUsersRequest;
import com.isaiiapp.backend.auth.v1.users.service.UsersService;
import com.isaiiapp.backend.auth.v1.usersroles.dto.request.AssignUserRoleRequest;
import com.isaiiapp.backend.auth.v1.usersroles.service.UsersRolesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner{

    private final PermissionService permissionService;
    private final RolesService rolesService;
    private final UsersService usersService;
    private final AuthService authService;
    private final RolesPermissionService rolesPermissionService;
    private final UsersRolesService usersRolesService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("🍽️ Starting restaurant system data initialization...");

        try {
            initializePermissions();
            initializeRoles();
            assignPermissionsToRoles();
            initializeUsers();
            initializeAuthAccounts();
            assignRolesToUsers();

            log.info("✅ Restaurant system initialization completed successfully!");
        } catch (Exception e) {
            log.error("❌ Error during restaurant system initialization: {}", e.getMessage(), e);
        }
    }

    private void initializePermissions() {
        log.info("📋 Initializing restaurant permissions...");

        String[][] permissions = {
                {"USER_MANAGEMENT", "Manage restaurant employees (create, update, delete users)"},
                {"USER_PASSWORD_RESET", "Reset passwords for restaurant employees"},
                {"USER_ACCOUNT_CONTROL", "Enable/disable employee accounts"},
                {"PRODUCT_MANAGEMENT", "Manage menu products (create, update, delete, pricing)"},
                {"MENU_CONFIGURATION", "Configure restaurant menu and categories"},
                {"REPORTS_SALES", "Generate sales reports by date"},
                {"REPORTS_PRODUCTS", "Generate product sales reports"},
                {"REPORTS_WAITERS", "Generate waiter performance reports"},
                {"REPORTS_KITCHEN", "Generate kitchen performance and timing reports"},
                {"SYSTEM_ANALYTICS", "Access to system analytics and metrics"},
                {"ORDER_CREATE", "Create new customer orders"},
                {"ORDER_VIEW", "View order status and details"},
                {"ORDER_CANCEL", "Cancel orders in 'Confirmado' status"},
                {"ORDER_PAYMENT", "Mark orders as paid"},
                {"ORDER_INVOICE", "Generate fiscal invoices"},
                {"TABLE_MANAGEMENT", "Manage table assignments and takeout orders"},
                {"KITCHEN_QUEUE_VIEW", "View kitchen order queue"},
                {"KITCHEN_ORDER_START", "Mark orders as 'En Curso' (in progress)"},
                {"KITCHEN_ORDER_COMPLETE", "Mark orders as 'Terminado' (completed)"},
                {"KITCHEN_TIMING", "Track preparation times"},
                {"SESSION_MANAGEMENT", "Manage user sessions"},
                {"PROFILE_VIEW", "View own profile information"},
                {"PROFILE_UPDATE", "Update own profile information"}
        };

        for (String[] permData : permissions) {
            try {
                if (permissionService.isPermissionNameAvailable(permData[0])) {
                    CreatePermissionRequest request = new CreatePermissionRequest(permData[0], permData[1]);
                    permissionService.createPermission(request);
                    log.info("✓ Created permission: {}", permData[0]);
                }
            } catch (Exception e) {
                log.warn("⚠️ Permission {} already exists or error: {}", permData[0], e.getMessage());
            }
        }
    }

    private void initializeRoles() {
        log.info("👥 Initializing restaurant roles...");

        String[][] roles = {
                {"admin", "Restaurant Administrator - Owners and managers with full system access"},
                {"host", "Restaurant Host/Waiter - Responsible for taking orders and customer service"},
                {"cook", "Kitchen Staff - Responsible for order preparation and kitchen operations"}
        };

        for (String[] roleData : roles) {
            try {
                if (rolesService.isRoleNameAvailable(roleData[0])) {
                    CreateRolesRequest request = new CreateRolesRequest(roleData[0], roleData[1]);
                    rolesService.createRole(request);
                    log.info("✓ Created role: {}", roleData[0]);
                }
            } catch (Exception e) {
                log.warn("⚠️ Role {} already exists or error: {}", roleData[0], e.getMessage());
            }
        }
    }

    private void assignPermissionsToRoles() {
        log.info("🔗 Assigning permissions to restaurant roles...");

        assignPermissionsToRole("admin", new String[]{
                "USER_MANAGEMENT", "USER_PASSWORD_RESET", "USER_ACCOUNT_CONTROL",
                "PRODUCT_MANAGEMENT", "MENU_CONFIGURATION",
                "REPORTS_SALES", "REPORTS_PRODUCTS", "REPORTS_WAITERS", "REPORTS_KITCHEN", "SYSTEM_ANALYTICS",
                "ORDER_VIEW", "ORDER_CANCEL", "ORDER_PAYMENT",
                "TABLE_MANAGEMENT",
                "KITCHEN_QUEUE_VIEW", "KITCHEN_TIMING",
                "SESSION_MANAGEMENT", "PROFILE_VIEW", "PROFILE_UPDATE"
        });

        assignPermissionsToRole("host", new String[]{
                "ORDER_CREATE", "ORDER_VIEW", "ORDER_CANCEL", "ORDER_PAYMENT", "ORDER_INVOICE",
                "TABLE_MANAGEMENT",
                "PROFILE_VIEW", "PROFILE_UPDATE"
        });

        assignPermissionsToRole("cook", new String[]{
                "KITCHEN_QUEUE_VIEW", "KITCHEN_ORDER_START", "KITCHEN_ORDER_COMPLETE", "KITCHEN_TIMING",
                "ORDER_VIEW",
                "PROFILE_VIEW", "PROFILE_UPDATE"
        });
    }

    private void assignPermissionsToRole(String roleName, String[] permissionNames) {
        try {
            var roleOpt = rolesService.getRoleByName(roleName);
            if (roleOpt.isPresent()) {
                Long roleId = roleOpt.get().getId();

                for (String permissionName : permissionNames) {
                    var permissionOpt = permissionService.getPermissionByName(permissionName);
                    if (permissionOpt.isPresent()) {
                        Long permissionId = permissionOpt.get().getId();

                        if (!rolesPermissionService.roleHasPermission(roleId, permissionId)) {
                            AssignRolePermissionRequest request = new AssignRolePermissionRequest(roleId, permissionId);
                            rolesPermissionService.assignPermissionToRole(request);
                        }
                    }
                }
                log.info("✓ Assigned permissions to role: {}", roleName);
            }
        } catch (Exception e) {
            log.warn("⚠️ Error assigning permissions to role {}: {}", roleName, e.getMessage());
        }
    }

    private void initializeUsers() {
        log.info("👤 Initializing restaurant employees...");

        String[][] users = {
                {"ADMIN01", "Carlos", "Rodriguez", "true"},
                {"MGR0001", "Maria", "Gonzalez", "true"},
                {"HOST001", "Juan", "Perez", "true"},
                {"HOST002", "Ana", "Martinez", "true"},
                {"HOST003", "Luis", "Garcia", "true"},
                {"COOK001", "Pedro", "Sanchez", "true"},
                {"COOK002", "Sofia", "Lopez", "true"},
                {"COOK003", "Miguel", "Torres", "true"}
        };

        for (String[] userData : users) {
            try {
                if (usersService.isEmployeeIdAvailable(userData[0])) {
                    CreateUsersRequest request = new CreateUsersRequest(
                            userData[0], userData[1], userData[2], Boolean.parseBoolean(userData[3])
                    );
                    usersService.createUser(request);
                    log.info("✓ Created employee: {} - {} {}", userData[0], userData[1], userData[2]);
                }
            } catch (Exception e) {
                log.warn("⚠️ Employee {} already exists or error: {}", userData[0], e.getMessage());
            }
        }
    }

    private void initializeAuthAccounts() {
        log.info("🔐 Initializing restaurant employee auth accounts...");

        String[][] authAccounts = {
                {"ADMIN01", "admin", "RestaurantAdmin2025!"},
                {"MGR0001", "manager", "RestaurantMgr2025!"},
                {"HOST001", "host001", "RestaurantHost2025!"},
                {"HOST002", "host002", "RestaurantHost2025!"},
                {"HOST003", "host003", "RestaurantHost2025!"},
                {"COOK001", "chef", "RestaurantChef2025!"},
                {"COOK002", "cook001", "RestaurantCook2025!"},
                {"COOK003", "cook002", "RestaurantCook2025!"}
        };

        for (String[] authData : authAccounts) {
            try {
                var userOpt = usersService.getUserByEmployeeId(authData[0]);
                if (userOpt.isPresent() && authService.isUsernameAvailable(authData[1])) {
                    CreateAuthRequest request = new CreateAuthRequest(
                            userOpt.get().getId(), authData[1], authData[2], true
                    );
                    authService.createAuth(request);
                    log.info("✓ Created auth account: {} for employee {}", authData[1], authData[0]);
                }
            } catch (Exception e) {
                log.warn("⚠️ Auth account {} already exists or error: {}", authData[1], e.getMessage());
            }
        }
    }

    private void assignRolesToUsers() {
        log.info("🎭 Assigning roles to restaurant employees...");

        String[][] userRoles = {
                {"ADMIN01", "admin"},
                {"MGR0001", "admin"},
                {"HOST001", "host"},
                {"HOST002", "host"},
                {"HOST003", "host"},
                {"COOK001", "cook"},
                {"COOK002", "cook"},
                {"COOK003", "cook"}
        };

        for (String[] userRole : userRoles) {
            try {
                var userOpt = usersService.getUserByEmployeeId(userRole[0]);
                var roleOpt = rolesService.getRoleByName(userRole[1]);

                if (userOpt.isPresent() && roleOpt.isPresent()) {
                    Long userId = userOpt.get().getId();
                    Long roleId = roleOpt.get().getId();

                    if (!usersRolesService.userHasRole(userId, roleId)) {
                        AssignUserRoleRequest request = new AssignUserRoleRequest(userId, roleId);
                        usersRolesService.assignRoleToUser(request);
                        log.info("✓ Assigned role {} to employee {}", userRole[1], userRole[0]);
                    }
                }
            } catch (Exception e) {
                log.warn("⚠️ Error assigning role {} to employee {}: {}", userRole[1], userRole[0], e.getMessage());
            }
        }
    }
}
