# Arquitectura de la conexión con Supabase

## Servicios y configuración

La aplicación utiliza Supabase Auth, PostgreSQL mediante REST y Supabase Storage.
`SupabaseHttp` envía la clave pública en `apikey` y el JWT del usuario autenticado
en `Authorization: Bearer`. Los permisos se validan en el servidor con RLS.

La URL base y la clave pública proceden de `supabase.local.properties` o de las
variables de entorno `SUPABASE_URL` y `SUPABASE_PUBLISHABLE_KEY`. Gradle genera los
campos correspondientes de `BuildConfig`. No se incluyen credenciales administrativas.

| Función | Ruta relativa a la URL del proyecto |
| --- | --- |
| Registro | `/auth/v1/signup` |
| Inicio y renovación de sesión | `/auth/v1/token` |
| Consulta de usuario y cambio de contraseña | `/auth/v1/user` |
| Recuperación de acceso | `/auth/v1/recover` |
| Cierre de sesión | `/auth/v1/logout` |
| Ofertas | `/rest/v1/ofertas` |
| Perfiles y negocios | `/rest/v1/perfiles`, `/rest/v1/negocios` |
| Pedidos y notificaciones | `/rest/v1/pedidos`, `/rest/v1/notificaciones` |
| Operaciones transaccionales | `/rest/v1/rpc/...` |
| Fotografías | `/storage/v1/object/ofertas/...` |

## Identidad y sesiones

El UUID autenticado identifica al perfil. El rol se obtiene de `public.perfiles`,
y cada negocio se vincula con su propietario mediante `propietario_id`.
El trigger de registro crea el perfil y, cuando corresponde, el negocio.

Access token, refresh token y verificador PKCE se cifran con AES-GCM y una clave de
Android Keystore. Los tokens no se almacenan en Room. La conexión renueva el token
antes de vencer y reintenta una solicitud rechazada por expiración. Una renovación
rechazada elimina la sesión local.

El registro admite la configuración de Supabase con o sin confirmación de correo.
Cuando el servidor devuelve una sesión, la aplicación inicia sesión directamente.
Si exige confirmación, muestra el aviso correspondiente.

El enlace Android `rescatapp://auth/callback` recibe códigos de autenticación y
recuperación. El canje utiliza PKCE, por lo que el enlace debe abrirse en la misma
instalación que inició el flujo. Una solicitud nueva reemplaza el verificador
pendiente de la anterior. El envío de correos depende de la configuración SMTP.

## Datos y caché

`SupabaseConexion` coordina un snapshot compartido por las pantallas. El servidor
conserva ofertas, pedidos y notificaciones. La caché `supabase_cache.db` utiliza una
clave compuesta por cuenta y recurso. Cambiar de cuenta vacía los datos expuestos
en memoria; cerrar sesión elimina la caché privada de esa cuenta.

El catálogo público y las ofertas del propietario se presentan como listas
separadas. La consulta normal excluye ofertas eliminadas. Los clientes ven las
ofertas disponibles según las políticas del servidor.

Las consultas paginan los resultados en bloques de 500 registros. Las listas se
actualizan después de cada escritura y cada 10 segundos mientras la app está
visible. Se utiliza sondeo REST, no una suscripción WebSocket de Realtime.

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

Las reservas incluyen un UUID de solicitud persistido para reintentar una respuesta
perdida sin duplicar la reserva. El servidor calcula los precios y snapshots del
pedido. Android no modifica directamente el stock durante reservas y cancelaciones.

Los pedidos conservan la fecha de retiro, horario, imagen, precio y código del
servidor. Las notificaciones pertenecen a un destinatario concreto; leerlas o
eliminarlas se persiste en Supabase.

Las imágenes JPG, PNG o WebP de hasta 5 MiB se almacenan en el bucket `ofertas`,
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
- La base anterior `rescatapp.db` se conserva por compatibilidad y no se importa a cuentas reales.
- Las pruebas HTTP de Android utilizan un servidor local simulado, sin credenciales reales.
