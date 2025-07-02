package com.isaiiapp.backend.auth.v1.roles.service;

import com.isaiiapp.backend.auth.v1.exception.DuplicateResourceException;
import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.auth.v1.permission.dto.response.PermissionResponse;
import com.isaiiapp.backend.auth.v1.permission.mapper.PermissionMapper;
import com.isaiiapp.backend.auth.v1.permission.model.Permission;
import com.isaiiapp.backend.auth.v1.permission.repository.PermissionRepository;
import com.isaiiapp.backend.auth.v1.roles.dto.request.CreateRolesRequest;
import com.isaiiapp.backend.auth.v1.roles.dto.request.UpdateRolesRequest;
import com.isaiiapp.backend.auth.v1.roles.dto.response.RolesResponse;
import com.isaiiapp.backend.auth.v1.roles.mapper.RolesMapper;
import com.isaiiapp.backend.auth.v1.roles.model.Roles;
import com.isaiiapp.backend.auth.v1.roles.repository.RolesRepository;
import com.isaiiapp.backend.auth.v1.rolespermission.repository.RolesPermissionRepository;
import com.isaiiapp.backend.auth.v1.usersroles.repository.UsersRolesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RolesServiceImpl implements RolesService {

    private final RolesRepository rolesRepository;
    private final RolesMapper rolesMapper;
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final RolesPermissionRepository rolesPermissionRepository;
    private final UsersRolesRepository usersRolesRepository;

    @Override
    public RolesResponse createRole(CreateRolesRequest request) {
        log.info("Creating new role with name: {}", request.getName());

        // Verificar si ya existe un rol con el mismo nombre
        if (rolesRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Role already exists with name: " + request.getName());
        }

        Roles role = rolesMapper.toEntity(request);
        Roles savedRole = rolesRepository.save(role);

        log.info("Role created successfully with id: {}", savedRole.getId());
        return rolesMapper.toResponse(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RolesResponse> getRoleById(Long id) {
        log.info("Fetching role by id: {}", id);
        Optional<Roles> role = rolesRepository.findById(id);
        return role.map(rolesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RolesResponse> getRoleByName(String name) {
        log.info("Fetching role by name: {}", name);
        Optional<Roles> role = rolesRepository.findByName(name);
        return role.map(rolesMapper::toResponse);
    }

    @Override
    public RolesResponse updateRole(Long id, UpdateRolesRequest request) {
        log.info("Updating role with id: {}", id);

        Roles existingRole = rolesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        // Verificar si el nuevo nombre ya existe (excluyendo el rol actual)
        if (request.getName() != null &&
                !existingRole.getName().equals(request.getName()) &&
                rolesRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Role already exists with name: " + request.getName());
        }

        // Actualizar campos solo si no son null
        if (request.getName() != null) {
            existingRole.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existingRole.setDescription(request.getDescription());
        }

        Roles updatedRole = rolesRepository.save(existingRole);
        log.info("Role updated successfully with id: {}", updatedRole.getId());

        return rolesMapper.toResponse(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role with id: {}", id);

        Roles role = rolesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        // Verificar si el rol tiene usuarios asignados
        Long usersCount = rolesRepository.countRolesByUserId(id);
        if (usersCount > 0) {
            throw new IllegalStateException("Cannot delete role with assigned users. Please reassign users first.");
        }

        // Remover todas las relaciones rol-permiso antes de eliminar el rol
        rolesPermissionRepository.deleteByRoleId(id);

        rolesRepository.delete(role);
        log.info("Role deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolesResponse> getAllRoles(Pageable pageable) {
        log.info("Fetching all roles with pagination: {}", pageable);
        Page<Roles> rolesPage = rolesRepository.findAll(pageable);
        return rolesPage.map(rolesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolesResponse> searchRolesByName(String name, Pageable pageable) {
        log.info("Searching roles by name: {}", name);
        Page<Roles> rolesPage = rolesRepository.findByNameContaining(name, pageable);
        return rolesPage.map(rolesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolesResponse> searchRolesByDescription(String description, Pageable pageable) {
        log.info("Searching roles by description: {}", description);
        Page<Roles> rolesPage = rolesRepository.findByDescriptionContaining(description, pageable);
        return rolesPage.map(rolesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolesResponse> getRolesByUserId(Long userId, Pageable pageable) {
        log.info("Fetching roles for user id: {}", userId);
        Page<Roles> rolesPage = rolesRepository.findRolesByUserId(userId, pageable);
        return rolesPage.map(rolesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolesResponse> getRolesNotAssignedToUser(Long userId, Pageable pageable) {
        log.info("Fetching roles not assigned to user id: {}", userId);
        Page<Roles> rolesPage = rolesRepository.findRolesNotAssignedToUser(userId, pageable);
        return rolesPage.map(rolesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolesResponse> getRolesByPermissionId(Long permissionId, Pageable pageable) {
        log.info("Fetching roles with permission id: {}", permissionId);
        Page<Roles> rolesPage = rolesRepository.findRolesByPermissionId(permissionId, pageable);
        return rolesPage.map(rolesMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RolesResponse> getRoleWithPermissions(Long id) {
        log.info("Fetching role with permissions for id: {}", id);

        Optional<Roles> roleOpt = rolesRepository.findById(id);
        if (roleOpt.isEmpty()) {
            return Optional.empty();
        }

        Roles role = roleOpt.get();
        RolesResponse response = rolesMapper.toResponse(role);

        // Cargar los permisos del rol
        List<Permission> permissions = permissionRepository.findPermissionsByRoleIdList(id);
        List<PermissionResponse> permissionResponses = permissions.stream()
                .map(permissionMapper::toResponse)
                .toList();

        response.setPermissions(permissionResponses);

        return Optional.of(response);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRoleNameAvailable(String name) {
        return !rolesRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return rolesRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return rolesRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolesResponse> getRolesByUserIdList(Long userId) {
        log.info("Fetching roles list for user id: {}", userId);
        List<Roles> roles = rolesRepository.findRolesByUserIdList(userId);
        return roles.stream()
                .map(rolesMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countRolesByUserId(Long userId) {
        return rolesRepository.countRolesByUserId(userId);
    }

    @Override
    public void initializeDefaultRoles() {
        log.info("Initializing default roles");

        String[][] defaultRoles = {
                {"admin", "Administrator role with full system access"},
                {"host", "Host role for managing events and bookings"},
                {"cook", "Cook role for kitchen operations"}
        };

        for (String[] roleData : defaultRoles) {
            String roleName = roleData[0];
            String roleDescription = roleData[1];

            if (!rolesRepository.existsByName(roleName)) {
                Roles role = new Roles();
                role.setName(roleName);
                role.setDescription(roleDescription);
                rolesRepository.save(role);
                log.info("Created default role: {}", roleName);
            }
        }

        log.info("Default roles initialization completed");
    }
}
