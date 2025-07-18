-- =====================================================
-- SCRIPT 1: CREACIÓN DE ESQUEMAS Y TABLAS (VERSIÓN IDEMPOTENTE)
-- =====================================================

-- 1. Creación de Esquemas
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS tables;
CREATE SCHEMA IF NOT EXISTS product;
CREATE SCHEMA IF NOT EXISTS orders;