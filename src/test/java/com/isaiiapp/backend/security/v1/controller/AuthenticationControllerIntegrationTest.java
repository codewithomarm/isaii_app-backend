package com.isaiiapp.backend.security.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaiiapp.backend.auth.v1.auth.model.Auth;
import com.isaiiapp.backend.auth.v1.auth.repository.AuthRepository;
import com.isaiiapp.backend.auth.v1.users.model.Users;
import com.isaiiapp.backend.auth.v1.users.repository.UsersRepository;
import com.isaiiapp.backend.security.v1.dto.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("docker")
public class AuthenticationControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private UsersRepository usersRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final String username = "usuario_test";
    private final String correctPassword = "clave_correcta_2025";
    private final String wrongPassword = "clave_incorrecta";

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/auth/login";
    }

    @BeforeEach
    @Transactional
    void setupUsuarioDePrueba() {
        // Eliminar Auth y Users previos si existen
        authRepository.findByUsername(username).ifPresent(auth -> {
            authRepository.delete(auth);
            usersRepository.deleteById(auth.getUser().getId());
        });

        // Crear un employeeId único para evitar conflictos
        String employeeId = "EMP" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        System.out.println("Usando employeeId: " + employeeId);

        // Crear Users
        Users nuevoUsuario = new Users();
        nuevoUsuario.setEmployeeId(employeeId);
        nuevoUsuario.setFirstName("Test");
        nuevoUsuario.setLastName("Usuario");
        nuevoUsuario.setIsActive(true);
        usersRepository.save(nuevoUsuario);

        // Crear Auth vinculado
        Auth nuevoAuth = new Auth();
        nuevoAuth.setUser(nuevoUsuario);
        nuevoAuth.setUsername(username);
        nuevoAuth.setPasswordHash(passwordEncoder.encode(correctPassword)); // no debe coincidir con "clave_incorrecta"
        nuevoAuth.setEnabled(true);
        nuevoAuth.setLoginAttempts(0);
        nuevoAuth.setRecuperationTkn(null);
        nuevoAuth.setRecuperationTknExp(null);
        nuevoAuth.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));

        authRepository.save(nuevoAuth);
    }

    @Test
    void whenMultipleFailedLoginAttempts_thenAccountIsLocked() throws Exception {
        for (int i = 1; i <= 5; i++) {
            ResponseEntity<String> response = attemptLogin(username, wrongPassword);
            assertThat(response.getStatusCode())
                    .as("Intento #" + i)
                    .isIn(HttpStatus.UNAUTHORIZED, HttpStatus.LOCKED);
        }

        // Intento adicional: debería estar bloqueado
        ResponseEntity<String> finalResponse = attemptLogin(username, wrongPassword);
        assertThat(finalResponse.getStatusCode()).isEqualTo(HttpStatus.LOCKED);
        assertThat(finalResponse.getBody()).contains("AccountLockedException");
    }

    private ResponseEntity<String> attemptLogin(String user, String pass) throws Exception {
        LoginRequest request = new LoginRequest(user, pass);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(
                objectMapper.writeValueAsString(request), headers
        );

        return restTemplate.postForEntity(getBaseUrl(), requestEntity, String.class);
    }

}
