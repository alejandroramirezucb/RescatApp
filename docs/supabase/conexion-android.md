# Conexion de Android con Supabase

La aplicacion usa Supabase Auth, PostgreSQL por REST con RLS y Storage mediante
Retrofit y Gson ([SupabaseApi.kt](../../app/src/main/java/com/rescatapp/core/data/remote/SupabaseApi.kt)).
La clave publishable se envia como `apikey`; las operaciones privadas incluyen
el JWT del usuario. Android no incluye claves secretas ni `service_role`.

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

## Sesion

- El login consulta Supabase Auth; el rol procede de `public.perfiles` y el
  nombre del negocio de `public.negocios`.
- El registro envia los metadatos al trigger que crea perfil y negocio.
- Con confirmacion de correo activa, registrarse muestra un aviso y no inicia
  sesion. El usuario confirma desde el correo y luego inicia sesion en la app.
- El access token, el refresh token y su vencimiento se guardan en la tabla
  `sesion_activa` de Room. Antes de cada solicitud se renueva el token si vence
  en menos de un minuto; si la renovacion es rechazada se cierra la sesion.
- Cerrar sesion borra `sesion_activa`; la siguiente sincronizacion deja vacios
  los pedidos y notificaciones de la cuenta anterior.

## Datos y sincronizacion

- Cada repositorio pide sus datos a Supabase, los guarda en Room y las pantallas
  observan Room con `Flow`. Si no hay red se muestran los ultimos datos guardados
  y un aviso con "Reintentar". No se simulan escrituras sin conexion.
- La app sincroniza al iniciar sesion, despues de cada operacion y cada 10
  segundos mientras esta visible. Es sondeo REST, no Realtime.
- Las ofertas se crean directamente en Supabase con el negocio del usuario.
  Pausar, reactivar y eliminar llaman a sus RPC. Eliminar conserva los pedidos.
- Las reservas usan `reservar_oferta`; el stock solo cambia en el servidor
  durante reservas y cancelaciones.
- La preparacion y entrega usan `avanzar_pedido`; la entrega requiere el codigo
  que muestra el cliente.
- Las notificaciones las crea el servidor. Al sincronizar, las nuevas sin leer
  se muestran como notificacion del sistema. No hay notificaciones push con la
  app cerrada.
- Las fotos de hasta 5 MB se suben a
  `ofertas/<uuid-propietario>/<uuid-archivo>.<extension>`.

## Prueba manual con dos dispositivos o sesiones

1. Iniciar como cliente y comprobar que aparecen las ofertas del servidor.
2. Reservar un pack. Revisar que aparezca el pedido y se reduzca el stock en
   Table Editor. Reabrir la app y verificar que la sesion sigue iniciada.
3. Iniciar con otro cliente. No debe ver pedidos ni notificaciones del primero.
4. Iniciar como propietario del negocio. Debe ver sus ofertas y sus pedidos.
5. Pausar una oferta con stock. En el cliente debe desaparecer; reactivarla
   debe devolverla al catalogo sin modificar su stock.
6. Cambiar un pedido de reservado a preparando y despues a listo. Introducir
   primero un codigo incorrecto y despues el codigo mostrado por el cliente.
7. Cancelar otro pedido reservado y verificar que el stock se devuelve una vez.
8. Eliminar una oferta y comprobar que sus pedidos permanezcan en el historial.
9. Publicar una oferta con foto y verificarla desde el otro dispositivo.
10. Probar sin red: lectura desde Room y error visible al intentar escribir.

Comprobaciones locales:

```powershell
.\gradlew.bat spotlessCheck :app:testDebugUnitTest :app:assembleDebug --no-daemon
```

Las pruebas unitarias usan un `SupabaseApi` falso en memoria; no modifican el
Supabase del equipo ni necesitan contrasenas reales.
