package com.isaiiapp.backend.auth.v1.permission.repository;

import com.isaiiapp.backend.auth.v1.permission.model.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT p FROM Permission p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Permission> findByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM Permission p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Permission> findByDescriptionContaining(@Param("description") String description, Pageable pageable);

    @Query("SELECT p FROM Permission p WHERE p.id IN :ids")
    Page<Permission> findByIdIn(@Param("ids") List<Long> ids, Pageable pageable);

    @Query("SELECT p FROM Permission p JOIN RolesPermission rp ON p.id = rp.permission.id WHERE rp.role.id = :roleId")
    Page<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId, Pageable pageable);

    @Query("SELECT p FROM Permission p WHERE p.id NOT IN (SELECT rp.permission.id FROM RolesPermission rp WHERE rp.role.id = :roleId)")
    Page<Permission> findPermissionsNotAssignedToRole(@Param("roleId") Long roleId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Permission p " +
            "JOIN RolesPermission rp ON p.id = rp.permission.id " +
            "JOIN UsersRoles ur ON rp.role.id = ur.role.id " +
            "WHERE ur.user.id = :userId")
    Page<Permission> findPermissionsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Permission p JOIN RolesPermission rp ON p.id = rp.permission.id WHERE rp.role.id = :roleId")
    List<Permission> findPermissionsByRoleIdList(@Param("roleId") Long roleId);

    @Query("SELECT DISTINCT p FROM Permission p " +
            "JOIN RolesPermission rp ON p.id = rp.permission.id " +
            "JOIN UsersRoles ur ON rp.role.id = ur.role.id " +
            "WHERE ur.user.id = :userId")
    List<Permission> findPermissionsByUserIdList(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Permission p JOIN RolesPermission rp ON p.id = rp.permission.id WHERE rp.role.id = :roleId")
    Long countPermissionsByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT COUNT(DISTINCT p) FROM Permission p " +
            "JOIN RolesPermission rp ON p.id = rp.permission.id " +
            "JOIN UsersRoles ur ON rp.role.id = ur.role.id " +
            "WHERE ur.user.id = :userId")
    Long countPermissionsByUserId(@Param("userId") Long userId);
}
