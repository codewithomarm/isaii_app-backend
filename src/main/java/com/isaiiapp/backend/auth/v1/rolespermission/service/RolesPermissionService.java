package com.isaiiapp.backend.auth.v1.rolespermission.service;

import com.isaiiapp.backend.auth.v1.rolespermission.dto.request.AssignRolePermissionRequest;
import com.isaiiapp.backend.auth.v1.rolespermission.dto.response.RolesPermissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RolesPermissionService {

    /**
     * Asignar permiso a rol
     */
    RolesPermissionResponse assignPermissionToRole(AssignRolePermissionRequest request);

    /**
     * Remover permiso de rol
     */
    void removePermissionFromRole(Long roleId, Long permissionId);

    /**
     * Obtener todas las relaciones rol-permiso con paginación
     */
    Page<RolesPermissionResponse> getAllRolesPermissions(Pageable pageable);

    /**
     * Obtener relaciones por rol con paginación
     */
    Page<RolesPermissionResponse> getRelationsByRoleId(Long roleId, Pageable pageable);

    /**
     * Obtener relaciones por permiso con paginación
     */
    Page<RolesPermissionResponse> getRelationsByPermissionId(Long permissionId, Pageable pageable);

    /**
     * Buscar relaciones por nombre de rol con paginación
     */
    Page<RolesPermissionResponse> searchRelationsByRoleName(String roleName, Pageable pageable);

    /**
     * Buscar relaciones por nombre de permiso con paginación
     */
    Page<RolesPermissionResponse> searchRelationsByPermissionName(String permissionName, Pageable pageable);

    /**
     * Verificar si rol tiene permiso específico
     */
    boolean roleHasPermission(Long roleId, Long permissionId);

    /**
     * Verificar si rol tiene permiso por nombre
     */
    boolean roleHasPermissionByName(Long roleId, String permissionName);

    /**
     * Asignar múltiples permisos a un rol
     */
    List<RolesPermissionResponse> assignMultiplePermissionsToRole(Long roleId, List<Long> permissionIds);

    /**
     * Remover todos los permisos de un rol
     */
    void removeAllPermissionsFromRole(Long roleId);

    /**
     * Remover un permiso de todos los roles
     */
    void removePermissionFromAllRoles(Long permissionId);

    /**
     * Reemplazar todos los permisos de un rol
     */
    List<RolesPermissionResponse> replaceRolePermissions(Long roleId, List<Long> permissionIds);

    /**
     * Contar relaciones por rol
     */
    Long countRelationsByRoleId(Long roleId);

    /**
     * Contar relaciones por permiso
     */
    Long countRelationsByPermissionId(Long permissionId);

    /**
     * Obtener estadísticas de relaciones rol-permiso
     */
    RolePermissionStatsResponse getRolePermissionStats();

    /**
     * DTO para estadísticas de relaciones rol-permiso
     */
    record RolePermissionStatsResponse(
            Long totalRelations,
            Long rolesWithPermissions,
            Long permissionsAssigned
    ) {}
}
