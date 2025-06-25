package com.isaiiapp.backend.auth.v1.roles.service;

import com.isaiiapp.backend.auth.v1.roles.dto.request.CreateRolesRequest;
import com.isaiiapp.backend.auth.v1.roles.dto.request.UpdateRolesRequest;
import com.isaiiapp.backend.auth.v1.roles.dto.response.RolesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RolesService {

    /**
     * Crear nuevo rol
     */
    RolesResponse createRole(CreateRolesRequest request);

    /**
     * Obtener rol por ID
     */
    Optional<RolesResponse> getRoleById(Long id);

    /**
     * Obtener rol por nombre
     */
    Optional<RolesResponse> getRoleByName(String name);

    /**
     * Actualizar rol
     */
    RolesResponse updateRole(Long id, UpdateRolesRequest request);

    /**
     * Eliminar rol
     */
    void deleteRole(Long id);

    /**
     * Obtener todos los roles con paginación
     */
    Page<RolesResponse> getAllRoles(Pageable pageable);

    /**
     * Buscar roles por nombre con paginación
     */
    Page<RolesResponse> searchRolesByName(String name, Pageable pageable);

    /**
     * Buscar roles por descripción con paginación
     */
    Page<RolesResponse> searchRolesByDescription(String description, Pageable pageable);

    /**
     * Obtener roles de un usuario con paginación
     */
    Page<RolesResponse> getRolesByUserId(Long userId, Pageable pageable);

    /**
     * Obtener roles NO asignados a un usuario con paginación
     */
    Page<RolesResponse> getRolesNotAssignedToUser(Long userId, Pageable pageable);

    /**
     * Obtener roles que tienen un permiso específico con paginación
     */
    Page<RolesResponse> getRolesByPermissionId(Long permissionId, Pageable pageable);

    /**
     * Obtener rol con sus permisos
     */
    Optional<RolesResponse> getRoleWithPermissions(Long id);

    /**
     * Verificar si nombre de rol está disponible
     */
    boolean isRoleNameAvailable(String name);

    /**
     * Verificar si rol existe
     */
    boolean existsById(Long id);

    /**
     * Verificar si rol existe por nombre
     */
    boolean existsByName(String name);

    /**
     * Obtener roles de un usuario (sin paginación para uso interno)
     */
    List<RolesResponse> getRolesByUserIdList(Long userId);

    /**
     * Contar roles de un usuario
     */
    Long countRolesByUserId(Long userId);

    /**
     * Inicializar roles por defecto del sistema
     */
    void initializeDefaultRoles();
}
