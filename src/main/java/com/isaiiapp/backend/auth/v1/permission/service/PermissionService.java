package com.isaiiapp.backend.auth.v1.permission.service;

import com.isaiiapp.backend.auth.v1.permission.dto.request.CreatePermissionRequest;
import com.isaiiapp.backend.auth.v1.permission.dto.request.UpdatePermissionRequest;
import com.isaiiapp.backend.auth.v1.permission.dto.response.PermissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PermissionService {

    /**
     * Crear nuevo permiso
     */
    PermissionResponse createPermission(CreatePermissionRequest request);

    /**
     * Obtener permiso por ID
     */
    Optional<PermissionResponse> getPermissionById(Long id);

    /**
     * Obtener permiso por nombre
     */
    Optional<PermissionResponse> getPermissionByName(String name);

    /**
     * Actualizar permiso
     */
    PermissionResponse updatePermission(Long id, UpdatePermissionRequest request);

    /**
     * Eliminar permiso
     */
    void deletePermission(Long id);

    /**
     * Obtener todos los permisos con paginación
     */
    Page<PermissionResponse> getAllPermissions(Pageable pageable);

    /**
     * Buscar permisos por nombre con paginación
     */
    Page<PermissionResponse> searchPermissionsByName(String name, Pageable pageable);

    /**
     * Buscar permisos por descripción con paginación
     */
    Page<PermissionResponse> searchPermissionsByDescription(String description, Pageable pageable);

    /**
     * Obtener permisos de un rol con paginación
     */
    Page<PermissionResponse> getPermissionsByRoleId(Long roleId, Pageable pageable);

    /**
     * Obtener permisos NO asignados a un rol con paginación
     */
    Page<PermissionResponse> getPermissionsNotAssignedToRole(Long roleId, Pageable pageable);

    /**
     * Obtener todos los permisos de un usuario (a través de roles) con paginación
     */
    Page<PermissionResponse> getPermissionsByUserId(Long userId, Pageable pageable);

    /**
     * Verificar si nombre de permiso está disponible
     */
    boolean isPermissionNameAvailable(String name);

    /**
     * Verificar si permiso existe
     */
    boolean existsById(Long id);

    /**
     * Verificar si permiso existe por nombre
     */
    boolean existsByName(String name);

    /**
     * Obtener permisos de un rol (sin paginación para uso interno)
     */
    List<PermissionResponse> getPermissionsByRoleIdList(Long roleId);

    /**
     * Obtener permisos de un usuario (sin paginación para uso interno)
     */
    List<PermissionResponse> getPermissionsByUserIdList(Long userId);

    /**
     * Contar permisos de un rol
     */
    Long countPermissionsByRoleId(Long roleId);

    /**
     * Contar permisos de un usuario
     */
    Long countPermissionsByUserId(Long userId);

    /**
     * Verificar si usuario tiene permiso específico
     */
    boolean userHasPermission(Long userId, String permissionName);

    /**
     * Verificar si usuario tiene alguno de los permisos
     */
    boolean userHasAnyPermission(Long userId, List<String> permissionNames);

    /**
     * Verificar si usuario tiene todos los permisos
     */
    boolean userHasAllPermissions(Long userId, List<String> permissionNames);

    /**
     * Inicializar permisos por defecto del sistema
     */
    void initializeDefaultPermissions();
}
