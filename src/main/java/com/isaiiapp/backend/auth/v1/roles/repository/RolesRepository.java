package com.isaiiapp.backend.auth.v1.roles.repository;

import com.isaiiapp.backend.auth.v1.roles.model.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT r FROM Roles r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Roles> findByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT r FROM Roles r WHERE LOWER(r.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Roles> findByDescriptionContaining(@Param("description") String description, Pageable pageable);

    @Query("SELECT r FROM Roles r WHERE r.id IN :ids")
    Page<Roles> findByIdIn(@Param("ids") List<Long> ids, Pageable pageable);

    @Query("SELECT r FROM Roles r JOIN UsersRoles ur ON r.id = ur.role.id WHERE ur.user.id = :userId")
    Page<Roles> findRolesByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Roles r WHERE r.id NOT IN (SELECT ur.role.id FROM UsersRoles ur WHERE ur.user.id = :userId)")
    Page<Roles> findRolesNotAssignedToUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Roles r JOIN RolesPermission rp ON r.id = rp.role.id WHERE rp.permission.id = :permissionId")
    Page<Roles> findRolesByPermissionId(@Param("permissionId") Long permissionId, Pageable pageable);

    @Query("SELECT r FROM Roles r JOIN UsersRoles ur ON r.id = ur.role.id WHERE ur.user.id = :userId")
    List<Roles> findRolesByUserIdList(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Roles r JOIN UsersRoles ur ON r.id = ur.role.id WHERE ur.user.id = :userId")
    Long countRolesByUserId(@Param("userId") Long userId);
}
