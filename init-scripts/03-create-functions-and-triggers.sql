-- =====================================================
-- SCRIPT 3: CREACIÓN DE FUNCIONES Y TRIGGERS
-- Se ejecuta al final, después de que todo existe.
-- =====================================================

-- 1. Función para recalcular el total de una orden
CREATE OR REPLACE FUNCTION recalculate_order_total()
RETURNS TRIGGER AS $$
DECLARE
    order_id_to_update BIGINT;
    new_total DECIMAL(10,2);
BEGIN
    -- Determinar el order_id según el tipo de operación
    IF TG_OP = 'DELETE' THEN
        order_id_to_update := OLD.order_id;
    ELSE
        order_id_to_update := NEW.order_id;
    END IF;

    -- Calcular el nuevo total sumando todos los subtotales de los items de la orden
    SELECT COALESCE(SUM(subtotal), 0.00)
    INTO new_total
    FROM orders.order_item
    WHERE order_id = order_id_to_update;

    -- Actualizar el total en la tabla orders
    UPDATE orders.orders
    SET total_amount = new_total,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = order_id_to_update;

    RAISE NOTICE 'Order % total recalculated to %', order_id_to_update, new_total;

    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- 2. Creación de Triggers (se crean directamente porque las tablas ya existen)

-- Trigger para INSERT en order_item
DROP TRIGGER IF EXISTS trigger_order_item_insert ON orders.order_item;
CREATE TRIGGER trigger_order_item_insert
    AFTER INSERT ON orders.order_item
    FOR EACH ROW
    EXECUTE FUNCTION recalculate_order_total();

-- Trigger para UPDATE en order_item
DROP TRIGGER IF EXISTS trigger_order_item_update ON orders.order_item;
CREATE TRIGGER trigger_order_item_update
    AFTER UPDATE OF quantity, unit_price ON orders.order_item
    FOR EACH ROW
    WHEN (OLD.quantity IS DISTINCT FROM NEW.quantity OR OLD.unit_price IS DISTINCT FROM NEW.unit_price)
    EXECUTE FUNCTION recalculate_order_total();

-- Trigger para DELETE en order_item
DROP TRIGGER IF EXISTS trigger_order_item_delete ON orders.order_item;
CREATE TRIGGER trigger_order_item_delete
    AFTER DELETE ON orders.order_item
    FOR EACH ROW
    EXECUTE FUNCTION recalculate_order_total();

COMMENT ON FUNCTION recalculate_order_total() IS
'Función trigger que recalcula automáticamente el total de una orden cuando se modifican sus items.';
