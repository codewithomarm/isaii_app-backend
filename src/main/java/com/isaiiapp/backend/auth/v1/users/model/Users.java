package com.isaiiapp.backend.auth.v1.users.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "auth", name = "users",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "employee_id", name = "users_employeeId_UNIQUE")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Employee id should not be null")
    @Size(min = 7, max = 7, message = "Employee Id must be 7 characters")
    @Column(name = "employee_id", unique = true, nullable = false, length = 7)
    private String employeeId;

    @NotNull(message = "First name should not be null")
    @Size(min = 2, max = 150, message = "First Name should be between 2 and 150 characters")
    @Column(name = "first_name", nullable = false, length = 150)
    private String firstName;

    @NotNull(message = "Last name should not be null")
    @Size(min = 3, max = 150, message = "Last Name should be between 3 and 150 characters")
    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;

    @Column(name = "is_active")
    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
