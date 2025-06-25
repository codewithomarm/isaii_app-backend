package com.isaiiapp.backend.auth.v1.users.service;

import com.isaiiapp.backend.auth.v1.users.dto.request.CreateUsersRequest;
import com.isaiiapp.backend.auth.v1.users.dto.request.UpdateUsersRequest;
import com.isaiiapp.backend.auth.v1.users.dto.response.UsersResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UsersService {

    /**
     * Crear nuevo usuario
     */
    UsersResponse createUser(CreateUsersRequest request);

    /**
     * Obtener usuario por ID
     */
    Optional<UsersResponse> getUserById(Long id);

    /**
     * Obtener usuario por employee ID
     */
    Optional<UsersResponse> getUserByEmployeeId(String employeeId);

    /**
     * Actualizar usuario
     */
    UsersResponse updateUser(Long id, UpdateUsersRequest request);

    /**
     * Eliminar usuario
     */
    void deleteUser(Long id);

    /**
     * Obtener todos los usuarios con paginación
     */
    Page<UsersResponse> getAllUsers(Pageable pageable);

    /**
     * Obtener usuarios activos con paginación
     */
    Page<UsersResponse> getActiveUsers(Pageable pageable);

    /**
     * Obtener usuarios inactivos con paginación
     */
    Page<UsersResponse> getInactiveUsers(Pageable pageable);

    /**
     * Buscar usuarios por nombre completo con paginación
     */
    Page<UsersResponse> searchUsersByFullName(String fullName, Pageable pageable);

    /**
     * Buscar usuarios por firstName o lastName con paginación
     */
    Page<UsersResponse> searchUsersByName(String name, Pageable pageable);

    /**
     * Buscar usuarios por employeeId con paginación
     */
    Page<UsersResponse> searchUsersByEmployeeId(String employeeId, Pageable pageable);

    /**
     * Activar/desactivar usuario
     */
    void toggleUserStatus(Long id, Boolean isActive);

    /**
     * Verificar si employee ID está disponible
     */
    boolean isEmployeeIdAvailable(String employeeId);

    /**
     * Obtener usuario con sus roles
     */
    Optional<UsersResponse> getUserWithRoles(Long id);

    /**
     * Obtener estadísticas de usuarios
     */
    UserStatsResponse getUserStats();

    /**
     * Verificar si usuario existe
     */
    boolean existsById(Long id);

    /**
     * Verificar si usuario existe por employee ID
     */
    boolean existsByEmployeeId(String employeeId);

    /**
     * DTO para estadísticas de usuarios
     */
    record UserStatsResponse(
            Long totalUsers,
            Long activeUsers,
            Long inactiveUsers
    ) {}
}
