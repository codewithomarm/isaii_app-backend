package com.isaiiapp.backend.security.v1.config;

import com.isaiiapp.backend.auth.v1.exception.AccountLockedException;
import com.isaiiapp.backend.security.v1.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
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
import org.springframework.security.web.AuthenticationEntryPoint;
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
                        .requestMatchers(
                                "/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()
                        // Modulo Security v1 - Logout
                        .requestMatchers("/api/v1/auth/logout").authenticated()
                        // Modulo AUTH V1
                        .requestMatchers("/api/v1/auth-accounts").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("/api/v1/auth-accounts/").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("/api/v1/auth-accounts/{id}").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("/api/v1/auth-accounts/username/**").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("/api/v1/auth-accounts/stats").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("/api/v1/auth-accounts/locked").hasAuthority("PERMISSION_USER_ACCOUNT_CONTROL")
                        .requestMatchers("/api/v1/auth-accounts/*/unlock").hasAuthority("PERMISSION_USER_ACCOUNT_CONTROL")
                        .requestMatchers("/api/v1/auth-accounts/*/toggle-status").hasAuthority("PERMISSION_USER_ACCOUNT_CONTROL")
                        .requestMatchers("/api/v1/auth-accounts/*/reset-password").hasAuthority("PERMISSION_USER_PASSWORD_RESET")
                        .requestMatchers("/api/v1/auth-accounts/generate-recovery-token").hasAuthority("PERMISSION_USER_PASSWORD_RESET")
                        .requestMatchers("/api/v1/auth-accounts/validate-recovery-token").permitAll()
                        .requestMatchers("/api/v1/auth-accounts/change-password-with-token").permitAll()
                        // Modulo Permission V1
                        .requestMatchers( "POST", "/api/v1/permissions").hasAnyAuthority("PERMISSION_PERMISSION_CREATE", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers( "PUT", "/api/v1/permissions/**").hasAnyAuthority("PERMISSION_PERMISSION_UPDATE", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers( "DELETE", "/api/v1/permissions/**").hasAnyAuthority("PERMISSION_PERMISSION_DELETE", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers( "GET", "/api/v1/permissions/{id}").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers( "GET", "/api/v1/permissions/name/**").hasAnyAuthority("PERMISSION_PERMISSION_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers( "GET", "/api/v1/permissions").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers( "GET", "/api/v1/permissions/search").hasAnyAuthority("PERMISSION_PERMISSION_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers( "GET", "/api/v1/permissions/role/**").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers( "GET", "/api/v1/permissions/role/*/available").hasAnyAuthority("PERMISSION_PERMISSION_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers( "GET", "/api/v1/permissions/employee/**").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers( "GET", "/api/v1/permissions/user/**").hasAnyAuthority("PERMISSION_PERMISSION_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers( "GET", "/api/v1/permissions/check-name/**").hasAnyAuthority("PERMISSION_PERMISSION_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers( "GET", "/api/v1/permissions/employee/*/check/*").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers( "GET", "/api/v1/permissions/user/*/check/*").hasAnyAuthority("PERMISSION_PERMISSION_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers( "POST", "/api/v1/permissions/initialize-defaults").hasAuthority("PERMISSION_SYSTEM_ADMIN")
                        // Modulo Roles V1
                        .requestMatchers("POST", "/api/v1/roles").hasAnyAuthority("PERMISSION_ROLE_CREATE", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers("GET", "/api/v1/roles/{id}").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("GET", "/api/v1/roles/name/{name}").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("GET", "/api/v1/roles/{id}/with-permissions").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("PUT", "/api/v1/roles/{id}").hasAnyAuthority("PERMISSION_ROLE_UPDATE", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers("DELETE", "/api/v1/roles/{id}").hasAnyAuthority("PERMISSION_ROLE_DELETE", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers("GET", "/api/v1/roles").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("GET", "/api/v1/roles/search").hasAnyAuthority("PERMISSION_ROLE_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers("GET", "/api/v1/roles/user/{userId}").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("GET", "/api/v1/roles/user/{userId}/available").hasAnyAuthority("PERMISSION_ROLE_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers("GET", "/api/v1/roles/permission/{permissionId}").hasAnyAuthority("PERMISSION_ROLE_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers("GET", "/api/v1/roles/check-name/{name}").hasAnyAuthority("PERMISSION_ROLE_READ", "PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers("POST", "/api/v1/roles/initialize-defaults").hasAuthority("PERMISSION_SYSTEM_ADMIN")
                        .requestMatchers("GET", "/api/v1/roles/employee/{userId}").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        // Modulo Users V1
                        .requestMatchers("POST", "/api/v1/employees").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("GET", "/api/v1/employees/{id}").hasAnyAuthority("PERMISSION_USER_MANAGEMENT", "PERMISSION_PROFILE_VIEW")
                        .requestMatchers("GET", "/api/v1/employees/employee/{employeeId}").hasAnyAuthority("PERMISSION_USER_MANAGEMENT", "PERMISSION_PROFILE_VIEW")
                        .requestMatchers("GET", "/api/v1/employees/{id}/with-roles").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("PUT", "/api/v1/employees/{id}").hasAnyAuthority("PERMISSION_USER_MANAGEMENT", "PERMISSION_PROFILE_UPDATE")
                        .requestMatchers("DELETE", "/api/v1/employees/{id}").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("GET", "/api/v1/employees").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("GET", "/api/v1/employees/active").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("GET", "/api/v1/employees/by-role/{roleName}").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("GET", "/api/v1/employees/search").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("PATCH", "/api/v1/employees/{id}/toggle-status").hasAuthority("PERMISSION_USER_ACCOUNT_CONTROL")
                        .requestMatchers("GET", "/api/v1/employees/check-employee-id/{employeeId}").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("GET", "/api/v1/employees/stats").hasAuthority("PERMISSION_USER_MANAGEMENT")
                        .requestMatchers("GET", "/api/v1/employees/profile").hasAuthority("PERMISSION_PROFILE_VIEW")
                        // Modulo OrderItems V1
                        .requestMatchers(HttpMethod.POST, "/api/v1/order-items").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/order-items/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/order-items/{id}/quantity").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/order-items/{id}/unit-price").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/order-items/{id}/instructions").hasAnyRole("ADMIN", "HOST", "COOK")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/order-items/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/order-items/**").hasAnyRole("ADMIN", "HOST", "COOK")
                        .requestMatchers(HttpMethod.GET, "/api/v1/order-items/product/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers(HttpMethod.GET, "/api/v1/order-items/count/product/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers(HttpMethod.GET, "/api/v1/order-items/total-quantity/product/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers(HttpMethod.GET, "/api/v1/order-items/most-ordered-products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/order-items/todays-most-ordered-products").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers(HttpMethod.GET, "/api/v1/order-items/stats").hasRole("ADMIN")
                        // Modulo Orders V1
                        .requestMatchers("POST", "/api/v1/orders").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("PUT", "/api/v1/orders/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("PATCH", "/api/v1/orders/*/notes").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("PATCH", "/api/v1/orders/*/total").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("POST", "/api/v1/orders/*/recalculate-total").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("DELETE", "/api/v1/orders/**").hasRole("ADMIN")
                        .requestMatchers("PATCH", "/api/v1/orders/*/status").hasAnyRole("ADMIN", "HOST", "COOK")
                        .requestMatchers("PATCH", "/api/v1/orders/*/mark-in-progress").hasAnyRole("ADMIN", "COOK")
                        .requestMatchers("PATCH", "/api/v1/orders/*/mark-completed").hasAnyRole("ADMIN", "COOK")
                        .requestMatchers("PATCH", "/api/v1/orders/*/mark-paid").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("PATCH", "/api/v1/orders/*/mark-canceled").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("GET", "/api/v1/orders/**").hasAnyRole("ADMIN", "HOST", "COOK")
                        .requestMatchers("GET", "/api/v1/orders/user/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("GET", "/api/v1/orders/employee/**").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("GET", "/api/v1/orders/search/user").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("GET", "/api/v1/orders/search/notes").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("GET", "/api/v1/orders/created-between").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("GET", "/api/v1/orders/total-range").hasAnyRole("ADMIN", "HOST")
                        .requestMatchers("GET", "/api/v1/orders/stats").hasRole("ADMIN")
                        .requestMatchers("GET", "/api/v1/orders/average-preparation-time").hasRole("ADMIN")
                        .requestMatchers("GET", "/api/v1/orders/inconsistent-totals").hasRole("ADMIN")
                        .requestMatchers("POST", "/api/v1/orders/recalculate-all-totals").hasRole("ADMIN")
                        // Modulo Status V1
                        .requestMatchers("POST", "/api/v1/order/status").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/v1/order/status/**").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/v1/order/status/**").hasRole("ADMIN")
                        .requestMatchers("POST", "/api/v1/order/status/initialize-defaults").hasRole("ADMIN")
                        .requestMatchers("GET", "/api/v1/order/status/**").hasAnyRole("ADMIN", "HOST", "COOK")
                        .requestMatchers("GET", "/api/v1/order/status/check-name/**").hasRole("ADMIN")
                        .requestMatchers("GET", "/api/v1/order/status/stats").hasRole("ADMIN")
                        // Modulo Category V1
                        .requestMatchers(HttpMethod.POST,    "/api/v1/categories").hasAuthority("CREATE_CATEGORY")
                        .requestMatchers(HttpMethod.GET,     "/api/v1/categories/**").hasAuthority("READ_CATEGORY")
                        .requestMatchers(HttpMethod.PUT,     "/api/v1/categories/**").hasAuthority("UPDATE_CATEGORY")
                        .requestMatchers(HttpMethod.DELETE,  "/api/v1/categories/**").hasAuthority("DELETE_CATEGORY")
                        .requestMatchers(HttpMethod.PATCH,   "/api/v1/categories/**").hasAuthority("UPDATE_CATEGORY")
                        // Modulo Product V1
                        .requestMatchers(HttpMethod.POST,   "/api/v1/product/product").hasAuthority("CREATE_PRODUCT")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/product/product/stats").hasAuthority("STATS_PRODUCT")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/product/product/created-between").hasAuthority("REPORT_PRODUCT")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/product/product/updated-between").hasAuthority("REPORT_PRODUCT")
                        .requestMatchers(HttpMethod.PATCH,  "/api/v1/product/product/*/toggle-status").hasAuthority("TOGGLE_PRODUCT")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/product/product/*").hasAuthority("UPDATE_PRODUCT")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/product/product/*").hasAuthority("DELETE_PRODUCT")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/product/product/**").hasAuthority("READ_PRODUCT")
                        // Modulo Tables V1
                        .requestMatchers(HttpMethod.POST, "/api/v1/tables").hasAuthority("CREATE_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/{id}").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/number/**").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tables/{id}").hasAuthority("UPDATE_TABLE")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/tables/{id}/status").hasAuthority("UPDATE_TABLE")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/tables/{id}/toggle").hasAuthority("UPDATE_TABLE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tables/{id}").hasAuthority("DELETE_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/active").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/status/**").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/available").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/occupied").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/capacity/min/*").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/capacity/range").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/search").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/check-availability/*").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/exists/*").hasAuthority("READ_TABLE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tables/statistics").hasAuthority("READ_TABLE")
                        // Excepciones
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