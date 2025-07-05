package com.isaiiapp.backend.security.v1.jwt.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    /**
     * Generar token de acceso
     */
    String generateAccessToken(UserDetails userDetails);

    /**
     * Generar token de acceso con claims adicionales
     */
    String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails);

    /**
     * Generar refresh token
     */
    String generateRefreshToken(UserDetails userDetails);

    /**
     * Extraer username del token
     */
    String extractUsername(String token);

    /**
     * Extraer fecha de expiración del token
     */
    Date extractExpiration(String token);

    /**
     * Extraer claim específico del token
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    /**
     * Extraer todos los claims del token
     */
    Claims extractAllClaims(String token);

    /**
     * Verificar si el token está expirado
     */
    Boolean isTokenExpired(String token);

    /**
     * Validar token contra UserDetails
     */
    Boolean validateToken(String token, UserDetails userDetails);

    /**
     * Verificar si el token es válido (no expirado y formato correcto)
     */
    Boolean isTokenValid(String token);

    /**
     * Obtener tiempo de expiración del access token en milisegundos
     */
    Long getAccessTokenExpiration();

    /**
     * Obtener tiempo de expiración del refresh token en milisegundos
     */
    Long getRefreshTokenExpiration();
}
