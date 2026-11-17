# Inventario de API y persistencia para migrar RescatApp

Revisión del 17 de noviembre de 2026 sobre el commit
562c9d8515b155e34b7f52003d169a8321aad037.
Se distingue lo comprobado de la propuesta de migración.
**Documento historico previo a la migracion.** El estado actual de Android esta en conexion-android.md; el cliente antiguo ya se retiro.
La preparación ejecutable posterior está en puesta-en-marcha.md y supabase/sql.
Mantiene IDs enteros para negocios/ofertas/pedidos por compatibilidad con Kotlin;
las identidades de Auth y los perfiles utilizan UUID.

## Resultado comprobado

- Un servicio Retrofit con un recurso remoto: ofertas.
- Cuatro operaciones HTTP: consultar, crear, actualizar y eliminar.
- La lectura real respondió HTTP 200 con cinco ofertas y once campos.
- Las cinco ofertas pertenecen a Panadería La Central.
- No hay IDs duplicados, campos ausentes ni valores nulos en la captura.
- Room conserva cinco tablas locales que no se obtienen de MockAPI.
- Inicio, Explorar, Detalle y reservas consultan ofertas locales.
- La pestaña «En línea» utiliza un repositorio remoto separado.
- Publicar envía una copia a MockAPI. No existe sincronización general entre
  ambos repositorios ni una correspondencia persistente entre sus IDs.

El alcance es la API integrada en este repositorio. No permite descubrir
recursos que un compañero pudiera tener en MockAPI sin integrarlos en la app.

## Endpoints actuales

Servicio anterior: MockAPI (URL retirada tras la migracion).

| Método | Ruta | Función Kotlin | Datos |
| --- | --- | --- | --- |
| GET | ofertas | obtenerOfertas | Lista de OfertaDto |
| POST | ofertas | crearOferta | Recibe una OfertaDto y devuelve la creada |
| PUT | ofertas/{id} | actualizarOferta | Devuelve la oferta actualizada |
| DELETE | ofertas/{id} | eliminarOferta | Elimina el registro remoto |

Solo se ejecutó GET. No se probaron los métodos que escriben o eliminan datos.
La app no declara GET ofertas/{id}, API de usuarios, login remoto,
API de pedidos, API de notificaciones, subida remota de fotos ni API bancaria.

Fuentes en app/src/main/java/com/rescatapp/core/data/remote:
ApiService.kt, RetrofitClient.kt, OfertaDto.kt y RepositorioOfertasRemotas.kt.

## Captura realizada

Directorio relativo: supabase/migracion/capturas/20261117T203732921Z/

| Archivo | Contenido |
| --- | --- |
| ofertas.json | Respuesta HTTP original, sin transformar |
| ofertas.csv | Los mismos registros en formato tabular |
| inventario.json | URL, método, HTTP, fecha UTC, tipos, cantidades y SHA-256 |

La captura está excluida de Git para no publicar registros por accidente.
Fecha: 2026-11-17T20:37:40.9605681Z, las 16:37 en Bolivia.
SHA-256 del JSON:

    C417EF74E47A1F7D45F284E43FCD30D6FE812CD1B1BA065D8F701C7B06ED5FFF

| ID remoto | Nombre | Categoría | Unidades |
| --- | --- | --- | --- |
| 1 | Pack Sorpresa | PANADERIA | 5 |
| 2 | Pack Medialunas | PANADERIA | 4 |
| 3 | Pack Salteñas | COMIDA | 3 |
| 4 | Torta del Día | POSTRES | 2 |
| 6 | Pack Croissants | PANADERIA | 2 |

Son resultados de esa lectura; el servidor puede cambiar posteriormente.

## Campos de ofertas y propuesta de destino

Los nombres del destino son una propuesta. Todavía no se crearon tablas.

| Campo actual | Tipo JSON observado | Propuesta PostgreSQL | Consideración |
| --- | --- | --- | --- |
| id | string | legacy_id text | Conservar con el origen; no asumir coincidencia con Room |
| nombre | string | nombre text | Nombre del pack |
| comercio | string | negocio_id integer y nombre público | El nombre no identifica al propietario |
| categoria | string | categoria text con valores permitidos | PANADERIA, COMIDA, POSTRES y CAFETERIA en el modelo |
| descripcion | string | descripcion text | Mantener el contenido |
| pesoKg | number | peso_kg numeric | Validar valor positivo |
| precioNormal | number | precio_normal numeric | Importe exacto en Bs. |
| precioRescate | number | precio_rescate numeric | Validar contra precio normal |
| cantidadDisponible | entero | cantidad_disponible integer | Sin cantidades negativas |
| horaRetiroDesde | string | hora_retiro_desde time | Definir fecha o vigencia del pack |
| horaRetiroHasta | string | hora_retiro_hasta time | Definir retiros que crucen medianoche |

El DTO no contiene imagenUri, propietario, fecha de publicación, vencimiento
ni estado de pausa. Esos datos no se pueden recuperar de esta respuesta.

## Datos locales fuera de la API

Room: rescatapp.db, versión 3. No se extrajo ninguna base de un dispositivo.

| Tabla | Campos actuales | Destino propuesto |
| --- | --- | --- |
| ofertas | id, nombre, comercio, categoria, descripcion, pesoKg, precioNormal, precioRescate, cantidadDisponible, horaRetiroDesde, horaRetiroHasta, imagenUri | Ofertas remotas; Room como caché |
| pedidos | id, ofertaId, nombreOferta, comercio, categoria, precioPagado, ahorro, pesoKg, horaRetiroDesde, horaRetiroHasta, estado, imagenUri, fecha | Pedidos con cliente y negocio identificados |
| notificaciones | id, titulo, mensaje, fechaHora, leida, tipo, rolDestino, pedidoId, ofertaId | Avisos por destinatario, no solo por rol |
| usuarios | id, nombre, correo, contrasenaHash, rol, telefono, direccion, nombreNegocio, fechaCreacion | Supabase Auth y perfiles separados |
| sesion_activa | id, usuarioId, correo, nombre, rol, nombreNegocio, fechaInicio, token | Sesión Supabase Auth; no importar tokens simulados |

Otros datos:

- DatosDeEjemplo.kt contiene ofertas y pedidos iniciales. No equivale a exportar
  los datos actuales del teléfono, que pueden haberse editado o eliminado.
- UsuariosDeEjemplo.kt contiene las cuentas de demostración.
- UsuarioDemo mantiene el rol y datos del invitado.
- Las fotos elegidas son URI del dispositivo. Copiar una URI content://
  no sube el archivo ni permite verlo desde otro teléfono.
- PasarelaPagoSimulada no llama a un banco. Los pagos seguirán simulados.

Fuentes locales: core/data/local/*Entity.kt, RescatAppDatabase.kt,
ModuloBaseDatos.kt y los Repositorio*.kt de core/data.

## Modelo objetivo propuesto

| Recurso | Responsabilidad | Relación importante |
| --- | --- | --- |
| Supabase Auth | Registro, login y sesión | Identidad UUID de cada usuario |
| perfiles | Nombre, rol y contacto | id asociado a auth.users |
| negocios | Nombre público, ubicación y propietario | propietario_id |
| ofertas | Packs, precios, stock, foto y publicación | negocio_id |
| pedidos | Reserva, importes históricos, estado y retiro | cliente_id, negocio_id y oferta_id |
| notificaciones | Avisos y lectura | destinatario_id y referencias opcionales |
| Storage | Archivos de imágenes | Ruta vinculada a oferta o negocio |

Cambios necesarios para completar la migración:

1. Identificar al propietario de cada negocio y oferta.
2. Mantener un mapa de IDs Room/MockAPI hacia los nuevos IDs. El mismo número
   puede representar distintos registros según la fuente.
3. Revisar duplicados entre copias locales y remotas antes de importar.
   No unir registros solo por nombre ni sobrescribir por ID numérico.
4. Añadir pausa/reactivación como estado independiente de las existencias.
5. Asociar pedidos a clientes: PedidoEntity no guarda cliente_id. Los históricos
   sin propietario verificable necesitan una decisión explícita.
6. Conservar nombre, precio, ahorro y horario históricos de cada pedido,
   aunque la oferta se edite después.
7. Reservar/cancelar en una transacción del servidor, evitando sobreventa
   o devolver dos veces las unidades.
8. Cada negocio administra lo suyo; cada cliente accede a sus pedidos y avisos.
   El catálogo muestra ofertas publicadas y disponibles.
9. Migrar autenticación con un flujo compatible con Auth. No copiar
   contrasenaHash a una tabla pública ni reutilizar tokens locales.
10. Subir los archivos de fotos y definir sus permisos.
11. Mantener Room y Flow para caché/UI reactiva. Definir refresco o Realtime
    entre dispositivos: cambiar BASE_URL por sí solo no sincroniza las fuentes.

## Mapeo futuro de operaciones

Estas son propuestas; todavía no son endpoints implementados.

| Operación | Destino sugerido |
| --- | --- |
| GET ofertas | GET /rest/v1/ofertas con filtros según lector |
| POST ofertas | POST /rest/v1/ofertas |
| PUT ofertas/{id} | PATCH /rest/v1/ofertas?id=eq.<id> |
| DELETE ofertas/{id} | RPC eliminar_oferta, con borrado lógico para conservar historial |
| Pausar/reactivar | RPC cambiar_estado_oferta, independiente de las unidades |
| Reservar/cancelar | Funciones/RPC con validación y transacción |
| Login/registro/sesión | Supabase Auth |
| Fotografías | Supabase Storage |
| Avisos | Data API con RLS por destinatario |

Las tablas expuestas necesitan políticas RLS y permisos. Android utilizará
la URL del proyecto y una clave publicable; las claves secretas o service_role
no deben estar en Android ni en Git.

Referencias oficiales consultadas:

- https://supabase.com/docs/guides/database/overview
- https://supabase.com/docs/guides/database/tables
- https://supabase.com/docs/guides/database/secure-data
- https://supabase.com/docs/guides/getting-started/api-keys

## Captura historica

La captura inicial ya se incorporo al SQL de carga. El exportador usado durante
la migracion fue retirado junto con el cliente antiguo. Los identificadores de
origen se conservan para reconocer las ofertas y vincular sus fotografias.

## Pendiente para iniciar la integración

- Proyecto Supabase de destino y URL.
- Campos finales de perfiles, negocios, ofertas y pedidos.
- Correspondencia de negocios con cuentas propietarias.
- Decisión sobre conservar o sustituir los datos de demostración.
- Si se desean conservar cambios de dispositivos: exportaciones Room e imágenes,
  revisadas sin incluir tokens ni contraseñas.
- Clave publicable configurada localmente y acceso autorizado para ejecutar SQL.
  No hace falta compartir claves secretas en el chat.

El siguiente paso es cerrar este modelo, crear tablas/políticas y adaptar
repositorios, conservando las pantallas y estilos.
