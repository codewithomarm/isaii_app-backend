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
                        // =====================================================
                        // ENDPOINTS PÚBLICOS
                        // =====================================================
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

                        // =====================================================
                        // ENDPOINTS DE TEST (DESARROLLO)
                        // =====================================================
                        .requestMatchers("/v1/test/**").permitAll()

                        // =====================================================
                        // MÓDULO AUTH - ADMINISTRACIÓN DE USUARIOS
                        // =====================================================
                        .requestMatchers("/v1/admin/**").hasAuthority("PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers("/v1/users/**").hasAnyAuthority("PERMISSION_USER_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers("/v1/roles/**").hasAnyAuthority("PERMISSION_ROLE_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers("/v1/permissions/**").hasAnyAuthority("PERMISSION_PERMISSION_READ", "PERMISSION_SYSTEM_ADMIN")

                        // =====================================================
                        // MÓDULO ORDERS - STATUS
                        // =====================================================
                        .requestMatchers( "POST", "/v1/order/status").hasRole("ADMIN")
                        .requestMatchers( "PUT", "/v1/order/status/**").hasRole("ADMIN")
                        .requestMatchers( "DELETE", "/v1/order/status/**").hasRole("ADMIN")
                        .requestMatchers( "POST", "/v1/order/status/initialize-defaults").hasRole("ADMIN")
                        .requestMatchers( "GET", "/v1/order/status/**").hasAnyRole("ADMIN", "HOST", "COOK")
                        .requestMatchers( "GET", "/v1/order/status/check-name/**").hasRole("ADMIN")
                        .requestMatchers( "GET", "/v1/order/status/stats").hasRole("ADMIN")

                        // =====================================================
                        // MÓDULO PRODUCT - PRODUCTOS
                        // =====================================================
                        .requestMatchers( "POST", "/v1/product/product").hasRole("ADMIN")
                        .requestMatchers( "PUT", "/v1/product/product/**").hasRole("ADMIN")
                        .requestMatchers( "DELETE", "/v1/product/product/**").hasRole("ADMIN")
                        .requestMatchers( "PATCH", "/v1/product/product/*/toggle-status").hasRole("ADMIN")
                        .requestMatchers( "GET", "/v1/product/product/**").hasAnyRole("ADMIN", "HOST", "COOK")
                        .requestMatchers( "GET", "/v1/product/product/inactive").hasRole("ADMIN")
                        .requestMatchers( "GET", "/v1/product/product/created-between").hasRole("ADMIN")
                        .requestMatchers( "GET", "/v1/product/product/updated-between").hasRole("ADMIN")
                        .requestMatchers( "GET", "/v1/product/product/stats").hasRole("ADMIN")

                        // =====================================================
                        // MÓDULO ORDERS - ÓRDENES
                        // =====================================================
                        .requestMatchers( "POST", "/v1/orders").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "PUT", "/v1/orders/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "PATCH", "/v1/orders/*/notes").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "PATCH", "/v1/orders/*/total").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "POST", "/v1/orders/*/recalculate-total").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "DELETE", "/v1/orders/**").hasRole("ADMIN")
                        .requestMatchers( "PATCH", "/v1/orders/*/status").hasAnyRole("ADMIN", "HOST", "COOK")
                        .requestMatchers( "PATCH", "/v1/orders/*/mark-in-progress").hasAnyRole("ADMIN", "COOK")
                        .requestMatchers( "PATCH", "/v1/orders/*/mark-completed").hasAnyRole("ADMIN", "COOK")
                        .requestMatchers( "PATCH", "/v1/orders/*/mark-paid").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "PATCH", "/v1/orders/*/mark-canceled").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "GET", "/v1/orders/**").hasAnyRole("ADMIN", "HOST", "COOK")
                        .requestMatchers( "GET", "/v1/orders/user/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "GET", "/v1/orders/employee/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "GET", "/v1/orders/search/user").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "GET", "/v1/orders/search/notes").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "GET", "/v1/orders/created-between").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "GET", "/v1/orders/total-range").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "GET", "/v1/orders/stats").hasRole("ADMIN")
                        .requestMatchers( "GET", "/v1/orders/average-preparation-time").hasRole("ADMIN")
                        .requestMatchers( "GET", "/v1/orders/inconsistent-totals").hasRole("ADMIN")
                        .requestMatchers( "POST", "/v1/orders/recalculate-all-totals").hasRole("ADMIN")

                        // =====================================================
                        // MÓDULO ORDER ITEMS - ITEMS DE ÓRDENES
                        // =====================================================
                        .requestMatchers( "POST", "/v1/order-items").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "PUT", "/v1/order-items/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "PATCH", "/v1/order-items/*/quantity").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "PATCH", "/v1/order-items/*/unit-price").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "PATCH", "/v1/order-items/*/instructions").hasAnyRole("ADMIN", "HOST", "COOK")
                        .requestMatchers( "DELETE", "/v1/order-items/**").hasRole("ADMIN")
                        .requestMatchers( "GET", "/v1/order-items/**").hasAnyRole("ADMIN", "HOST", "COOK")
                        .requestMatchers( "GET", "/v1/order-items/product/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "GET", "/v1/order-items/count/product/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "GET", "/v1/order-items/total-quantity/product/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers( "GET", "/v1/order-items/stats").hasRole("ADMIN")
                        .requestMatchers( "GET", "/v1/order-items/most-ordered-products").hasRole("ADMIN")
                        .requestMatchers( "GET", "/v1/order-items/todays-most-ordered-products").hasAnyRole("ADMIN", "HOST")

                        // =====================================================
                        // MÓDULO TABLES - MESAS (FUTURO)
                        // =====================================================
                        .requestMatchers("/v1/tables/**").hasAnyRole("ADMIN", "HOST", "COOK")

                        // =====================================================
                        // MÓDULO CATEGORIES - CATEGORÍAS (FUTURO)
                        // =====================================================
                        .requestMatchers("/v1/categories/**").hasAnyRole("ADMIN", "HOST", "COOK")

                        // =====================================================
                        // MÓDULO COOKING - COCINA (FUTURO)
                        // =====================================================
                        .requestMatchers("/v1/cooking/**").hasAnyRole("ADMIN", "COOK")

                        // =====================================================
                        // MÓDULO BILLING - FACTURACIÓN (FUTURO)
                        // =====================================================
                        .requestMatchers("/v1/billing/**").hasAnyRole("ADMIN", "HOST")

                        // =====================================================
                        // MÓDULO REPORTS - REPORTES (FUTURO)
                        // =====================================================
                        .requestMatchers("/v1/reports/**").hasRole("ADMIN")

                        // =====================================================
                        // FALLBACK - CUALQUIER OTRA PETICIÓN
                        // =====================================================
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