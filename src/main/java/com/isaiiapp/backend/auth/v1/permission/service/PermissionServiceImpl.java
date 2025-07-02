package com.isaiiapp.backend.auth.v1.permission.service;

import com.isaiiapp.backend.auth.v1.exception.DuplicateResourceException;
import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.auth.v1.permission.dto.request.CreatePermissionRequest;
import com.isaiiapp.backend.auth.v1.permission.dto.request.UpdatePermissionRequest;
import com.isaiiapp.backend.auth.v1.permission.dto.response.PermissionResponse;
import com.isaiiapp.backend.auth.v1.permission.mapper.PermissionMapper;
import com.isaiiapp.backend.auth.v1.permission.model.Permission;
import com.isaiiapp.backend.auth.v1.permission.repository.PermissionRepository;
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
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        log.info("Creating new permission with name: {}", request.getName());

        // Verificar si ya existe un permiso con el mismo nombre
        if (permissionRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Permission already exists with name: " + request.getName());
        }

        Permission permission = permissionMapper.toEntity(request);
        Permission savedPermission = permissionRepository.save(permission);

        log.info("Permission created successfully with id: {}", savedPermission.getId());
        return permissionMapper.toResponse(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PermissionResponse> getPermissionById(Long id) {
        log.info("Fetching permission by id: {}", id);
        Optional<Permission> permission = permissionRepository.findById(id);
        return permission.map(permissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PermissionResponse> getPermissionByName(String name) {
        log.info("Fetching permission by name: {}", name);
        Optional<Permission> permission = permissionRepository.findByName(name);
        return permission.map(permissionMapper::toResponse);
    }

    @Override
    public PermissionResponse updatePermission(Long id, UpdatePermissionRequest request) {
        log.info("Updating permission with id: {}", id);

        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));

        // Verificar si el nuevo nombre ya existe (excluyendo el permiso actual)
        if (request.getName() != null &&
                !existingPermission.getName().equals(request.getName()) &&
                permissionRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Permission already exists with name: " + request.getName());
        }

        // Actualizar campos solo si no son null
        if (request.getName() != null) {
            existingPermission.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existingPermission.setDescription(request.getDescription());
        }

        Permission updatedPermission = permissionRepository.save(existingPermission);
        log.info("Permission updated successfully with id: {}", updatedPermission.getId());

        return permissionMapper.toResponse(updatedPermission);
    }

    @Override
    public void deletePermission(Long id) {
        log.info("Deleting permission with id: {}", id);

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));

        // Verificar si el permiso está asignado a algún rol
        Long rolesCount = permissionRepository.countPermissionsByRoleId(id);
        if (rolesCount > 0) {
            throw new IllegalStateException("Cannot delete permission assigned to roles. Please remove from roles first.");
        }

        permissionRepository.delete(permission);
        log.info("Permission deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> getAllPermissions(Pageable pageable) {
        log.info("Fetching all permissions with pagination: {}", pageable);
        Page<Permission> permissionsPage = permissionRepository.findAll(pageable);
        return permissionsPage.map(permissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> searchPermissionsByName(String name, Pageable pageable) {
        log.info("Searching permissions by name: {}", name);
        Page<Permission> permissionsPage = permissionRepository.findByNameContaining(name, pageable);
        return permissionsPage.map(permissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> searchPermissionsByDescription(String description, Pageable pageable) {
        log.info("Searching permissions by description: {}", description);
        Page<Permission> permissionsPage = permissionRepository.findByDescriptionContaining(description, pageable);
        return permissionsPage.map(permissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> getPermissionsByRoleId(Long roleId, Pageable pageable) {
        log.info("Fetching permissions for role id: {}", roleId);
        Page<Permission> permissionsPage = permissionRepository.findPermissionsByRoleId(roleId, pageable);
        return permissionsPage.map(permissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> getPermissionsNotAssignedToRole(Long roleId, Pageable pageable) {
        log.info("Fetching permissions not assigned to role id: {}", roleId);
        Page<Permission> permissionsPage = permissionRepository.findPermissionsNotAssignedToRole(roleId, pageable);
        return permissionsPage.map(permissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> getPermissionsByUserId(Long userId, Pageable pageable) {
        log.info("Fetching permissions for user id: {}", userId);
        Page<Permission> permissionsPage = permissionRepository.findPermissionsByUserId(userId, pageable);
        return permissionsPage.map(permissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPermissionNameAvailable(String name) {
        return !permissionRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return permissionRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByRoleIdList(Long roleId) {
        log.info("Fetching permissions list for role id: {}", roleId);
        List<Permission> permissions = permissionRepository.findPermissionsByRoleIdList(roleId);
        return permissions.stream()
                .map(permissionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByUserIdList(Long userId) {
        log.info("Fetching permissions list for user id: {}", userId);
        List<Permission> permissions = permissionRepository.findPermissionsByUserIdList(userId);
        return permissions.stream()
                .map(permissionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countPermissionsByRoleId(Long roleId) {
        return permissionRepository.countPermissionsByRoleId(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countPermissionsByUserId(Long userId) {
        return permissionRepository.countPermissionsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasPermission(Long userId, String permissionName) {
        log.info("Checking if user {} has permission: {}", userId, permissionName);
        List<Permission> userPermissions = permissionRepository.findPermissionsByUserIdList(userId);
        return userPermissions.stream()
                .anyMatch(permission -> permission.getName().equals(permissionName));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasAnyPermission(Long userId, List<String> permissionNames) {
        log.info("Checking if user {} has any of permissions: {}", userId, permissionNames);
        List<Permission> userPermissions = permissionRepository.findPermissionsByUserIdList(userId);
        List<String> userPermissionNames = userPermissions.stream()
                .map(Permission::getName)
                .toList();

        return permissionNames.stream()
                .anyMatch(userPermissionNames::contains);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasAllPermissions(Long userId, List<String> permissionNames) {
        log.info("Checking if user {} has all permissions: {}", userId, permissionNames);
        List<Permission> userPermissions = permissionRepository.findPermissionsByUserIdList(userId);
        List<String> userPermissionNames = userPermissions.stream()
                .map(Permission::getName)
                .toList();

        return userPermissionNames.containsAll(permissionNames);
    }

    @Override
    public void initializeDefaultPermissions() {
        log.info("Initializing default permissions");

        String[] defaultPermissions = {
                "USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE",
                "ROLE_CREATE", "ROLE_READ", "ROLE_UPDATE", "ROLE_DELETE",
                "PERMISSION_CREATE", "PERMISSION_READ", "PERMISSION_UPDATE", "PERMISSION_DELETE",
                "SYSTEM_ADMIN", "SYSTEM_CONFIG"
        };

        for (String permissionName : defaultPermissions) {
            if (!permissionRepository.existsByName(permissionName)) {
                Permission permission = new Permission();
                permission.setName(permissionName);
                permission.setDescription("Default system permission: " + permissionName);
                permissionRepository.save(permission);
                log.info("Created default permission: {}", permissionName);
            }
        }

        log.info("Default permissions initialization completed");
    }

}
