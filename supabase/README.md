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
| `cancelar_pedido` | Cancela un pedido reservado, devuelve el stock y avisa al cliente y al negocio |
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
| `sql/001_estructura_y_permisos.sql` | Tablas, índices, triggers y políticas RLS |
| `sql/002_operaciones_seguras.sql` | Funciones RPC de ofertas y pedidos |
| `sql/003_imagenes_y_realtime.sql` | Bucket de fotografías y sus políticas |
| `sql/005_verificar_configuracion.sql` | Consultas para revisar la configuración |
| `sql/006_pruebas_transaccionales.sql` | Pruebas de las reglas del servidor; revierte los datos creados |

Las fotografías aceptan JPG, PNG o WebP de hasta 5 MB y se guardan como
`<uuid-propietario>/<uuid-archivo>.<extension>`.
