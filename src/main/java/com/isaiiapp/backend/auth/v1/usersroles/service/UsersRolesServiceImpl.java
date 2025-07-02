package com.isaiiapp.backend.auth.v1.usersroles.service;

import com.isaiiapp.backend.auth.v1.exception.DuplicateResourceException;
import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.auth.v1.roles.mapper.RolesMapper;
import com.isaiiapp.backend.auth.v1.roles.model.Roles;
import com.isaiiapp.backend.auth.v1.roles.repository.RolesRepository;
import com.isaiiapp.backend.auth.v1.users.mapper.UsersMapper;
import com.isaiiapp.backend.auth.v1.users.model.Users;
import com.isaiiapp.backend.auth.v1.users.repository.UsersRepository;
import com.isaiiapp.backend.auth.v1.usersroles.dto.request.AssignUserRoleRequest;
import com.isaiiapp.backend.auth.v1.usersroles.dto.response.UsersRolesResponse;
import com.isaiiapp.backend.auth.v1.usersroles.model.UsersRoles;
import com.isaiiapp.backend.auth.v1.usersroles.model.UsersRolesId;
import com.isaiiapp.backend.auth.v1.usersroles.repository.UsersRolesRepository;
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
public class UsersRolesServiceImpl implements UsersRolesService {

    private final UsersRolesRepository usersRolesRepository;
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final UsersMapper usersMapper;
    private final RolesMapper rolesMapper;

    @Override
    public UsersRolesResponse assignRoleToUser(AssignUserRoleRequest request) {
        log.info("Assigning role {} to user {}", request.getRoleId(), request.getUserId());

        // Verificar que el usuario existe
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        // Verificar que el rol existe
        Roles role = rolesRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

        // Verificar que la asignación no existe ya
        if (usersRolesRepository.existsByUserIdAndRoleId(request.getUserId(), request.getRoleId())) {
            throw new DuplicateResourceException("Role is already assigned to this user");
        }

        // Crear la asignación
        UsersRolesId id = new UsersRolesId(request.getUserId(), request.getRoleId());
        UsersRoles usersRoles = new UsersRoles();
        usersRoles.setId(id);
        usersRoles.setUser(user);
        usersRoles.setRole(role);

        UsersRoles savedUsersRoles = usersRolesRepository.save(usersRoles);
        log.info("Role assigned successfully to user. User: {}, Role: {}",
                request.getUserId(), request.getRoleId());

        return mapToResponse(savedUsersRoles);
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        log.info("Removing role {} from user {}", roleId, userId);

        UsersRoles usersRoles = usersRolesRepository.findByUserIdAndRoleId(userId, roleId)
                .orElseThrow(() -> new ResourceNotFoundException("User-Role assignment not found"));

        usersRolesRepository.delete(usersRoles);
        log.info("Role removed successfully from user. User: {}, Role: {}", userId, roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersRolesResponse> getAllUsersRoles(Pageable pageable) {
        log.info("Fetching all users roles with pagination: {}", pageable);
        Page<UsersRoles> usersRolesPage = usersRolesRepository.findAll(pageable);
        return usersRolesPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersRolesResponse> getRelationsByUserId(Long userId, Pageable pageable) {
        log.info("Fetching relations for user id: {}", userId);

        // Verificar que el usuario existe
        if (!usersRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        Page<UsersRoles> usersRolesPage = usersRolesRepository.findByUserId(userId, pageable);
        return usersRolesPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersRolesResponse> getRelationsByRoleId(Long roleId, Pageable pageable) {
        log.info("Fetching relations for role id: {}", roleId);

        // Verificar que el rol existe
        if (!rolesRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }

        Page<UsersRoles> usersRolesPage = usersRolesRepository.findByRoleId(roleId, pageable);
        return usersRolesPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersRolesResponse> searchRelationsByUserName(String name, Pageable pageable) {
        log.info("Searching relations by user name: {}", name);
        Page<UsersRoles> usersRolesPage = usersRolesRepository.findByUserNameContaining(name, pageable);
        return usersRolesPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersRolesResponse> searchRelationsByRoleName(String roleName, Pageable pageable) {
        log.info("Searching relations by role name: {}", roleName);
        Page<UsersRoles> usersRolesPage = usersRolesRepository.findByRoleNameContaining(roleName, pageable);
        return usersRolesPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersRolesResponse> searchRelationsByEmployeeId(String employeeId, Pageable pageable) {
        log.info("Searching relations by employee id: {}", employeeId);
        Page<UsersRoles> usersRolesPage = usersRolesRepository.findByEmployeeIdContaining(employeeId, pageable);
        return usersRolesPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersRolesResponse> getActiveUsersByRoleId(Long roleId, Pageable pageable) {
        log.info("Fetching active users for role id: {}", roleId);

        // Verificar que el rol existe
        if (!rolesRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }

        Page<UsersRoles> usersRolesPage = usersRolesRepository.findActiveUsersByRoleId(roleId, pageable);
        return usersRolesPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasRole(Long userId, Long roleId) {
        return usersRolesRepository.existsByUserIdAndRoleId(userId, roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasRoleByName(Long userId, String roleName) {
        log.info("Checking if user {} has role: {}", userId, roleName);

        // Verificar que el usuario existe
        if (!usersRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // Buscar el rol por nombre
        Roles role = rolesRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));

        return usersRolesRepository.existsByUserIdAndRoleId(userId, role.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasAnyRole(Long userId, List<String> roleNames) {
        log.info("Checking if user {} has any of roles: {}", userId, roleNames);

        // Verificar que el usuario existe
        if (!usersRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        for (String roleName : roleNames) {
            try {
                if (userHasRoleByName(userId, roleName)) {
                    return true;
                }
            } catch (ResourceNotFoundException e) {
                // Continuar si el rol no existe
                log.warn("Role not found: {}", roleName);
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasAllRoles(Long userId, List<String> roleNames) {
        log.info("Checking if user {} has all roles: {}", userId, roleNames);

        // Verificar que el usuario existe
        if (!usersRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        for (String roleName : roleNames) {
            if (!userHasRoleByName(userId, roleName)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<UsersRolesResponse> assignMultipleRolesToUser(Long userId, List<Long> roleIds) {
        log.info("Assigning multiple roles to user {}: {}", userId, roleIds);

        // Verificar que el usuario existe
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<UsersRolesResponse> responses = new ArrayList<>();

        for (Long roleId : roleIds) {
            // Verificar que el rol existe
            Roles role = rolesRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

            // Solo asignar si no existe ya
            if (!usersRolesRepository.existsByUserIdAndRoleId(userId, roleId)) {
                UsersRolesId id = new UsersRolesId(userId, roleId);
                UsersRoles usersRoles = new UsersRoles();
                usersRoles.setId(id);
                usersRoles.setUser(user);
                usersRoles.setRole(role);

                UsersRoles savedUsersRoles = usersRolesRepository.save(usersRoles);
                responses.add(mapToResponse(savedUsersRoles));
                log.info("Role {} assigned to user {}", roleId, userId);
            } else {
                log.warn("Role {} already assigned to user {}", roleId, userId);
            }
        }

        log.info("Multiple roles assignment completed for user: {}", userId);
        return responses;
    }

    @Override
    public void removeAllRolesFromUser(Long userId) {
        log.info("Removing all roles from user: {}", userId);

        // Verificar que el usuario existe
        if (!usersRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        usersRolesRepository.deleteByUserId(userId);
        log.info("All roles removed successfully from user: {}", userId);
    }

    @Override
    public void removeRoleFromAllUsers(Long roleId) {
        log.info("Removing role {} from all users", roleId);

        // Verificar que el rol existe
        if (!rolesRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }

        usersRolesRepository.deleteByRoleId(roleId);
        log.info("Role removed successfully from all users: {}", roleId);
    }

    @Override
    public List<UsersRolesResponse> replaceUserRoles(Long userId, List<Long> roleIds) {
        log.info("Replacing roles for user {}: {}", userId, roleIds);

        // Verificar que el usuario existe
        if (!usersRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // Remover todos los roles actuales
        removeAllRolesFromUser(userId);

        // Asignar los nuevos roles
        List<UsersRolesResponse> responses = assignMultipleRolesToUser(userId, roleIds);

        log.info("Roles replaced successfully for user: {}", userId);
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUsersByRoleId(Long roleId) {
        return usersRolesRepository.countUsersByRoleId(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveUsersByRoleId(Long roleId) {
        return usersRolesRepository.countActiveUsersByRoleId(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countRolesByUserId(Long userId) {
        return usersRolesRepository.countRolesByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleStatsResponse getUserRoleStats() {
        log.info("Fetching user role statistics");

        Long totalRelations = usersRolesRepository.count();
        Long usersWithRoles = usersRepository.count(); // Simplificado
        Long rolesAssigned = rolesRepository.count(); // Simplificado

        return new UserRoleStatsResponse(totalRelations, usersWithRoles, rolesAssigned);
    }

    private UsersRolesResponse mapToResponse(UsersRoles usersRoles) {
        UsersRolesResponse response = new UsersRolesResponse();
        response.setUser(usersMapper.toResponse(usersRoles.getUser()));
        response.setRole(rolesMapper.toResponse(usersRoles.getRole()));
        return response;
    }
}
