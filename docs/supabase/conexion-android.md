# Arquitectura de la conexión con Supabase

## Servicios y configuración

La aplicación utiliza Supabase Auth, PostgreSQL mediante REST y Supabase Storage.
Las rutas se declaran con Retrofit en `SupabaseApi` y Gson convierte las respuestas
en DTOs. `ModuloRed` envía la clave pública en `apikey` y los repositorios envían el
JWT del usuario en `Authorization: Bearer`. Los permisos se validan en el servidor con RLS.

La URL base y la clave pública proceden de `supabase.local.properties` o de las
variables de entorno `SUPABASE_URL` y `SUPABASE_PUBLISHABLE_KEY`. Gradle genera los
campos correspondientes de `BuildConfig`; sin configuración el proyecto compila y la
app muestra el aviso de sin conexión. No se incluyen credenciales administrativas.

| Función | Ruta relativa a la URL del proyecto |
| --- | --- |
| Registro | `/auth/v1/signup` |
| Inicio y renovación de sesión | `/auth/v1/token` |
| Ofertas | `/rest/v1/ofertas` |
| Perfiles y negocios | `/rest/v1/perfiles`, `/rest/v1/negocios` |
| Pedidos y notificaciones | `/rest/v1/pedidos`, `/rest/v1/notificaciones` |
| Operaciones transaccionales | `/rest/v1/rpc/...` |
| Fotografías | `/storage/v1/object/ofertas/...` |

## Identidad y sesiones

El UUID autenticado identifica al perfil. El rol se obtiene de `public.perfiles`,
y cada negocio se vincula con su propietario mediante `propietario_id`.
El trigger de registro crea el perfil y, cuando corresponde, el negocio.

El access token, el refresh token y su vencimiento se guardan en la tabla
`sesion_activa` de Room. `RepositorioAutenticacion` renueva el token cuando vence en
menos de un minuto; una renovación rechazada elimina la sesión local. Cerrar sesión
borra la sesión y la siguiente sincronización vacía pedidos y notificaciones.

El registro admite la configuración de Supabase con o sin confirmación de correo.
Cuando el servidor devuelve una sesión, la aplicación inicia sesión directamente.
Si exige confirmación, muestra el aviso correspondiente; el usuario confirma desde
el correo y luego inicia sesión en la app. No hay recuperación de contraseña desde
la app.

## Datos y caché

El servidor conserva ofertas, pedidos y notificaciones. Cada repositorio los pide
con Retrofit, los guarda en Room (`rescatapp.db`) y las pantallas observan Room con
`Flow`. Al sincronizar se reemplazan los registros y se borran los que ya no existen
en el servidor.

El catálogo público y las ofertas del propietario se presentan como listas
separadas. La consulta normal excluye ofertas eliminadas. Los clientes ven las
ofertas disponibles según las políticas del servidor.

Las listas se actualizan al iniciar sesión, después de cada escritura y cada 10
segundos mientras la app está visible (`SincronizarDatosUseCase`). Se utiliza sondeo
REST, no una suscripción WebSocket de Realtime.

Sin conexión se conserva la última lectura disponible y se informa del error.
Las escrituras requieren respuesta del servidor; no existe una cola de operaciones
pendientes para trabajar sin conexión.

## Operaciones del servidor

| RPC | Comportamiento |
| --- | --- |
| `reservar_oferta` | Verifica disponibilidad, descuenta stock y crea el pedido y sus avisos en una transacción |
| `cancelar_pedido` | Cancela un pedido reservado y devuelve el stock una sola vez |
| `avanzar_pedido` | Valida el estado esperado y exige el código de rescate para confirmar la entrega |
| `cambiar_estado_oferta` | Pausa o reactiva una oferta del propietario |
| `eliminar_oferta` | Oculta la oferta y conserva sus pedidos anteriores |

Cada reserva envía un UUID de solicitud nuevo. El servidor calcula los precios y snapshots del
pedido. Android no modifica directamente el stock durante reservas y cancelaciones.

Los pedidos conservan la fecha de retiro, horario, imagen, precio y código del
servidor. Las notificaciones pertenecen a un destinatario concreto; leerlas o
eliminarlas se persiste en Supabase. Al sincronizar, las nuevas sin leer se muestran
como notificación del sistema.

Las imágenes JPG, PNG o WebP de hasta 5 MB se almacenan en el bucket `ofertas`,
bajo `<uuid-propietario>/<uuid-archivo>.<extension>`. El catálogo utiliza sus URLs
públicas y las políticas restringen las escrituras al propietario.

## Definición del servidor

| Archivo | Contenido |
| --- | --- |
| `001_estructura_y_permisos.sql` | Tablas, índices, triggers, permisos y políticas RLS |
| `002_operaciones_seguras.sql` | RPC de ofertas y pedidos |
| `003_imagenes_y_realtime.sql` | Bucket, políticas de Storage y publicación de Realtime |
| `005_verificar_configuracion.sql` | Consultas de configuración y permisos |
| `006_pruebas_transaccionales.sql` | Comprobaciones de reglas del servidor con reversión de las filas de prueba |

Los números conservan la identificación de los scripts existentes. Los campos
opcionales `origen` y `legacy_id` son metadatos históricos de los registros y no
representan otra conexión activa.

## Límites funcionales

- Los pagos están simulados y no producen cobros reales.
- Las notificaciones dentro de la app no incluyen entrega push con la app cerrada.
- El impacto considera pedidos recogidos; los reservados no cuentan como rescates completados.
- `rescatapp.db` es solo una caché; un cambio de versión la recrea.
- Las pruebas unitarias usan un `SupabaseApi` falso en memoria, sin credenciales reales.
