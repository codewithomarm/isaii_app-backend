package com.isaiiapp.backend.auth.v1.auth.mapper;

import com.isaiiapp.backend.auth.v1.auth.dto.request.CreateAuthRequest;
import com.isaiiapp.backend.auth.v1.auth.dto.response.AuthResponse;
import com.isaiiapp.backend.auth.v1.auth.model.Auth;
import com.isaiiapp.backend.auth.v1.users.mapper.UsersMapper;
import com.isaiiapp.backend.auth.v1.users.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersMapper usersMapper;

    /**
     * Convertir CreateAuthRequest a Auth entity
     */
    public Auth toEntity(CreateAuthRequest request, Users user) {
        Auth auth = new Auth();
        auth.setUser(user);
        auth.setUsername(request.getUsername());
        auth.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        auth.setEnabled(request.getEnabled());
        auth.setLoginAttempts(0);
        return auth;
    }

    /**
     * Convertir Auth entity a AuthResponse
     */
    public AuthResponse toResponse(Auth auth) {
        AuthResponse response = new AuthResponse();
        response.setId(auth.getId());
        response.setUser(usersMapper.toResponse(auth.getUser()));
        response.setUsername(auth.getUsername());
        response.setEnabled(auth.getEnabled());
        response.setLoginAttempts(auth.getLoginAttempts());
        response.setCreatedAt(auth.getCreatedAt());
        response.setHasRecuperationToken(auth.getRecuperationTkn() != null);
        response.setRecuperationTokenExpiry(auth.getRecuperationTknExp());
        return response;
    }
}
