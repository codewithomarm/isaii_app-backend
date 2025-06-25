package com.isaiiapp.backend.auth.v1.usersroles.service;

import com.isaiiapp.backend.auth.v1.usersroles.dto.request.AssignUserRoleRequest;
import com.isaiiapp.backend.auth.v1.usersroles.dto.response.UsersRolesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsersRolesService {

    /**
     * Asignar rol a usuario
     */
    UsersRolesResponse assignRoleToUser(AssignUserRoleRequest request);

    /**
     * Remover rol de usuario
     */
    void removeRoleFromUser(Long userId, Long roleId);

    /**
     * Obtener todas las relaciones usuario-rol con paginación
     */
    Page<UsersRolesResponse> getAllUsersRoles(Pageable pageable);

    /**
     * Obtener relaciones por usuario con paginación
     */
    Page<UsersRolesResponse> getRelationsByUserId(Long userId, Pageable pageable);

    /**
     * Obtener relaciones por rol con paginación
     */
    Page<UsersRolesResponse> getRelationsByRoleId(Long roleId, Pageable pageable);

    /**
     * Buscar relaciones por nombre de usuario con paginación
     */
    Page<UsersRolesResponse> searchRelationsByUserName(String name, Pageable pageable);

    /**
     * Buscar relaciones por nombre de rol con paginación
     */
    Page<UsersRolesResponse> searchRelationsByRoleName(String roleName, Pageable pageable);

    /**
     * Buscar relaciones por employeeId con paginación
     */
    Page<UsersRolesResponse> searchRelationsByEmployeeId(String employeeId, Pageable pageable);

    /**
     * Obtener usuarios activos con un rol específico con paginación
     */
    Page<UsersRolesResponse> getActiveUsersByRoleId(Long roleId, Pageable pageable);

    /**
     * Verificar si usuario tiene rol específico
     */
    boolean userHasRole(Long userId, Long roleId);

    /**
     * Verificar si usuario tiene rol por nombre
     */
    boolean userHasRoleByName(Long userId, String roleName);

    /**
     * Verificar si usuario tiene alguno de los roles
     */
    boolean userHasAnyRole(Long userId, List<String> roleNames);

    /**
     * Verificar si usuario tiene todos los roles
     */
    boolean userHasAllRoles(Long userId, List<String> roleNames);

    /**
     * Asignar múltiples roles a un usuario
     */
    List<UsersRolesResponse> assignMultipleRolesToUser(Long userId, List<Long> roleIds);

    /**
     * Remover todos los roles de un usuario
     */
    void removeAllRolesFromUser(Long userId);

    /**
     * Remover un rol de todos los usuarios
     */
    void removeRoleFromAllUsers(Long roleId);

    /**
     * Reemplazar todos los roles de un usuario
     */
    List<UsersRolesResponse> replaceUserRoles(Long userId, List<Long> roleIds);

    /**
     * Contar usuarios con un rol específico
     */
    Long countUsersByRoleId(Long roleId);

    /**
     * Contar usuarios activos con un rol específico
     */
    Long countActiveUsersByRoleId(Long roleId);

    /**
     * Contar roles de un usuario
     */
    Long countRolesByUserId(Long userId);

    /**
     * Obtener estadísticas de relaciones usuario-rol
     */
    UserRoleStatsResponse getUserRoleStats();

    /**
     * DTO para estadísticas de relaciones usuario-rol
     */
    record UserRoleStatsResponse(
            Long totalRelations,
            Long usersWithRoles,
            Long rolesAssigned
    ) {}
}
