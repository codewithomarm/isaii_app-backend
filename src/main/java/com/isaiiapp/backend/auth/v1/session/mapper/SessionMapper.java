package com.isaiiapp.backend.auth.v1.session.mapper;

import com.isaiiapp.backend.auth.v1.session.dto.response.SessionResponse;
import com.isaiiapp.backend.auth.v1.session.model.Session;
import com.isaiiapp.backend.auth.v1.users.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SessionMapper {

    @Autowired
    private UsersMapper usersMapper;

    /**
     * Convertir Session entity a SessionResponse
     */
    public SessionResponse toResponse(Session session) {
        SessionResponse response = new SessionResponse();
        response.setId(session.getId());
        response.setUser(usersMapper.toResponse(session.getUser()));
        response.setAccessTokenPreview(getTokenPreview(session.getAccessToken()));
        response.setRefreshTokenPreview(getTokenPreview(session.getRefreshToken()));
        response.setAccessTokenExpiresAt(session.getAccessTokenExpiresAt());
        response.setRefreshTokenExpiresAt(session.getRefreshTokenExpiresAt());
        response.setCreatedAt(session.getCreatedAt());
        response.setLastActivityAt(session.getLastActivityAt());
        response.setIsActive(session.getIsActive());
        response.setIsExpired(isSessionExpired(session));
        return response;
    }

    /**
     * Crear preview seguro del token (primeros y últimos 4 caracteres)
     */
    private String getTokenPreview(String token) {
        if (token == null || token.length() < 8) {
            return "****";
        }
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }

    /**
     * Verificar si la sesión está expirada
     */
    private Boolean isSessionExpired(Session session) {
        LocalDateTime now = LocalDateTime.now();
        return session.getAccessTokenExpiresAt().isBefore(now) ||
                session.getRefreshTokenExpiresAt().isBefore(now);
    }
}
