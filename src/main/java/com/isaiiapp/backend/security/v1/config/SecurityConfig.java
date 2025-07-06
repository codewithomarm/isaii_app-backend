package com.isaiiapp.backend.security.v1.config;

import com.isaiiapp.backend.security.v1.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers(
                                "/v1/auth/login",
                                "/v1/auth/refresh",
                                "/v1/auth/register",
                                "/v1/auth/forgot-password",
                                "/v1/auth/reset-password",
                                "/v1/test/public",
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()

                        // Endpoints de test
                        .requestMatchers("/v1/test/**").permitAll()  // ✅ AGREGADO: todos los endpoints de test públicos

                        // Endpoints de administración
                        .requestMatchers("/v1/admin/**").hasAuthority("PERMISSION_SYSTEM_ADMIN")

                        // Endpoints de usuarios
                        .requestMatchers("/v1/users/**").hasAnyAuthority("PERMISSION_USER_READ", "PERMISSION_SYSTEM_ADMIN")

                        // Endpoints de roles
                        .requestMatchers("/v1/roles/**").hasAnyAuthority("PERMISSION_ROLE_READ", "PERMISSION_SYSTEM_ADMIN")

                        // Endpoints de permisos
                        .requestMatchers("/v1/permissions/**").hasAnyAuthority("PERMISSION_PERMISSION_READ", "PERMISSION_SYSTEM_ADMIN")

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
