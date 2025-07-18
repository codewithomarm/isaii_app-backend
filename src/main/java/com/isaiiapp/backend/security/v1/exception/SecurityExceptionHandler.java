package com.isaiiapp.backend.security.v1.exception;

import com.isaiiapp.backend.auth.v1.exception.AccountLockedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class SecurityExceptionHandler {

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<String> handleAccountLocked(AccountLockedException ex) {
        log.warn("Account locked: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.LOCKED).body("AccountLockedException: " + ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        log.error("Bad credentials: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password", "INVALID_CREDENTIALS");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
        log.error("Username not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password", "INVALID_CREDENTIALS");
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleJwtAuthentication(JwtAuthenticationException ex) {
        log.error("JWT authentication error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or expired token", "INVALID_TOKEN");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(AuthenticationException ex) {
        log.error("Authentication error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication failed", "AUTHENTICATION_FAILED");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, String errorCode) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("errorCode", errorCode);

        return ResponseEntity.status(status).body(errorResponse);
    }
}
