-- =====================================================
-- SCRIPT 2: INSERCIÓN DE DATOS INICIALES (SEEDING)
-- Se ejecuta después de crear las tablas.
-- =====================================================

-- Insertar Roles por defecto
INSERT INTO auth.roles (name, description) VALUES
('ADMIN', 'Administrador del sistema con todos los permisos'),
('HOST', 'Mesero/Anfitrión con permisos para tomar órdenes y gestionar mesas'),
('COOK', 'Cocinero con permisos para ver y actualizar estado de órdenes')
ON CONFLICT (name) DO NOTHING;

-- Insertar Permisos básicos
INSERT INTO auth.permission (name, description) VALUES
('PERMISSION_ORDER_CREATE', 'Permite crear nuevas órdenes'),
('PERMISSION_ORDER_READ', 'Permite ver órdenes'),
('PERMISSION_ORDER_UPDATE', 'Permite modificar órdenes'),
('PERMISSION_ORDER_DELETE', 'Permite eliminar órdenes'),
('PERMISSION_ORDER_STATS', 'Permite ver estadísticas de órdenes'),
('PERMISSION_USER_READ', 'Permite ver usuarios'),
('PERMISSION_ROLE_READ', 'Permite ver roles'),
('PERMISSION_PERMISSION_READ', 'Permite ver permisos'),
('PERMISSION_SYSTEM_ADMIN', 'Permisos de super administrador del sistema')
ON CONFLICT (name) DO NOTHING;

-- Asignar permisos a los roles
-- ADMIN tiene todos los permisos
INSERT INTO auth.roles_permission (role_id, permission_id)
SELECT r.id, p.id FROM auth.roles r, auth.permission p WHERE r.name = 'ADMIN'
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- HOST (Mesero) tiene permisos de órdenes
INSERT INTO auth.roles_permission (role_id, permission_id)
SELECT r.id, p.id FROM auth.roles r, auth.permission p
WHERE r.name = 'HOST' AND p.name IN ('PERMISSION_ORDER_CREATE', 'PERMISSION_ORDER_READ', 'PERMISSION_ORDER_UPDATE', 'PERMISSION_ORDER_STATS')
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- COOK (Cocinero) tiene permisos de lectura y actualización de estado
INSERT INTO auth.roles_permission (role_id, permission_id)
SELECT r.id, p.id FROM auth.roles r, auth.permission p
WHERE r.name = 'COOK' AND p.name IN ('PERMISSION_ORDER_READ', 'PERMISSION_ORDER_UPDATE')
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Insertar Estados de Orden por defecto
INSERT INTO orders.status (name, description) VALUES
('Confirmado', 'Pedido confirmado y enviado a cocina'),
('En Curso', 'Pedido en preparación en cocina'),
('Terminado', 'Pedido terminado y listo para servir'),
('Pagado', 'Pedido pagado por el cliente'),
('Cancelado', 'Pedido cancelado')
ON CONFLICT (name) DO NOTHING;

-- Insertar Mesas de ejemplo (con is_active y status)
INSERT INTO tables.tables (table_number, capacity, is_active, status) VALUES
('T1', 4, true, 'LIBRE'),
('T2', 4, true, 'LIBRE'),
('T3', 2, true, 'LIBRE'),
('T4', 6, true, 'LIBRE'),
('Barra-1', 1, true, 'LIBRE'),
('Barra-2', 1, true, 'LIBRE')
ON CONFLICT (table_number) DO NOTHING;

-- Insertar Categorías de ejemplo (con is_active)
INSERT INTO product.category (name, description, is_active) VALUES
('Bebidas', 'Bebidas frías y calientes', true),
('Entradas', 'Aperitivos y entradas', true),
('Platos Fuertes', 'Platos principales de la casa', true),
('Postres', 'Postres y dulces', true)
ON CONFLICT (name) DO NOTHING;

-- Insertar Productos de ejemplo
INSERT INTO product.product (category_id, name, description, price, is_active) VALUES
((SELECT id FROM product.category WHERE name = 'Bebidas'), 'Refresco de Cola', '355ml', 2.50, true),
((SELECT id FROM product.category WHERE name = 'Bebidas'), 'Agua Embotellada', '500ml', 1.50, true),
((SELECT id FROM product.category WHERE name = 'Entradas'), 'Papas Fritas', 'Porción de papas fritas con salsa de la casa', 4.50, true),
((SELECT id FROM product.category WHERE name = 'Platos Fuertes'), 'Hamburguesa Clásica', 'Carne de res, lechuga, tomate, queso', 12.00, true),
((SELECT id FROM product.category WHERE name = 'Postres'), 'Pastel de Chocolate', 'Rebanada de pastel de chocolate', 5.00, true)
ON CONFLICT (name) DO NOTHING;

-- Insertar un usuario ADMIN de prueba (contraseña: admin123)
INSERT INTO auth.users (employee_id, first_name, last_name, email, password, phone_number, is_active) VALUES
('ADMIN001', 'Admin', 'User', 'admin@isaiiapp.com', '$2a$10$G.w2a51g3/E04s.gqLxM0.L23h3nS4Lz2aa.r.iKPEXs7i5fP3pS.', '0000000000', true)
ON CONFLICT (employee_id) DO NOTHING;

-- Asignar rol de ADMIN al usuario admin
INSERT INTO auth.users_roles (users_id, roles_id)
SELECT u.id, r.id
FROM auth.users u, auth.roles r
WHERE u.employee_id = 'ADMIN001' AND r.name = 'ADMIN'
ON CONFLICT (users_id, roles_id) DO NOTHING;

-- Insertar la entrada correspondiente en la tabla auth para el usuario admin
INSERT INTO auth.auth (user_id, username, password_hash, enabled)
SELECT id, employee_id, password, is_active
FROM auth.users
WHERE employee_id = 'ADMIN001'
ON CONFLICT (user_id) DO NOTHING;


