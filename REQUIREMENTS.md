
# Prueba Técnica – Desarrollador Senior

## Objetivo
Construir un servicio API que permita gestionar cobros automáticos simulados, incluyendo procesamiento individual y por lotes, con trazabilidad de eventos.

## Tiempo estimado
2 dias

## Entrega
Repositorio (Git) con instrucciones claras para ejecutar la solución y un breve documento explicando las decisiones técnicas tomadas.

---

## Entidades mínimas
```
Cliente
 * id
 * nombre
 * dpi
 * email (opcional)
 * teléfono (opcional)
Cobro
 * id
 * cliente_id
 * monto
 * moneda
 * estado (PENDIENTE / PROCESADO / FALLIDO)
 * fecha_creación
 * fecha_proceso
 * referencia_externa
Auditoría
 * id
 * evento
 * resumen_payload
 * fecha
 * usuario_sistema
```
## Requerimientos funcionales
    1. Crear cliente
        * POST /clientes
        * Validar que el DPI sea único
    2. Registrar cobro
        * POST /cobros
        * Validar monto mayor a 0 y existencia del cliente
        * El cobro se registra inicialmente en estado PENDIENTE
    3. Procesar cobro
        * POST /cobros/{id}/procesar
        * Simulación de reglas:
            * Monto ≤ 1000 → PROCESADO
            * Monto > 1000 → FALLIDO
        * Registrar el evento en auditoría
    4. Procesar lote de cobros
        * POST /cobros/lotes/procesar
        * Recibe una lista de IDs de cobro
        * Procesa todos los registros y devuelve un resumen con totales, procesados y fallidos
        * El proceso debe ser idempotente
    5. Consulta de cobros por cliente
        * GET /clientes/{id}/cobros?estado=&desde=&hasta=
        * Permite filtrar por estado y rango de fechas

---

## Requerimientos no funcionales 
* Manejo consistente de errores y códigos HTTP.
* Persistencia de datos (base de datos relacional preferible; alternativa permitida si se documenta).
* Registro básico de logs por request.
* Implementar pruebas unitarias mínimas.
* Incluir validación básica de seguridad mediante token en header (o documentar alternativa).

---

## Entregables
1. Repositorio con el código fuente.
2. Archivo README que incluya:
    * Pasos para ejecutar la solución.
    * Descripción de endpoints (ejemplos de uso).
    * Decisiones técnicas y trade-offs.
3. Pruebas unitarias y cómo ejecutarlas.

