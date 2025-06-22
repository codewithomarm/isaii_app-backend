package com.isaiiapp.backend.auth.v1.auth.model;

import com.isaiiapp.backend.auth.v1.users.model.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(schema = "auth", name = "auth",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "user_id", name = "auth_userId_UNIQUE")
        },
        indexes = {
            @Index(columnList = "user_id", name = "fk_auth_users_idx")
        })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @NotNull(message = "User should not be null")
    @JoinColumn(name = "user_id", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_auth_users"))
    private Users user;

    @NotNull(message = "Username should not be null")
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @NotNull(message = "Password Hash should not be null")
    @Size(min = 60, max = 150, message = "Password Hash should be between 60 and 150 characters")
    @Column(name = "password_hash", nullable = false, length = 150)
    private String passwordHash;

    @NotNull(message = "Enabled should not be null")
    @Column(nullable = false)
    private Boolean enabled;

    @Column(name = "recuperation_tkn", length = 10)
    @Size(min = 10, max = 10, message = "Recuperation Token must have 10 characters")
    private String recuperationTkn;

    @Column(name = "recuperation_tkn_exp")
    private LocalDateTime recuperationTknExp;

    @Column(name = "login_attempts")
    private Integer loginAttempts;

    @NotNull(message = "Created At should not be null")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        initializeTimeStamps();
    }

    public void initializeTimeStamps() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        if (createdAt == null) {
            createdAt = now;
        }
    }
}
