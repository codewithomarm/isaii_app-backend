package com.isaiiapp.backend.auth.v1.usersroles.repository;

import com.isaiiapp.backend.auth.v1.usersroles.model.UsersRoles;
import com.isaiiapp.backend.auth.v1.usersroles.model.UsersRolesId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRolesRepository extends JpaRepository<UsersRoles, UsersRolesId> {

    @Query("SELECT ur FROM UsersRoles ur WHERE ur.user.id = :userId")
    Page<UsersRoles> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT ur FROM UsersRoles ur WHERE ur.role.id = :roleId")
    Page<UsersRoles> findByRoleId(@Param("roleId") Long roleId, Pageable pageable);

    @Query("SELECT ur FROM UsersRoles ur WHERE LOWER(ur.user.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(ur.user.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<UsersRoles> findByUserNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT ur FROM UsersRoles ur WHERE LOWER(ur.role.name) LIKE LOWER(CONCAT('%', :roleName, '%'))")
    Page<UsersRoles> findByRoleNameContaining(@Param("roleName") String roleName, Pageable pageable);

    @Query("SELECT ur FROM UsersRoles ur WHERE ur.user.employeeId LIKE CONCAT('%', :employeeId, '%')")
    Page<UsersRoles> findByEmployeeIdContaining(@Param("employeeId") String employeeId, Pageable pageable);

    @Query("SELECT ur FROM UsersRoles ur WHERE ur.role.id = :roleId AND ur.user.isActive = true")
    Page<UsersRoles> findActiveUsersByRoleId(@Param("roleId") Long roleId, Pageable pageable);

    @Query("SELECT COUNT(ur) > 0 FROM UsersRoles ur WHERE ur.user.id = :userId AND ur.role.id = :roleId")
    boolean existsByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Query("SELECT ur FROM UsersRoles ur WHERE ur.user.id = :userId AND ur.role.id = :roleId")
    Optional<UsersRoles> findByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Modifying
    @Query("DELETE FROM UsersRoles ur WHERE ur.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM UsersRoles ur WHERE ur.role.id = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT COUNT(ur) FROM UsersRoles ur WHERE ur.role.id = :roleId")
    Long countUsersByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT COUNT(ur) FROM UsersRoles ur WHERE ur.role.id = :roleId AND ur.user.isActive = true")
    Long countActiveUsersByRoleId(@Param("roleId") Long roleId);
    
    @Query("SELECT COUNT(ur) FROM UsersRoles ur WHERE ur.user.id = :userId")
    Long countRolesByUserId(@Param("userId") Long userId);
}
