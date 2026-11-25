# Servidor Supabase

Supabase guarda los datos compartidos entre todos los teléfonos. Cuando un negocio
publica una oferta, se guarda en Supabase y cualquier cliente la ve y puede reservarla
desde otro dispositivo.

## Servicios usados

| Servicio | Uso en la app |
| --- | --- |
| Auth | Registro, inicio de sesión y renovación del token |
| PostgreSQL (REST) | Perfiles, negocios, ofertas, pedidos y notificaciones |
| RPC | Operaciones que deben ser atómicas: reservar, cancelar, avanzar pedidos |
| Storage | Fotografías de las ofertas en el bucket `ofertas` |

La app llama al servidor con Retrofit (`SupabaseApi`). Cada petición envía la clave
pública en `apikey` y el token del usuario en `Authorization: Bearer`. Las políticas
RLS de PostgreSQL deciden qué puede leer o modificar cada usuario.

## Endpoints

| Función | Método | Ruta |
| --- | --- | --- |
| Registro | POST | `/auth/v1/signup` |
| Inicio y renovación de sesión | POST | `/auth/v1/token` |
| Perfil y negocio del usuario | GET | `/rest/v1/perfiles`, `/rest/v1/negocios` |
| Catálogo de ofertas | GET | `/rest/v1/ofertas` |
| Crear y editar oferta | POST, PATCH | `/rest/v1/ofertas` |
| Pedidos | GET | `/rest/v1/pedidos` |
| Notificaciones | GET, PATCH, DELETE | `/rest/v1/notificaciones` |
| Operaciones del servidor | POST | `/rest/v1/rpc/...` |
| Subir fotografía | POST | `/storage/v1/object/ofertas/...` |

## Operaciones del servidor (RPC)

| RPC | Qué hace |
| --- | --- |
| `reservar_oferta` | Revisa el stock, lo descuenta, crea el pedido y avisa al cliente y al negocio |
| `cancelar_pedido` | Cancela un pedido reservado, anula el pago simulado, devuelve el stock una sola vez y avisa a ambas partes |
| `avanzar_pedido` | Pasa el pedido a preparando, listo o recogido y avisa al cliente; para entregar exige el código de rescate |
| `cambiar_estado_oferta` | Pausa o reactiva una oferta del propietario |
| `eliminar_oferta` | Oculta la oferta y conserva los pedidos anteriores |

El servidor calcula los precios del pedido; la app no modifica el stock directamente.

## Sincronización

1. Cada repositorio pide los datos con Retrofit.
2. Los guarda en Room (`rescatapp.db`) y borra los que ya no existen en el servidor.
3. Las pantallas observan Room con `Flow` y se actualizan solas.

Las listas se actualizan al iniciar sesión, después de cada cambio y cada 10 segundos
con la app visible (`SincronizarDatosUseCase`). Las notificaciones nuevas sin leer se
muestran también como notificación del sistema.

Sin conexión la app muestra lo último guardado en Room y un aviso. Crear, editar o
reservar necesita conexión.

## Sesión

El token de acceso, el token de renovación y su vencimiento se guardan en la tabla
`sesion_activa` de Room. `RepositorioAutenticacion` renueva el token cuando le falta
menos de un minuto para vencer. Cerrar sesión borra esa tabla.

## Scripts SQL

Se ejecutan una sola vez, en orden, en el SQL Editor de un proyecto nuevo.

| Archivo | Contenido |
| --- | --- |
| `sql/001_estructura.sql` | Tablas, índices y triggers de fecha y catálogo |
| `sql/002_operaciones_pedidos.sql` | RPC de pedidos: reservar, cancelar y avanzar |
| `sql/003_permisos_imagenes_y_realtime.sql` | Perfil al registrarse, políticas RLS, permisos, bucket de fotografías y Realtime |
| `sql/004_operaciones_ofertas.sql` | RPC de ofertas: pausar, reactivar y eliminar |
| `sql/005_verificar_configuracion.sql` | Consultas para revisar la configuración |
| `sql/006_pruebas_transaccionales.sql` | Pruebas de las reglas del servidor; revierte los datos creados |
| `sql/007_contacto_cliente_pedidos.sql` | Incorpora el teléfono del comprador en los pedidos existentes y nuevos |
| `sql/008_pruebas_contacto_cliente.sql` | Verifica contacto y permisos después de aplicar 007; revierte los datos de prueba |
| `sql/009_cancelacion_pago_simulado.sql` | Registra la anulación del pago simulado en la misma transacción que la cancelación y el stock |
| `sql/010_pruebas_cancelacion_pago.sql` | Verifica cancelación, pago, reintentos, estados y permisos; revierte los datos de prueba |

Una instalación existente se actualiza ejecutando solo `007` y `009`, sin repetir
`002`; después se pueden correr sus pruebas, `008` y `010`.

- `007` guarda el nombre y el teléfono del comprador en el pedido al reservar. Solo
  los ve quien ya tiene acceso al pedido por RLS. Sin este script la app muestra
  "Teléfono no disponible".
- `009` agrega `estado_pago_simulado` (`APROBADO` o `ANULADO`). `cancelar_pedido`
  bloquea el pedido como `avanzar_pedido`, así que si la tienda ya lo aceptó la
  cancelación falla sin tocar pago ni stock. Repetir una cancelación ya hecha no
  devuelve stock dos veces. No hay cobro ni reembolso bancario real.

En el teléfono, Room pasa de la versión 5 a la 7 con migraciones que conservan
pedidos y sesión.

Las fotografías aceptan JPG, PNG o WebP de hasta 5 MB y se guardan como
`<uuid-propietario>/<uuid-archivo>.<extension>`.
