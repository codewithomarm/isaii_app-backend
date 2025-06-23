package com.isaiiapp.backend.auth.v1.rolespermission.repository;

import com.isaiiapp.backend.auth.v1.rolespermission.model.RolesPermission;
import com.isaiiapp.backend.auth.v1.rolespermission.model.RolesPermissionId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesPermissionRepository extends JpaRepository<RolesPermission, RolesPermissionId> {

    @Query("SELECT rp FROM RolesPermission rp WHERE rp.role.id = :roleId")
    Page<RolesPermission> findByRoleId(@Param("roleId") Long roleId, Pageable pageable);

    @Query("SELECT rp FROM RolesPermission rp WHERE rp.permission.id = :permissionId")
    Page<RolesPermission> findByPermissionId(@Param("permissionId") Long permissionId, Pageable pageable);

    @Query("SELECT rp FROM RolesPermission rp WHERE LOWER(rp.role.name) LIKE LOWER(CONCAT('%', :roleName, '%'))")
    Page<RolesPermission> findByRoleNameContaining(@Param("roleName") String roleName, Pageable pageable);

    @Query("SELECT rp FROM RolesPermission rp WHERE LOWER(rp.permission.name) LIKE LOWER(CONCAT('%', :permissionName, '%'))")
    Page<RolesPermission> findByPermissionNameContaining(@Param("permissionName") String permissionName, Pageable pageable);

    @Query("SELECT COUNT(rp) > 0 FROM RolesPermission rp WHERE rp.role.id = :roleId AND rp.permission.id = :permissionId")
    boolean existsByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    @Query("SELECT rp FROM RolesPermission rp WHERE rp.role.id = :roleId AND rp.permission.id = :permissionId")
    Optional<RolesPermission> findByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    @Modifying
    @Query("DELETE FROM RolesPermission rp WHERE rp.role.id = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Modifying
    @Query("DELETE FROM RolesPermission rp WHERE rp.permission.id = :permissionId")
    void deleteByPermissionId(@Param("permissionId") Long permissionId);

    @Query("SELECT COUNT(rp) FROM RolesPermission rp WHERE rp.role.id = :roleId")
    Long countByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT COUNT(rp) FROM RolesPermission rp WHERE rp.permission.id = :permissionId")
    Long countByPermissionId(@Param("permissionId") Long permissionId);
}
