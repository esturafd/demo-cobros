#!/bin/sh
#
# Entrypoint del contenedor de Cobros API.
#
# Responsabilidades:
#  1. Asegurar el directorio de datos (base SQLite + token persistido).
#  2. Generar un token de API aleatorio la primera vez que arranca el contenedor
#     (si no se ha proporcionado uno por entorno) y persistirlo para reutilizarlo
#     en reinicios posteriores.
#  3. Mostrar el token en los logs para que el usuario pueda autenticarse.
#
# Se puede fijar un token propio con: -e APP_SECURITY_API_TOKEN=<valor>
#
set -e

DATA_DIR="/deployments/data"
mkdir -p "$DATA_DIR"

if [ -z "${APP_SECURITY_API_TOKEN:-}" ]; then
  TOKEN_FILE="$DATA_DIR/api-token"
  if [ -f "$TOKEN_FILE" ]; then
    APP_SECURITY_API_TOKEN="$(cat "$TOKEN_FILE")"
  else
    APP_SECURITY_API_TOKEN="$(head -c 32 /dev/urandom | od -An -tx1 | tr -d ' \n')"
    printf '%s' "$APP_SECURITY_API_TOKEN" > "$TOKEN_FILE"
    chmod 600 "$TOKEN_FILE"
  fi
  export APP_SECURITY_API_TOKEN
fi

echo "======================================================================"
echo "  Cobros API - token de acceso (cabecera X-API-Token):"
echo "  ${APP_SECURITY_API_TOKEN}"
echo "======================================================================"

exec "$@"
