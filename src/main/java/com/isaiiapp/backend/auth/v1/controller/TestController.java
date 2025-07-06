package com.isaiiapp.backend.auth.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/test")
@Slf4j
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "🍽️ Restaurant System - Public endpoint");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "🔐 Restaurant System - Protected endpoint");
        response.put("timestamp", LocalDateTime.now());
        response.put("employee", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('PERMISSION_USER_MANAGEMENT')")
    public ResponseEntity<Map<String, Object>> adminEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "👑 Restaurant Admin - Management dashboard access");
        response.put("timestamp", LocalDateTime.now());
        response.put("employee", auth.getName());
        response.put("role", "admin");
        response.put("authorities", auth.getAuthorities());
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/host")
    @PreAuthorize("hasAuthority('PERMISSION_ORDER_CREATE')")
    public ResponseEntity<Map<String, Object>> hostEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "🍽️ Host/Waiter - Order management access");
        response.put("timestamp", LocalDateTime.now());
        response.put("employee", auth.getName());
        response.put("role", "host");
        response.put("capabilities", new String[]{"Create orders", "Manage tables", "Process payments"});
        response.put("authorities", auth.getAuthorities());
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/kitchen")
    @PreAuthorize("hasAuthority('PERMISSION_KITCHEN_QUEUE_VIEW')")
    public ResponseEntity<Map<String, Object>> kitchenEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "👨‍🍳 Kitchen Staff - Order preparation access");
        response.put("timestamp", LocalDateTime.now());
        response.put("employee", auth.getName());
        response.put("role", "cook");
        response.put("capabilities", new String[]{"View order queue", "Update order status", "Track preparation times"});
        response.put("authorities", auth.getAuthorities());
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports")
    @PreAuthorize("hasAuthority('PERMISSION_REPORTS_SALES')")
    public ResponseEntity<Map<String, Object>> reportsEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "📊 Restaurant Reports - Analytics access");
        response.put("timestamp", LocalDateTime.now());
        response.put("employee", auth.getName());
        response.put("availableReports", new String[]{"Sales by date", "Product performance", "Waiter performance", "Kitchen timing"});
        response.put("authorities", auth.getAuthorities());
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }
}
