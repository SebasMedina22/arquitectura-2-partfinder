# PartFinder — Product context (for impeccable skill)

## Register
product

## Users
Mecanicos y dueños de talleres mecanicos pequeños y medianos en LATAM. Acceden a la herramienta desde una PC en el taller (a veces tablet) con un auto a medio desarmar al lado. Tienen prisa: cada minuto que un cliente espera es un minuto sin facturar.

## Product Purpose
Encontrar repuestos compatibles con el modelo del cliente, ver quien los tiene en stock REAL, y emitir un pedido sin friccion. Si el sistema del proveedor es lento, no quedarse esperando un "spinner" — preferimos un "puede que lo tengan, decide tu" antes que un error.

## Brand / tone
Tecnico, directo, sin floritura. Profesional pero no corporativo. Cero stock photos sonrientes. Sin emojis. Densidad alta en pantallas operacionales — los talleres prefieren ver mas datos que mas blanco.

## Anti-references
- Apps de e-commerce consumer (Amazon, Mercado Libre) — demasiada conversion-bait
- Dashboards SaaS con curvas pastel — no es el lugar
- Cualquier UI que abuse de iconitos lindos

## Surfaces in scope
SPA web servida desde Nginx en `:8080`. Cuatro pestañas: Buscar parte, Crear pedido, Pedidos del taller, Tendencias, Admin de simulacion.

## Strategic principles
- Mostrar el `availability` (AVAILABLE / UNCERTAIN / NOT_FOUND) con badges inequivocos: el mecanico debe captar en menos de un segundo si la pieza esta confirmada o no.
- "Disponibilidad Incierta" (R1) debe parecer una respuesta valida del sistema, NO un error. Color ambar, no rojo.
- Codigos de partes (`PRT-FO-001`) y proveedores (`SUP-LIMA`) en monospace — son IDs, no copy.
- Cero modales bloqueantes. Si una accion necesita confirmacion, inline.

## Visual register
Dark theme (taller con luz alta, una sola pantalla, queremos reducir reflejos sobre vidrio). Neutros tintados hacia un azul-grafito sutil. Un acento (azul electrico) para acciones primarias.
