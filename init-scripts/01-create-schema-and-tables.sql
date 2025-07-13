-- =====================================================
-- SCRIPT 1: CREACIÓN DE ESQUEMAS Y TABLAS (VERSIÓN IDEMPOTENTE)
-- =====================================================

-- 1. Creación de Esquemas
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS tables;
CREATE SCHEMA IF NOT EXISTS product;
CREATE SCHEMA IF NOT EXISTS orders;

-- =====================================================
-- MÓDULO: AUTH
-- =====================================================

CREATE TABLE IF NOT EXISTS auth.users (
   id BIGSERIAL PRIMARY KEY,
   employee_id VARCHAR(50) UNIQUE NOT NULL,
   first_name VARCHAR(100) NOT NULL,
   last_name VARCHAR(100) NOT NULL,
   email VARCHAR(100) UNIQUE NOT NULL,
   password VARCHAR(255) NOT NULL,
   phone_number VARCHAR(20),
   is_active BOOLEAN NOT NULL DEFAULT true,
   created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auth.roles (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(50) UNIQUE NOT NULL,
   description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS auth.permission (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(100) UNIQUE NOT NULL,
   description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS auth.users_roles (
   users_id BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
   roles_id BIGINT NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
   PRIMARY KEY (users_id, roles_id)
);

CREATE TABLE IF NOT EXISTS auth.roles_permission (
   role_id BIGINT NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
   permission_id BIGINT NOT NULL REFERENCES auth.permission(id) ON DELETE CASCADE,
   PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS auth.auth (
   id BIGSERIAL PRIMARY KEY,
   user_id BIGINT UNIQUE NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
   username VARCHAR(100) UNIQUE NOT NULL,
   password_hash VARCHAR(150) NOT NULL,
   enabled BOOLEAN NOT NULL,
   recuperation_tkn VARCHAR(10),
   recuperation_tkn_exp TIMESTAMP WITHOUT TIME ZONE,
   login_attempts INT,
   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auth.session (
   id BIGSERIAL PRIMARY KEY,
   user_id BIGINT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
   access_token VARCHAR(600) UNIQUE NOT NULL,
   refresh_token VARCHAR(600) UNIQUE NOT NULL,
   access_token_expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   refresh_token_expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   last_activity_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   is_active BOOLEAN
);

-- =====================================================
-- MÓDULO: PRODUCT
-- =====================================================
CREATE TABLE IF NOT EXISTS product.category (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(100) UNIQUE NOT NULL,
   description VARCHAR(255),
   is_active BOOLEAN NOT NULL DEFAULT true,
   created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product.product (
   id BIGSERIAL PRIMARY KEY,
   category_id BIGINT REFERENCES product.category(id),
   name VARCHAR(100) UNIQUE NOT NULL,
   description TEXT,
   price DECIMAL(10, 2) NOT NULL,
   is_active BOOLEAN NOT NULL DEFAULT true,
   created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- MÓDULO: TABLES
-- =====================================================
CREATE TABLE IF NOT EXISTS tables.tables (
   id BIGSERIAL PRIMARY KEY,
   table_number VARCHAR(10) UNIQUE NOT NULL,
   capacity INT NOT NULL,
   is_active BOOLEAN NOT NULL DEFAULT true, -- cambiado de `is_available`
   status VARCHAR(10) NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- MÓDULO: ORDERS
-- =====================================================
CREATE TABLE IF NOT EXISTS orders.status (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(50) UNIQUE NOT NULL,
   description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS orders.orders (
   id BIGSERIAL PRIMARY KEY,
   user_id BIGINT NOT NULL REFERENCES auth.users(id),
   table_id BIGINT NOT NULL REFERENCES tables.tables(id),
   status_id BIGINT NOT NULL REFERENCES orders.status(id),
   is_takeaway BOOLEAN NOT NULL DEFAULT false,
   total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
   notes VARCHAR(250),
   created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP WITHOUT TIME ZONE,
   confirmed_at TIMESTAMP WITHOUT TIME ZONE,
   in_progress_at TIMESTAMP WITHOUT TIME ZONE,
   completed_at TIMESTAMP WITHOUT TIME ZONE,
   paid_at TIMESTAMP WITHOUT TIME ZONE,
   canceled_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS orders.order_item (
   id BIGSERIAL PRIMARY KEY,
   order_id BIGINT NOT NULL REFERENCES orders.orders(id) ON DELETE CASCADE,
   product_id BIGINT NOT NULL REFERENCES product.product(id),
   quantity INT NOT NULL,
   unit_price DECIMAL(10, 2) NOT NULL,
   subtotal DECIMAL(10, 2) NOT NULL,
   special_instructions VARCHAR(250)
);

