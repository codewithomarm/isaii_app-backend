package com.isaiiapp.backend.auth.v1.users.repository;

import com.isaiiapp.backend.auth.v1.users.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);

    @Query("SELECT u FROM Users u WHERE u.isActive = true")
    Page<Users> findAllActive(Pageable pageable);

    @Query("SELECT u FROM Users u WHERE u.isActive = false")
    Page<Users> findAllInactive(Pageable pageable);

    @Query("SELECT u FROM Users u WHERE u.isActive = :isActive")
    Page<Users> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);

    @Query("SELECT u FROM Users u WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    Page<Users> findByFullNameContaining(@Param("fullName") String fullName, Pageable pageable);

    @Query("SELECT u FROM Users u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Users> findByFirstNameOrLastNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT u FROM Users u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))")
    Page<Users> findByFirstNameContaining(@Param("firstName") String firstName, Pageable pageable);

    @Query("SELECT u FROM Users u WHERE LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    Page<Users> findByLastNameContaining(@Param("lastName") String lastName, Pageable pageable);

    @Query("SELECT u FROM Users u WHERE u.employeeId LIKE CONCAT('%', :employeeId, '%')")
    Page<Users> findByEmployeeIdContaining(@Param("employeeId") String employeeId, Pageable pageable);

    @Query("SELECT COUNT(u) FROM Users u WHERE u.isActive = true")
    Long countActiveUsers();

    @Query("SELECT COUNT(u) FROM Users u WHERE u.isActive = false")
    Long countInactiveUsers();
}
