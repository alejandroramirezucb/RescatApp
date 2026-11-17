# Preparar Supabase para RescatApp

## Alcance y estado

Este paquete prepara el backend en un PROYECTO NUEVO dedicado a RescatApp.
No ejecutar sobre la tienda u otro proyecto existente.
No contiene contraseñas ni claves. No cambió la conexión Android.
Los SQL fueron revisados con un analizador PostgreSQL y PL/pgSQL.
Todavía no se ejecutaron en un servidor PostgreSQL/Supabase: los pasos 005 y 006
son parte obligatoria de la comprobación en tu proyecto.

La primera carga conserva las cinco ofertas de la captura MockAPI del 17/11/2026.
Los datos de Room se mantienen intactos en los dispositivos. No se importan
pedidos sin saber quién es su cliente ni se transfieren los tokens simulados.

## 1. Crear proyecto

1. En https://supabase.com/dashboard crea una organización del equipo o usa
   una organización donde todos deban tener acceso a RescatApp.
2. Crea un proyecto nuevo llamado RescatApp. Para una demo, el plan Free puede
   servir si está disponible para tu organización; revisa los límites del panel.
3. Selecciona South America / São Paulo si aparece, por cercanía con Bolivia.
4. Genera una contraseña de base de datos y guárdala de forma privada.
   NO es la contraseña de login de los clientes y NO se coloca en Android.
5. Espera a que finalice la creación.
6. En los ajustes de la organización invita al equipo con permisos acordes
   a su trabajo. Cada persona entra con su propia cuenta del panel.

Los miembros del panel administran el proyecto. Las cuentas de clientes y
negocios se crean aparte en Authentication: no son lo mismo.

## 2. Ejecutar estructura y permisos

En SQL Editor crea una consulta nueva, copia TODO el archivo indicado y pulsa Run.
No copies las marcas de Markdown; los archivos son SQL puro.

Ejecutar, en orden:

| Archivo | Prepara |
| --- | --- |
| supabase/sql/001_estructura_y_permisos.sql | Tablas, índices, perfiles al registrarse, restricciones y RLS |
| supabase/sql/002_operaciones_seguras.sql | Reservar, cancelar, avanzar, pausar/reactivar y eliminar |
| supabase/sql/003_imagenes_y_realtime.sql | Bucket de fotos, permisos y publicación Realtime |

Los tres se ejecutan una sola vez sobre el proyecto nuevo. No son scripts
de restablecimiento y no borran tablas ajenas. Si aparece un error, conserva
el mensaje completo y el número de archivo; no desactives RLS para continuar.

Para copiar un archivo desde PowerShell:

    Get-Content -Raw .\supabase\sql\001_estructura_y_permisos.sql | Set-Clipboard

Tablas resultantes:

| Tabla | Uso |
| --- | --- |
| perfiles | Nombre, rol CLIENTE/NEGOCIO y contacto privado; el correo y contraseña viven en Auth |
| negocios | Un negocio por propietario, nombre público y datos del local |
| ofertas | Oferta, negocio, precio, stock, fecha/horas, imagen y estado |
| pedidos | Cliente real, negocio, oferta, estado y copia histórica de los datos de compra |
| notificaciones | Avisos privados por destinatario |
| catalogo_actualizacion | Contador público sin datos privados para actualizar el catálogo |

## 3. Preparar autenticación

En Authentication > Sign In / Providers, o la sección equivalente del panel:

1. Habilita Email y registro con correo/contraseña.
2. Para el funcionamiento final, mantén activada la confirmación de correo.
3. Configura un SMTP propio si registrarán personas que no son miembros
   del equipo de Supabase. El servicio de correo de prueba tiene restricciones.
4. En Authentication > URL Configuration añade este Redirect URL exacto:

       rescatapp://auth/callback

El callback necesita su implementación Android; se añadirá al conectar Auth.
No indica que el enlace ya funcione en la versión actual de la app.
El Site URL es la página de respaldo de los correos: usa una página HTTPS
del proyecto si tienen una. No uses la URL de la base como una página web ficticia.

Para SMTP el panel pide host, puerto, usuario, contraseña y remitente.
Configura esos datos directamente en Supabase. No deben ir al APK.
Antes de activar confirmaciones para el público, verifica que recibes
un correo real y que el enlace llega al flujo de la app.

Para la primera comprobación puedes crear las dos cuentas de prueba con
autoconfirmación desde el Dashboard, manteniendo activadas las confirmaciones
para los futuros registros. Así estas cuentas no dependen del envío de correo.

Si temporalmente desactivas Confirm email para probar registro sin SMTP,
eso permite registrarse sin verificar la propiedad del correo. Rehabilítalo
y configura el correo antes de presentar ese flujo como registro verificado.

## 4. Crear cuentas iniciales

DESPUÉS del archivo 001:

1. Abre Authentication > Users > Add user y elige crear un usuario.
2. Crea una cuenta de cliente con un correo que controles y una contraseña propia.
3. Crea otra cuenta distinta para el responsable de Panadería La Central.
4. Si la creación manual ofrece Auto Confirm User, actívalo para estas cuentas
   de prueba. Usa contraseñas distintas y guárdalas para tus pruebas.
5. Comprueba en Table Editor > perfiles que aparecen ambos usuarios.
   Inicialmente pueden figurar como CLIENTE; el siguiente script asigna el negocio.

No insertes contraseñas directamente en public.perfiles ni crees las cuentas
editando auth.users con SQL.

El futuro registro Android enviará estos datos adicionales a Supabase Auth:

    nombre: nombre de la persona
    rol: CLIENTE o NEGOCIO
    telefono: texto
    direccion: texto
    nombreNegocio: obligatorio cuando rol es NEGOCIO

El trigger crea el perfil y, para NEGOCIO, su negocio. Su propietario se toma
del ID generado por Auth, no de un ID enviado libremente por la app.
Modificar user_metadata después no cambia el rol protegido de perfiles.

## 5. Cargar las cinco ofertas

Abre supabase/sql/004_cargar_ofertas_demo.sql y reemplaza SOLO:

    REEMPLAZAR_CORREO_NEGOCIO
    REEMPLAZAR_CORREO_CLIENTE

con los correos de las cuentas recién creadas. No reemplaces esos textos
por contraseñas o UUID. Ejecuta el archivo entero en SQL Editor.

Resultado:

- La cuenta de negocio queda como NEGOCIO.
- Se crea Panadería La Central vinculada a ese propietario.
- Se importan las cinco ofertas: IDs antiguos 1, 2, 3, 4 y 6.
- Se conserva origen=mockapi y legacy_id; los IDs internos nuevos pueden ser otros.
- La fecha de retiro se establece para MAÑANA en Bolivia, evitando cargar ofertas
  con horarios ya vencidos. Se puede editar después desde Table Editor.
- Ejecutarlo de nuevo no duplica ni restablece el stock de esas ofertas.

No importes además ofertas.csv en la misma tabla: el SQL ya hizo la carga.
El CSV original usa nombres de campos distintos y no asigna propietarios.

## 6. Comprobar configuración y operaciones

1. Ejecuta supabase/sql/005_verificar_configuracion.sql.
2. Deben aparecer las seis tablas con rowsecurity=true.
3. Deben aparecer los dos perfiles y un negocio asociado al propietario.
4. El recuento de ofertas_mockapi debe ser 5.
5. El bucket ofertas debe ser público y tener límite de 5 MiB.
6. En la publicación deben estar negocios, ofertas, pedidos, notificaciones
   y catalogo_actualizacion.
7. Las cuatro comprobaciones de permisos al final deben devolver false.
8. Ejecuta supabase/sql/006_pruebas_transaccionales.sql completo.

El 006 crea una oferta temporal y prueba:

- Reserva y reintento con la misma solicitud sin duplicar pedido.
- Rechazo de una reserva sin stock.
- Cancelación con devolución de una sola unidad, aunque se reintente.
- Ocultación del pedido y rechazo de cancelación por un tercero.
- Pausar y reactivar conservando existencias.
- Rechazo de un cambio de rol desde el usuario normal.
- Avance ordenado, reintento del mismo paso y rechazo de código incorrecto.
- Retiro y conservación del historial tras eliminar la oferta.

Debe devolver: Pruebas completadas; datos de prueba revertidos.
Si falla, comparte el error. Las filas de prueba se revierten con ROLLBACK;
pueden quedar saltos normales en las secuencias numéricas.
Esto no prueba aún conexiones simultáneas, red móvil, correo ni Android.

El SQL Editor usa postgres y normalmente omite RLS: ver datos allí NO prueba
que sean accesibles correctamente desde un cliente. El 006 simula el rol
authenticated; después se harán pruebas reales con cuentas y dispositivos distintos.

## 7. Cargar fotos

La API capturada no tenía fotos. Para mostrarlas en todos los teléfonos
hay que subir archivos reales, no URI content:// del dispositivo.

1. Abre Storage > ofertas.
2. Obtén el UUID del propietario en perfiles, ayudándote del resultado de 005.
3. Crea una carpeta con ese UUID exacto.
4. Sube cinco fotos JPG, máximo 5 MiB por archivo, con estos nombres:

       mockapi-1.jpg
       mockapi-2.jpg
       mockapi-3.jpg
       mockapi-4.jpg
       mockapi-6.jpg

5. En supabase/sql/007_vincular_fotos.sql reemplaza la URL del proyecto y
   el correo del negocio, y ejecútalo.
6. Copia una imagen_url de ofertas y ábrela en una ventana sin sesión:
   debe cargar la foto.

El bucket es público porque son imágenes del catálogo. No subas documentos,
contraseñas, comprobantes privados ni información de cuentas.
Las subidas de la futura app usarán la carpeta del usuario autenticado.
También se admiten PNG y WebP; si usas esos formatos, asigna sus URL reales
manualmente en ofertas.imagen_url en lugar de ejecutar el mapeo de archivos JPG.

## 8. Estados, reservas y pagos

Ofertas:

- ACTIVA: visible si tiene stock, el negocio está activo y no venció el retiro.
- PAUSADA: conserva datos y stock; oculta al cliente.
- ELIMINADA: fuera del catálogo; se conserva para no romper los pedidos históricos.
- Agotada: cantidad_disponible=0; no es un estado de pausa.

Los horarios actuales no cruzan medianoche, igual que el validador Android.
El servidor utiliza la fecha de retiro y America/La_Paz.

Pedidos:

    RESERVADO -> PREPARANDO -> LISTO -> RECOGIDO
    RESERVADO -> CANCELADO

La cancelación y el descuento de stock se ejecutan en la misma transacción
que el cambio del pedido. El cliente no envía el importe ni modifica el estado
directamente: el servidor obtiene los precios de la oferta.
La confirmación del retiro exige el código correcto.

Los pagos siguen siendo DEMO. pago_simulado=true es obligatorio. Esta preparación
no cobra dinero, no valida un depósito bancario y no crea una pasarela real.

Contratos RPC de la futura app:

| Función | Parámetros |
| --- | --- |
| reservar_oferta | p_oferta_id, p_solicitud_id (UUID nuevo por intento de compra; conservarlo para reintentos) |
| cancelar_pedido | p_pedido_id |
| avanzar_pedido | p_pedido_id, p_estado_esperado, p_codigo_rescate cuando se confirma el retiro |
| cambiar_estado_oferta | p_oferta_id, p_estado: ACTIVA o PAUSADA |
| eliminar_oferta | p_oferta_id |

Se llaman por POST /rest/v1/rpc/NOMBRE_FUNCION con la sesión real del usuario.
No uses DELETE directo sobre ofertas o pedidos ni des permisos generales para hacerlo.

## 9. Preparar la información para conectar Android

En Connect o Settings > API Keys del proyecto obtiene:

- Project URL: https://REFERENCIA.supabase.co
- Publishable key, normalmente sb_publishable_...

Puedes compartir la URL y esa clave publicable para configurar la conexión,
pero es preferible colocarlas directamente en el archivo local.
NO compartir contraseña de la base, clave secret, service_role, access token
personal de Supabase, contraseña de usuarios ni sesiones JWT.

Desde la raíz:

    Copy-Item .\supabase.local.properties.example .\supabase.local.properties

Edita supabase.local.properties y sustituye las dos líneas, sin comillas:

    SUPABASE_URL=https://REFERENCIA.supabase.co
    SUPABASE_PUBLISHABLE_KEY=sb_publishable_TU_CLAVE

El archivo está excluido de Git. No cambies local.properties del Android SDK.
La versión actual de la app todavía no lee esta configuración: se conectará
en la siguiente implementación, después de comprobar Supabase.

Mensaje que puedes enviarme:

    Proyecto RescatApp creado.
    Región:
    URL del proyecto:
    supabase.local.properties configurado: sí/no
    SQL 001, 002 y 003: ejecutados / error exacto
    Cuentas de cliente y negocio: creadas
    Correos de las cuentas demo (sin contraseñas):
    SQL 004: ejecutado; cantidad de ofertas:
    SQL 005: resultado de los permisos:
    SQL 006: resultado:
    Fotos y SQL 007: listos / pendientes
    Confirm email: activado/desactivado
    SMTP y recepción de correos: comprobados / pendientes
    Datos de dispositivos Room que quiero conservar: ninguno / cuáles

No hace falta dar acceso administrador al panel para conectar Android con la
URL y clave publicable. Los SQL privilegiados los ejecutas tú en el panel.

## 10. Alcance de la conexion Android

El estado implementado y sus limites estan en [conexion-android.md](conexion-android.md).
La siguiente lista conserva el alcance definido al preparar la migracion:

1. Leer la configuración local en Gradle sin publicar credenciales administrativas.
2. Reemplazar registro/login local por Auth, gestionar confirmación, recuperación,
   renovación de sesión, cierre de sesión y callback Android.
3. Usar perfiles/negocios reales y quitar la propiedad basada en nombres de UsuarioDemo.
4. Unificar el catálogo: retirar MockAPI como fuente activa y desactivar los seeds
   que vuelven a llenar Room con ejemplos antiguos.
5. Migrar la caché Room sin mezclar IDs anteriores con los IDs del servidor.
   Las copias locales por usuario se separan y se limpian al cerrar/cambiar cuenta.
6. Usar OkHttp y Gson para REST/RPC y Room/Flow para cache/UI.
7. Guardar solo campos modificados al editar, evitando sobrescribir el stock
   con una copia antigua cuando se cambia el precio.
8. Usar las funciones RPC de reservas, cancelación y estados; no duplicar estas
   operaciones modificando el stock de Room como si fuera la autoridad.
9. Subir fotos a Storage y guardar su URL.
10. Escuchar catalogo_actualizacion y refrescar la caché del catálogo, incluyendo
    retirar ofertas pausadas, eliminadas o agotadas. Escuchar pedidos y avisos
    privados; refrescar al reconectar y al volver a la app, también por vencimientos.
11. Separar las listas del negocio de las del catálogo público. El dueño puede
    consultar sus pausadas, pero la UI excluye las ELIMINADAS de sus listas normales.
12. Recalcular ventas de hoy/semana usando fechas reales; contar impacto de
    pedidos recogidos y respetar cancelaciones. Los nombres actuales de métricas
    locales no garantizan que estén filtradas por fecha.
13. Distinguir avisos dentro de la app y push con la app cerrada: Realtime no
    sustituye FCM ni otro servicio de push. Esa entrega requiere implementación adicional.
14. Mantener el flujo de pago simulado claramente identificado.
15. Quitar el acceso de negocio sin autenticación. El invitado puede explorar;
    reservar y administrar requiere login. Las contraseñas demo no van en el APK.
16. Añadir edición de perfil dentro de los permisos y un proceso de eliminación
    de cuenta que respete pedidos históricos; actualmente las relaciones impiden
    borrar usuarios con pedidos o negocios sin un tratamiento previo.
17. Probar desconexión, reintentos, errores de sesión, concurrencia, dos clientes,
    dos negocios y reinicio de la app antes de considerar terminada la migración.

Los datos históricos de los dispositivos se revisarán aparte: las contraseñas
SHA-256 y los tokens simulados no se copian como credenciales de Auth.
Los pedidos sin cliente identificable no se asignan a una persona por suposición.

## Referencias oficiales consultadas

- https://supabase.com/docs/guides/platform/regions
- https://supabase.com/docs/guides/auth/managing-user-data
- https://supabase.com/docs/guides/auth/passwords
- https://supabase.com/docs/guides/auth/auth-smtp
- https://supabase.com/docs/guides/auth/redirect-urls
- https://supabase.com/docs/guides/database/functions
- https://supabase.com/docs/guides/database/postgres/row-level-security
- https://supabase.com/docs/guides/storage/buckets/fundamentals
- https://supabase.com/docs/guides/realtime/postgres-changes
- https://supabase.com/docs/guides/getting-started/api-keys
