package com.isaiiapp.backend.auth.v1.users.service;

import com.isaiiapp.backend.auth.v1.exception.DuplicateResourceException;
import com.isaiiapp.backend.auth.v1.exception.ResourceNotFoundException;
import com.isaiiapp.backend.auth.v1.roles.dto.response.RolesResponse;
import com.isaiiapp.backend.auth.v1.roles.mapper.RolesMapper;
import com.isaiiapp.backend.auth.v1.roles.model.Roles;
import com.isaiiapp.backend.auth.v1.roles.repository.RolesRepository;
import com.isaiiapp.backend.auth.v1.users.dto.request.CreateUsersRequest;
import com.isaiiapp.backend.auth.v1.users.dto.request.UpdateUsersRequest;
import com.isaiiapp.backend.auth.v1.users.dto.response.UsersResponse;
import com.isaiiapp.backend.auth.v1.users.mapper.UsersMapper;
import com.isaiiapp.backend.auth.v1.users.model.Users;
import com.isaiiapp.backend.auth.v1.users.repository.UsersRepository;
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
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final UsersMapper usersMapper;
    private final RolesMapper rolesMapper;

    @Override
    public UsersResponse createUser(CreateUsersRequest request) {
        log.info("Creating user with employee ID: {}", request.getEmployeeId());

        // Verificar que el employee ID está disponible
        if (usersRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new DuplicateResourceException("User", "employeeId", request.getEmployeeId());
        }

        Users user = usersMapper.toEntity(request);
        Users savedUser = usersRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getId());
        return usersMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsersResponse> getUserById(Long id) {
        return usersRepository.findById(id)
                .map(usersMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsersResponse> getUserByEmployeeId(String employeeId) {
        return usersRepository.findByEmployeeId(employeeId)
                .map(usersMapper::toResponse);
    }

    @Override
    public UsersResponse updateUser(Long id, UpdateUsersRequest request) {
        log.info("Updating user with ID: {}", id);

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        Users updatedUser = usersRepository.save(user);
        log.info("User updated successfully with ID: {}", id);

        return usersMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!usersRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        usersRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersResponse> getAllUsers(Pageable pageable) {
        return usersRepository.findAll(pageable)
                .map(usersMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersResponse> getActiveUsers(Pageable pageable) {
        return usersRepository.findAllActive(pageable)
                .map(usersMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersResponse> getInactiveUsers(Pageable pageable) {
        return usersRepository.findAllInactive(pageable)
                .map(usersMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersResponse> searchUsersByFullName(String fullName, Pageable pageable) {
        return usersRepository.findByFullNameContaining(fullName, pageable)
                .map(usersMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersResponse> searchUsersByName(String name, Pageable pageable) {
        return usersRepository.findByFirstNameOrLastNameContaining(name, pageable)
                .map(usersMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsersResponse> searchUsersByEmployeeId(String employeeId, Pageable pageable) {
        return usersRepository.findByEmployeeIdContaining(employeeId, pageable)
                .map(usersMapper::toResponse);
    }

    @Override
    public void toggleUserStatus(Long id, Boolean isActive) {
        log.info("Toggling user status for ID: {} to {}", id, isActive);

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setIsActive(isActive);
        usersRepository.save(user);

        log.info("User status toggled successfully for ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmployeeIdAvailable(String employeeId) {
        return !usersRepository.existsByEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsersResponse> getUserWithRoles(Long id) {
        Optional<Users> userOpt = usersRepository.findById(id);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        Users user = userOpt.get();
        UsersResponse response = usersMapper.toResponse(user);

        // Cargar roles del usuario
        List<Roles> roles = rolesRepository.findRolesByUserIdList(id);
        List<RolesResponse> rolesResponse = roles.stream()
                .map(rolesMapper::toResponse)
                .toList();

        response.setRoles(rolesResponse);
        return Optional.of(response);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats() {
        Long totalUsers = usersRepository.count();
        Long activeUsers = usersRepository.countActiveUsers();
        Long inactiveUsers = usersRepository.countInactiveUsers();

        return new UserStatsResponse(totalUsers, activeUsers, inactiveUsers);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return usersRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmployeeId(String employeeId) {
        return usersRepository.existsByEmployeeId(employeeId);
    }


}
