package com.isaiiapp.backend.auth.v1.rolespermission.service;

import com.isaiiapp.backend.auth.v1.exception.DuplicateResourceException;
import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.auth.v1.permission.mapper.PermissionMapper;
import com.isaiiapp.backend.auth.v1.permission.model.Permission;
import com.isaiiapp.backend.auth.v1.permission.repository.PermissionRepository;
import com.isaiiapp.backend.auth.v1.roles.mapper.RolesMapper;
import com.isaiiapp.backend.auth.v1.roles.model.Roles;
import com.isaiiapp.backend.auth.v1.roles.repository.RolesRepository;
import com.isaiiapp.backend.auth.v1.rolespermission.dto.request.AssignRolePermissionRequest;
import com.isaiiapp.backend.auth.v1.rolespermission.dto.response.RolesPermissionResponse;
import com.isaiiapp.backend.auth.v1.rolespermission.model.RolesPermission;
import com.isaiiapp.backend.auth.v1.rolespermission.model.RolesPermissionId;
import com.isaiiapp.backend.auth.v1.rolespermission.repository.RolesPermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RolesPermissionServiceImpl implements RolesPermissionService {

    private final RolesPermissionRepository rolesPermissionRepository;
    private final RolesRepository rolesRepository;
    private final PermissionRepository permissionRepository;
    private final RolesMapper rolesMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public RolesPermissionResponse assignPermissionToRole(AssignRolePermissionRequest request) {
        log.info("Assigning permission {} to role {}", request.getPermissionId(), request.getRoleId());

        // Verificar que el rol existe
        Roles role = rolesRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

        // Verificar que el permiso existe
        Permission permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + request.getPermissionId()));

        // Verificar que la asignación no existe ya
        if (rolesPermissionRepository.existsByRoleIdAndPermissionId(request.getRoleId(), request.getPermissionId())) {
            throw new DuplicateResourceException("Permission is already assigned to this role");
        }

        // Crear la asignación
        RolesPermissionId id = new RolesPermissionId(request.getRoleId(), request.getPermissionId());
        RolesPermission rolesPermission = new RolesPermission();
        rolesPermission.setId(id);
        rolesPermission.setRole(role);
        rolesPermission.setPermission(permission);

        RolesPermission savedRolesPermission = rolesPermissionRepository.save(rolesPermission);
        log.info("Permission assigned successfully to role. Role: {}, Permission: {}",
                request.getRoleId(), request.getPermissionId());

        return mapToResponse(savedRolesPermission);
    }

    @Override
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        log.info("Removing permission {} from role {}", permissionId, roleId);

        RolesPermission rolesPermission = rolesPermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Role-Permission assignment not found"));

        rolesPermissionRepository.delete(rolesPermission);
        log.info("Permission removed successfully from role. Role: {}, Permission: {}", roleId, permissionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolesPermissionResponse> getAllRolesPermissions(Pageable pageable) {
        log.info("Fetching all roles permissions with pagination: {}", pageable);
        Page<RolesPermission> rolesPermissionsPage = rolesPermissionRepository.findAll(pageable);
        return rolesPermissionsPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolesPermissionResponse> getRelationsByRoleId(Long roleId, Pageable pageable) {
        log.info("Fetching relations for role id: {}", roleId);

        // Verificar que el rol existe
        if (!rolesRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }

        Page<RolesPermission> rolesPermissionsPage = rolesPermissionRepository.findByRoleId(roleId, pageable);
        return rolesPermissionsPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolesPermissionResponse> getRelationsByPermissionId(Long permissionId, Pageable pageable) {
        log.info("Fetching relations for permission id: {}", permissionId);

        // Verificar que el permiso existe
        if (!permissionRepository.existsById(permissionId)) {
            throw new ResourceNotFoundException("Permission not found with id: " + permissionId);
        }

        Page<RolesPermission> rolesPermissionsPage = rolesPermissionRepository.findByPermissionId(permissionId, pageable);
        return rolesPermissionsPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolesPermissionResponse> searchRelationsByRoleName(String roleName, Pageable pageable) {
        log.info("Searching relations by role name: {}", roleName);
        Page<RolesPermission> rolesPermissionsPage = rolesPermissionRepository.findByRoleNameContaining(roleName, pageable);
        return rolesPermissionsPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RolesPermissionResponse> searchRelationsByPermissionName(String permissionName, Pageable pageable) {
        log.info("Searching relations by permission name: {}", permissionName);
        Page<RolesPermission> rolesPermissionsPage = rolesPermissionRepository.findByPermissionNameContaining(permissionName, pageable);
        return rolesPermissionsPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean roleHasPermission(Long roleId, Long permissionId) {
        return rolesPermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean roleHasPermissionByName(Long roleId, String permissionName) {
        log.info("Checking if role {} has permission: {}", roleId, permissionName);

        // Verificar que el rol existe
        if (!rolesRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }

        // Buscar el permiso por nombre
        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with name: " + permissionName));

        return rolesPermissionRepository.existsByRoleIdAndPermissionId(roleId, permission.getId());
    }

    @Override
    public List<RolesPermissionResponse> assignMultiplePermissionsToRole(Long roleId, List<Long> permissionIds) {
        log.info("Assigning multiple permissions to role {}: {}", roleId, permissionIds);

        // Verificar que el rol existe
        Roles role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        List<RolesPermissionResponse> responses = new ArrayList<>();

        for (Long permissionId : permissionIds) {
            // Verificar que el permiso existe
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));

            // Solo asignar si no existe ya
            if (!rolesPermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)) {
                RolesPermissionId id = new RolesPermissionId(roleId, permissionId);
                RolesPermission rolesPermission = new RolesPermission();
                rolesPermission.setId(id);
                rolesPermission.setRole(role);
                rolesPermission.setPermission(permission);

                RolesPermission savedRolesPermission = rolesPermissionRepository.save(rolesPermission);
                responses.add(mapToResponse(savedRolesPermission));
                log.info("Permission {} assigned to role {}", permissionId, roleId);
            } else {
                log.warn("Permission {} already assigned to role {}", permissionId, roleId);
            }
        }

        log.info("Multiple permissions assignment completed for role: {}", roleId);
        return responses;
    }

    @Override
    public void removeAllPermissionsFromRole(Long roleId) {
        log.info("Removing all permissions from role: {}", roleId);

        // Verificar que el rol existe
        if (!rolesRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }

        rolesPermissionRepository.deleteByRoleId(roleId);
        log.info("All permissions removed successfully from role: {}", roleId);
    }

    @Override
    public void removePermissionFromAllRoles(Long permissionId) {
        log.info("Removing permission {} from all roles", permissionId);

        // Verificar que el permiso existe
        if (!permissionRepository.existsById(permissionId)) {
            throw new ResourceNotFoundException("Permission not found with id: " + permissionId);
        }

        rolesPermissionRepository.deleteByPermissionId(permissionId);
        log.info("Permission removed successfully from all roles: {}", permissionId);
    }

    @Override
    public List<RolesPermissionResponse> replaceRolePermissions(Long roleId, List<Long> permissionIds) {
        log.info("Replacing permissions for role {}: {}", roleId, permissionIds);

        // Verificar que el rol existe
        if (!rolesRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }

        // Remover todos los permisos actuales
        removeAllPermissionsFromRole(roleId);

        // Asignar los nuevos permisos
        List<RolesPermissionResponse> responses = assignMultiplePermissionsToRole(roleId, permissionIds);

        log.info("Permissions replaced successfully for role: {}", roleId);
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countRelationsByRoleId(Long roleId) {
        return rolesPermissionRepository.countByRoleId(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countRelationsByPermissionId(Long permissionId) {
        return rolesPermissionRepository.countByPermissionId(permissionId);
    }

    @Override
    @Transactional(readOnly = true)
    public RolePermissionStatsResponse getRolePermissionStats() {
        log.info("Fetching role permission statistics");

        Long totalRelations = rolesPermissionRepository.count();
        Long rolesWithPermissions = rolesRepository.count(); // Simplificado
        Long permissionsAssigned = permissionRepository.count(); // Simplificado

        return new RolePermissionStatsResponse(totalRelations, rolesWithPermissions, permissionsAssigned);
    }

    private RolesPermissionResponse mapToResponse(RolesPermission rolesPermission) {
        RolesPermissionResponse response = new RolesPermissionResponse();
        response.setRole(rolesMapper.toResponse(rolesPermission.getRole()));
        response.setPermission(permissionMapper.toResponse(rolesPermission.getPermission()));
        return response;
    }
}
