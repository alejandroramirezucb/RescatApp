# RescatApp

Aplicación Android para rescatar excedentes de alimentos. Los negocios publican
comida que les sobra a precio reducido y los clientes la reservan y la recogen.
Es una demo académica: los pagos son simulados.

## Problema

Panaderías, restaurantes y mercados botan comida en buen estado al final del día.
Al mismo tiempo, muchas personas buscan comer bien gastando menos. RescatApp une a
ambos: el negocio recupera parte del costo y el cliente ahorra. Se relaciona con el
ODS 12, producción y consumo responsables.

## Objetivos

- Permitir que un negocio publique, edite, pause y elimine ofertas de comida.
- Permitir que un cliente busque ofertas por texto y categoría, las reserve y pague de forma simulada.
- Compartir los datos entre teléfonos con Supabase.
- Avisar al cliente y al negocio cuando cambia el estado de un pedido.
- Mostrar el impacto: dinero ahorrado y kilos de comida rescatados.

## Funcionalidades

| Usuario | Puede |
| --- | --- |
| Todos | Registrarse, iniciar sesión, ver notificaciones y su perfil |
| Cliente | Buscar ofertas y negocios, ver el detalle, juntar varias ofertas y cantidades en el carrito, reservarlas en una sola compra, pagar con QR simulado o en efectivo, cancelar antes de que la tienda acepte y seguir sus pedidos |
| Negocio | Crear ofertas con foto, editarlas, pausarlas, eliminarlas, ver el nombre y teléfono del comprador, preparar pedidos y confirmar la entrega con el código del cliente |

Lo que publica un negocio aparece en el teléfono de cualquier cliente porque los
datos se guardan en Supabase.

## Tecnologías

| Tecnología | Uso |
| --- | --- |
| Kotlin | Lenguaje |
| Jetpack Compose y Material 3 | Interfaz |
| Navigation Compose | Navegación entre pantallas |
| ViewModel, Flow y StateFlow | Estado de las pantallas |
| Hilt | Inyección de dependencias |
| Room | Base local (caché y sesión) |
| Retrofit y Gson | Llamadas REST y conversión JSON |
| Supabase | Auth, PostgreSQL, Storage y funciones del servidor |
| JUnit y Robolectric | Pruebas |
| Spotless y ktlint | Formato del código |
| Git y GitHub Actions | Control de versiones y verificación automática |

Las versiones están en [gradle/libs.versions.toml](gradle/libs.versions.toml).

## Arquitectura

La app usa MVVM. Los datos siempre llegan a la pantalla a través de Room:

```
Pantalla Compose ──acción──► ViewModel ──► Repositorio ──► Retrofit ──► Supabase
       ▲                                        │
       │ StateFlow                              ▼ guarda
   ViewModel ◄──────── Flow ◄──────────────── Room
```

1. La pantalla llama a una función del ViewModel.
2. El ViewModel llama al repositorio dentro de `try/catch/finally`.
3. El repositorio pide o envía los datos con Retrofit y guarda la respuesta en Room.
4. Room emite la lista nueva por `Flow`; el ViewModel la expone como `StateFlow`.
5. La pantalla la lee con `collectAsStateWithLifecycle()` y se redibuja.

| Paquete | Contenido |
| --- | --- |
| `core/model` | Modelos: oferta, pedido, usuario, notificación |
| `core/data` | Repositorios |
| `core/data/local` | Room: entidades, DAO y `RescatAppDatabase` |
| `core/data/remote` | Retrofit: `SupabaseApi`, DTO y `ModuloRed` |
| `core/domain` | Reglas compartidas, sincronización y cálculo de impacto |
| `core/designsystem` | Tema y componentes visuales |
| `features/*` | Una carpeta por funcionalidad con su pantalla, ViewModel y casos de uso |
| `navigation` | Rutas, `NavHost` y barra inferior |
| `supabase` | Scripts SQL del servidor ([detalle](supabase/README.md)) |

## Requisitos técnicos

| Requisito | Dónde se cumple |
| --- | --- |
| Room | Entidades `OfertaEntity`, `PedidoEntity`, `NotificacionEntity` y `SesionEntity`; sus DAO; base `RescatAppDatabase` versión 7 con migraciones que conservan los pedidos |
| Persistencia | Room guarda ofertas, pedidos y sesión; al reabrir la app se muestran sin esperar al servidor |
| CRUD | Crear oferta en `RegistroScreen`; leer en Explorar; editar, pausar y eliminar en Mis ofertas |
| Flow y StateFlow | Los DAO devuelven `Flow`; los ViewModels exponen `StateFlow` con `MutableStateFlow` o `stateIn` |
| Filtro dinámico | `OfertaDao.filtrar` por categoría y `ExplorarViewModel` por texto, negocio y tipo (todo, productos o negocios); cambia al escribir o elegir un filtro |
| Retrofit | `SupabaseApi` con GET, POST, PATCH y DELETE; `GsonConverterFactory`; DTO con `toDomain()` |
| Carga y errores | Indicadores de progreso, mensajes de error, `try/catch/finally` y aviso sin conexión con reintento |
| Navegación | `NavHost` en `RescatAppNavHost`; rutas en `Rutas`; `navigate()` y `popBackStack()` |
| Validaciones | Campos obligatorios, números válidos, fecha de retiro desde hoy y horas con `DatePickerDialog` y `TimePickerDialog`, hora fin mayor a hora inicio, precio de rescate menor al normal, correo y contraseña |
| Diseño adaptable | Columnas según el ancho; contenido centrado hasta 600 dp en tablets |

Filtro de Explorar:

```sql
SELECT * FROM ofertas
WHERE estadoPublicacion = 'ACTIVA' AND cantidadDisponible > 0
  AND (:categoria IS NULL OR categoria = :categoria)
  AND (nombre LIKE '%' || :texto || '%' OR comercio LIKE '%' || :texto || '%')
ORDER BY id DESC
```

## Pantallas

| Pantalla | Para qué sirve |
| --- | --- |
| Inicio | Cliente: ofertas destacadas. Negocio: pedidos recientes y ventas de la semana |
| Explorar | Buscar y filtrar ofertas y negocios; ver las ofertas de un negocio |
| Detalle | Ver una oferta y agregarla al carrito |
| Carrito | Cambiar cantidades, quitar productos, ver total y ahorro, pagar y reservar todo |
| Registro | Publicar una oferta nueva (negocio) |
| Mis ofertas | Editar, pausar o eliminar ofertas (negocio) |
| Pedidos | Seguir, cancelar o avanzar pedidos |
| Notificaciones | Avisos de reservas y cambios de estado |
| Perfil | Datos de la cuenta, impacto y cierre de sesión |
| Acceso requerido | Elegir entre iniciar sesión o registrarse al abrir una pantalla que necesita cuenta |
| Login y Registro de usuario | Entrar o crear una cuenta de cliente o negocio |

## Configuración

Requisitos: Android Studio, JDK 17 y un dispositivo con Android 10 (API 29) o superior.

1. Abrir el proyecto en Android Studio.
2. Copiar `supabase.local.properties.example` como `supabase.local.properties`.
3. Escribir la URL del proyecto y su clave pública, sin comillas:

```properties
SUPABASE_URL=https://IDENTIFICADOR.supabase.co
SUPABASE_PUBLISHABLE_KEY=sb_publishable_CLAVE_PUBLICA
```

4. Sincronizar Gradle y ejecutar la app.

Ese archivo no se sube a Git. Sin él la app compila, pero muestra el aviso de sin
conexión. Para preparar un proyecto Supabase nuevo ver [supabase/README.md](supabase/README.md).

## Comandos

| Comando (PowerShell) | Para qué |
| --- | --- |
| `.\gradlew.bat spotlessApply` | Formatear el código |
| `.\gradlew.bat spotlessCheck` | Revisar el formato |
| `.\gradlew.bat :app:testDebugUnitTest` | Ejecutar las pruebas |
| `.\gradlew.bat :app:assembleDebug` | Generar el APK |

GitHub Actions ([android.yml](.github/workflows/android.yml)) ejecuta los tres
últimos en cada push y pull request. El hook opcional
`git config core.hooksPath .githooks` formatea los archivos Kotlin antes de cada commit.

## Pruebas

Las pruebas unitarias están en `app/src/test` y cubren registro y validación de
ofertas, edición y eliminación, DAO de Room, filtros de Explorar, repositorios con
un `SupabaseApi` falso, login, registro de usuarios, carrito, reservas, pago simulado y su
anulación al cancelar, datos del comprador, migraciones de Room, avance de pedidos
y rutas de navegación.

## Limitaciones

- Los pagos no cobran dinero real.
- La app se actualiza cada 10 segundos solo cuando está abierta; no hay notificaciones push con la app cerrada.
- Crear, editar o reservar necesita internet.
- El carrito vive en memoria: si se cierra la app se vacía.
- Cada unidad del carrito genera su propio pedido, porque `reservar_oferta` reserva una unidad por llamada.
- No hay recuperación de contraseña desde la app.

## Mejoras futuras

- Notificaciones push con Firebase Cloud Messaging.
- Actualización en tiempo real con Supabase Realtime.
- Pagos reales con una pasarela bancaria.
- Mapa de negocios cercanos.

## Equipo

| Integrante | GitHub |
| --- | --- |
| **Alejandro Ramirez** | [alejandroramirezucb](https://github.com/alejandroramirezucb), [alejandroramirezvallejos](https://github.com/alejandroramirezvallejos) |
| **Dariana Pol Aramayo** | [D4ri4na](https://github.com/D4ri4na) |
| **Josue Balbontin** | [josue-balbontin](https://github.com/josue-balbontin) |
| **Fernando Terrazas** | [FernandoTerrazasLl](https://github.com/FernandoTerrazasLl) |
| **Ariany Lopez** | [arianylopez](https://github.com/arianylopez) |
