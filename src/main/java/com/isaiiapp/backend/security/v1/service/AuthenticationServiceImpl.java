package com.isaiiapp.backend.security.v1.service;

import com.isaiiapp.backend.auth.v1.auth.service.AuthService;
import com.isaiiapp.backend.auth.v1.exception.AccountLockedException;
import com.isaiiapp.backend.auth.v1.session.model.Session;
import com.isaiiapp.backend.auth.v1.session.service.SessionService;
import com.isaiiapp.backend.auth.v1.users.mapper.UsersMapper;
import com.isaiiapp.backend.security.v1.dto.request.LoginRequest;
import com.isaiiapp.backend.security.v1.dto.request.RefreshTokenRequest;
import com.isaiiapp.backend.security.v1.dto.response.AuthenticationResponse;
import com.isaiiapp.backend.security.v1.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService{

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthService authService;
    private final SessionService sessionService;
    private final UsersMapper usersMapper;

    @Override
    public AuthenticationResponse authenticate(LoginRequest request) {
        log.info("Authenticating user: {}", request.getUsername());

        try {
            // Verificar si la cuenta está bloqueada
            if (authService.isAccountLocked(request.getUsername(), 5)) {
                log.warn("Cuenta bloqueada para usuario: {}", request.getUsername());
                throw new AccountLockedException("Account is locked due to too many failed login attempts");
            }

            // Autenticar con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Cargar detalles del usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

            // Generar tokens
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            // Obtener información del usuario
            var authResponse = authService.getAuthByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Crear sesión
            sessionService.createSession(authResponse.getUser().getId(), accessToken, refreshToken);

            // Resetear intentos de login
            authService.resetLoginAttempts(request.getUsername());

            // Construir respuesta
            AuthenticationResponse response = new AuthenticationResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setExpiresIn(jwtService.getAccessTokenExpiration());
            response.setExpiresAt(LocalDateTime.now().plusSeconds(jwtService.getAccessTokenExpiration() / 1000));
            response.setUser(authResponse.getUser());

            log.info("Authentication successful for user: {}", request.getUsername());
            return response;

        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.getUsername(), e);

            // Solo incrementar si no es bloqueo
            if (!(e instanceof AccountLockedException)) {
                try {
                    authService.incrementLoginAttempts(request.getUsername());
                    System.out.println("Intento fallido para: " + request.getUsername());
                } catch (Exception ex) {
                    log.warn("Could not increment login attempts for user: {}", request.getUsername());
                }
            }

            throw e; // Relanzar excepción original para que llegue al handler global
        }
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing token");

        try {
            // Validar refresh token
            if (!jwtService.isTokenValid(request.getRefreshToken())) {
                throw new RuntimeException("Invalid refresh token");
            }

            // Extraer username del refresh token
            String username = jwtService.extractUsername(request.getRefreshToken());

            // Verificar que la sesión existe y es válida
            Session session = sessionService.getValidSessionByRefreshToken(request.getRefreshToken())
                    .orElseThrow(() -> new RuntimeException("Invalid or expired refresh token"));

            // Cargar detalles del usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Generar nuevos tokens
            String newAccessToken = jwtService.generateAccessToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            // Actualizar sesión
            sessionService.renewSession(request.getRefreshToken(), newAccessToken, newRefreshToken);

            // Obtener información del usuario
            var authResponse = authService.getAuthByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Construir respuesta
            AuthenticationResponse response = new AuthenticationResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);
            response.setExpiresIn(jwtService.getAccessTokenExpiration());
            response.setExpiresAt(LocalDateTime.now().plusSeconds(jwtService.getAccessTokenExpiration() / 1000));
            response.setUser(authResponse.getUser());

            log.info("Token refresh successful for user: {}", username);
            return response;

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }

    @Override
    public void logout(String accessToken) {
        log.info("Logging out user");

        try {
            // Desactivar sesión por token
            sessionService.deactivateSessionByToken(accessToken);
            log.info("Logout successful");
        } catch (Exception e) {
            log.error("Logout failed", e);
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }

}
