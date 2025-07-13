#!/bin/bash

# Salir inmediatamente si un comando falla
set -e

# Colores para el output
GREEN='\033[0;32m'
NC='\033[0m' # No Color

log() {
    echo -e "${GREEN}[INFO] $1${NC}"
}

log "🧹 Limpiando entorno Docker anterior (volúmenes incluidos)..."
docker-compose down -v

log "🏗️ Construyendo y levantando los servicios en segundo plano..."
docker-compose up --build -d

log "👀 Mostrando logs en tiempo real. Presiona Ctrl+C para salir."
# El -f sigue los logs, --tail=0 muestra solo los nuevos logs
docker-compose logs -f --tail=0
