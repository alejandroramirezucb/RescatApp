# Conexion de Android con Supabase

La aplicacion usa Supabase Auth, PostgreSQL a traves de REST con RLS y Storage.
La clave publishable se envia como `apikey`; las operaciones privadas incluyen
el JWT del usuario. Android no incluye claves secretas ni `service_role`.
MockAPI deja de ser la fuente utilizada por la inyeccion de dependencias.

## Configuracion de cada integrante

En la raiz, copiar `supabase.local.properties.example` a
`supabase.local.properties` y completar la URL del proyecto y su publishable key.
El archivo local esta excluido de Git. Gradle tambien acepta las variables de
entorno `SUPABASE_URL` y `SUPABASE_PUBLISHABLE_KEY`.
En GitHub Actions, configurar ambas como variables del repositorio en
Settings > Secrets and variables > Actions > Variables.

Se requieren los SQL 001 a 003 y las cuentas con sus perfiles. Los SQL 004 y 007
cargan las ofertas de ejemplo y enlazan sus fotos. No repetir los scripts de
estructura en un proyecto donde ya se ejecutaron.

## Sesiones y datos por cuenta

- El login consulta Supabase Auth; el rol procede de `public.perfiles`.
- El registro envia los metadatos al trigger que crea perfil y negocio.
- Con confirmacion de correo activa, registrarse muestra un aviso y no inicia
  sesion hasta que la cuenta este confirmada.
- Access token, refresh token y verificador PKCE se cifran mediante AES-GCM con
  una clave de Android Keystore. Los tokens no se almacenan en Room.
- Se renueva el token antes de vencer y se reintenta una solicitud rechazada
  por expiracion. Una renovacion rechazada elimina la sesion local.
- La base `supabase_cache.db` guarda snapshots con clave compuesta por cuenta y
  recurso. Cambiar de cuenta vacia inmediatamente los datos expuestos en memoria.
  Cerrar sesion tambien elimina la cache privada de esa cuenta.
- La base antigua `rescatapp.db` no se importa ni asigna a usuarios nuevos.
  Sus usuarios y pedidos de demostracion no se utilizan en el flujo conectado.

## Catalogo, ofertas, pedidos y fotos

- Inicio y Explorar usan el mismo catalogo del servidor. Las listas se actualizan
  despues de cada escritura y automaticamente cada 10 segundos mientras la app
  esta visible. Es sondeo REST; no es una suscripcion WebSocket de Realtime.
- La app conserva la ultima cache si no hay red y muestra un aviso para reintentar.
  No se simulan escrituras exitosas sin conexion.
- Las ofertas se crean directamente en Supabase, sin duplicarlas para publicarlas.
  La propiedad se obtiene del negocio vinculado al UUID autenticado.
- Pausar, reactivar y eliminar llaman a sus RPC. Eliminar conserva los pedidos.
- Las reservas usan `reservar_oferta` con una solicitud UUID persistida para
  reintentar una respuesta perdida sin reservar dos veces. El stock se modifica
  exclusivamente en el servidor durante reservas y cancelaciones.
- La preparacion y entrega usan `avanzar_pedido`; la entrega requiere escribir
  el codigo real que muestra el cliente. Se usa el codigo calculado por Supabase.
- Las notificaciones privadas se consultan por la cuenta permitida por RLS;
  leer y eliminar se persisten en Supabase. Esto no implementa notificaciones
  push cuando la aplicacion esta cerrada.
- Las nuevas imagenes JPG/PNG/WebP, de hasta 5 MiB, se suben a
  `ofertas/<uuid-propietario>/<uuid-archivo>.<extension>`.
- Las ofertas tienen fecha de retiro editable. Los pedidos conservan el horario,
  la fecha, la imagen y el precio que tenian al reservar.
- El impacto considera pedidos recogidos. El resumen de ventas de hoy filtra
  pedidos recogidos y la fecha de Bolivia. Los pagos siguen siendo simulados.

## Correo y recuperacion

Autorizar `rescatapp://auth/callback` en Authentication > URL Configuration.
Android recibe ese enlace y canjea el codigo mediante PKCE. Abrir el enlace en
el mismo telefono desde el que se inicio el registro o la recuperacion.
El login incluye "Olvide mi contrasena" y el enlace recibido abre el formulario
para cambiarla. Solo se permite una solicitud PKCE pendiente por instalacion;
una nueva solicitud reemplaza a la anterior.

La entrega de confirmaciones y recuperaciones a destinatarios externos sigue
dependiendo de configurar SMTP en Supabase. Mientras tanto, usar las cuentas
de prueba ya confirmadas; no desactivar RLS ni incorporar credenciales de admin.

## Prueba manual con dos dispositivos o sesiones

1. Iniciar como cliente y comprobar que aparecen las cinco ofertas del servidor.
2. Reservar un pack. Revisar que aparezca el pedido y se reduzca el stock en
   Table Editor. Reabrir la app y verificar la persistencia.
3. Iniciar con otro cliente. No debe ver pedidos ni notificaciones del primero.
4. Iniciar como propietario del negocio. Debe ver sus ofertas y sus pedidos.
5. Pausar una oferta con stock. En el cliente debe desaparecer automaticamente;
   reactivarla debe devolverla al catalogo sin modificar su stock.
6. Cambiar un pedido de reservado a preparando y despues a listo. Introducir
   primero un codigo incorrecto y despues el codigo mostrado por el cliente.
7. Cancelar otro pedido reservado y verificar que el stock se devuelve una vez.
8. Eliminar una oferta y comprobar que desaparezca del catalogo y que sus pedidos
   permanezcan en el historial.
9. Publicar una oferta con foto y verificarla desde el otro dispositivo.
10. Probar sin red: lectura desde cache y error visible al intentar escribir.
    Volver a conectar y comprobar la actualizacion automatica.
11. Cuando haya SMTP, probar registro confirmado y recuperacion de contrasena.

Comprobaciones locales:

```powershell
.\gradlew.bat spotlessCheck :app:testDebugUnitTest :app:assembleDebug --no-daemon
```

Las pruebas HTTP de integracion usan un servidor local, no modifican el Supabase
del equipo ni necesitan contrasenas reales. La validacion final de login,
registro, Storage y flujos entre dispositivos requiere ejecutar la app.
