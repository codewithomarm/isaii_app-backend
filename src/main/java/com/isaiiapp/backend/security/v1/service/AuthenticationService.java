package com.isaiiapp.backend.security.v1.service;

import com.isaiiapp.backend.security.v1.dto.request.LoginRequest;
import com.isaiiapp.backend.security.v1.dto.request.RefreshTokenRequest;
import com.isaiiapp.backend.security.v1.dto.response.AuthenticationResponse;

public interface AuthenticationService {

    /**
     * Autenticar usuario y generar tokens
     */
    AuthenticationResponse authenticate(LoginRequest request);

    /**
     * Refrescar tokens usando refresh token
     */
    AuthenticationResponse refreshToken(RefreshTokenRequest request);

    /**
     * Cerrar sesión (invalidar token)
     */
    void logout(String accessToken);
}
