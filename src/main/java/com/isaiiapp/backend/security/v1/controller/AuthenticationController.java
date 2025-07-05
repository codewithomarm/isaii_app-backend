package com.isaiiapp.backend.security.v1.controller;

import com.isaiiapp.backend.security.v1.dto.request.LoginRequest;
import com.isaiiapp.backend.security.v1.dto.request.RefreshTokenRequest;
import com.isaiiapp.backend.security.v1.dto.response.AuthenticationResponse;
import com.isaiiapp.backend.security.v1.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        AuthenticationResponse response = authenticationService.authenticate(request);
        log.info("Login successful for username: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh attempt");
        AuthenticationResponse response = authenticationService.refreshToken(request);
        log.info("Token refresh successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        log.info("Logout attempt");
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        authenticationService.logout(token);
        log.info("Logout successful");
        return ResponseEntity.ok().build();
    }
}
