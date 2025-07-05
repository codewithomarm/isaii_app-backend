package com.isaiiapp.backend.security.v1.service;

import com.isaiiapp.backend.auth.v1.auth.model.Auth;
import com.isaiiapp.backend.auth.v1.auth.repository.AuthRepository;
import com.isaiiapp.backend.auth.v1.permission.model.Permission;
import com.isaiiapp.backend.auth.v1.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService  implements UserDetailsService {

    private final AuthRepository authRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        Auth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        // Verificar si la cuenta está habilitada
        if (!auth.getEnabled()) {
            log.warn("Account is disabled for username: {}", username);
            throw new UsernameNotFoundException("Account is disabled for username: " + username);
        }

        // Verificar si el usuario está activo
        if (!auth.getUser().getIsActive()) {
            log.warn("User is inactive for username: {}", username);
            throw new UsernameNotFoundException("User is inactive for username: " + username);
        }

        // Cargar permisos del usuario
        List<Permission> permissions = permissionRepository.findPermissionsByUserIdList(auth.getUser().getId());
        List<GrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission.getName()))
                .collect(Collectors.toList());

        // Agregar roles como authorities también
        authorities.addAll(
                permissionRepository.findPermissionsByUserIdList(auth.getUser().getId()).stream()
                        .map(permission -> new SimpleGrantedAuthority("ROLE_USER"))
                        .distinct()
                        .collect(Collectors.toList())
        );

        log.debug("User loaded successfully: {} with {} authorities", username, authorities.size());

        return User.builder()
                .username(auth.getUsername())
                .password(auth.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!auth.getEnabled())
                .build();
    }
}
