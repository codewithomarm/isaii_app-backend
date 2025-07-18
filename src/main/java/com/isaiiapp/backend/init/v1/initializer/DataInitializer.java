package com.isaiiapp.backend.init.v1.initializer;

import com.isaiiapp.backend.auth.v1.auth.model.Auth;
import com.isaiiapp.backend.auth.v1.auth.repository.AuthRepository;
import com.isaiiapp.backend.auth.v1.roles.model.Roles;
import com.isaiiapp.backend.auth.v1.roles.repository.RolesRepository;
import com.isaiiapp.backend.auth.v1.users.model.Users;
import com.isaiiapp.backend.auth.v1.users.repository.UsersRepository;
import com.isaiiapp.backend.auth.v1.usersroles.model.UsersRoles;
import com.isaiiapp.backend.auth.v1.usersroles.model.UsersRolesId;
import com.isaiiapp.backend.auth.v1.usersroles.repository.UsersRolesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class DataInitializer implements CommandLineRunner {

    private final UsersRepository usersRepository;
    private final AuthRepository authRepository;
    private final RolesRepository rolesRepository;
    private final UsersRolesRepository usersRolesRepository;
    private final PasswordEncoder passwordEncoder;
    private int contador = 0;

    @Override
    @Transactional
    public void run(String... args) {
        createUserWithRole("admin_user", "Admin", "Usuario", "ADMIN");
        createUserWithRole("host_user", "Host", "Usuario", "HOST");
        createUserWithRole("cook_user", "Cook", "Usuario", "COOK");
    }

    private void createUserWithRole(String username, String firstName, String lastName, String roleName) {
        if (authRepository.findByUsername(username).isPresent()) {
            log.info("Usuario '{}' ya existe. Skipping.", username);
            return;
        }

        // Crear usuario
        Users user = new Users();
        String employeeId = "EMP" + String.format("%04d", contador++); // EMP0001, EMP0002, etc.
        user.setEmployeeId(employeeId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setIsActive(true);

        Users savedUser = usersRepository.save(user);

        // Crear auth
        Auth auth = new Auth();
        auth.setUser(savedUser);
        auth.setUsername(username);
        auth.setPasswordHash(passwordEncoder.encode("password123")); // ⚠️ Cambiar en producción
        auth.setEnabled(true);
        auth.setLoginAttempts(0);
        auth.setCreatedAt(LocalDateTime.now());

        authRepository.save(auth);

        // Buscar rol
        Optional<Roles> roleOpt = rolesRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            log.warn("Rol '{}' no encontrado. Debes crearlo antes.", roleName);
            return;
        }
        Roles role = roleOpt.get();

        // Crear UsersRoles correctamente
        UsersRoles userRole = new UsersRoles();
        userRole.setId(new UsersRolesId(savedUser.getId(), role.getId()));
        userRole.setUser(savedUser);
        userRole.setRole(role); // <- IMPORTANTE: necesitas esto para que el insert funcione

        log.info("Guardando relación entre usuario '{}' y rol '{}'", savedUser.getFirstName(), role.getName());
        usersRolesRepository.save(userRole);
        log.info("Relación guardada.");

        log.info("Usuario '{}' creado con rol '{}'", username, roleName);
    }
}
