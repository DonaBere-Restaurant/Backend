-- Script para agregar autenticación biométrica a la tabla users
-- Ejecutar en la base de datos PostgreSQL de restaurantbere

ALTER TABLE users ADD COLUMN IF NOT EXISTS fingerprint_public_key TEXT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS fingerprint_enabled BOOLEAN DEFAULT FALSE;

-- Crear índice para búsquedas rápidas
CREATE INDEX IF NOT EXISTS idx_users_fingerprint_enabled ON users(fingerprint_enabled);

COMMIT;
