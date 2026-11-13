# RescatApp

Aplicación Android para rescatar alimentos. Los comercios publican sus excedentes del día como **ofertas a precio reducido**; los clientes las exploran, las reservan y siguen su pedido hasta retirarlo. La app muestra el impacto acumulado: rescates, dinero ahorrado y kilos de comida aprovechados.

**Problema que resuelve:** cada día los comercios de comida (panaderías, restaurantes, cafeterías) desechan productos en buen estado que no alcanzaron a vender. RescatApp los conecta con personas que los compran más baratos antes del cierre, lo que reduce el desperdicio (ODS 12: producción y consumo responsables).

---

## Contenido

1. [Cómo ejecutar](#cómo-ejecutar)
2. [Tecnologías](#tecnologías)
3. [Pantallas](#pantallas)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Arquitectura](#arquitectura)
6. [Persistencia local con Room](#persistencia-local-con-room)
7. [Consumo de API REST con Retrofit y MockAPI](#consumo-de-api-rest-con-retrofit-y-mockapi)
8. [Dónde se cumple cada punto de la guía técnica](#dónde-se-cumple-cada-punto-de-la-guía-técnica)
9. [Guion de la demostración técnica (12 pasos)](#guion-de-la-demostración-técnica-12-pasos)
10. [Preguntas de la defensa técnica](#preguntas-de-la-defensa-técnica)
11. [Objetivos de Desarrollo Sostenible](#objetivos-de-desarrollo-sostenible)
12. [Equipo](#equipo)

---

## Cómo ejecutar

1. Abrir la carpeta del proyecto en Android Studio.
2. Esperar la sincronización de Gradle.
3. Elegir un emulador o dispositivo físico y presionar **Run**.

| Comando | Qué hace |
| --- | --- |
| `./gradlew :app:assembleDebug` | Compila la app en variante debug |
| `./gradlew :app:testDebugUnitTest` | Ejecuta las pruebas unitarias automatizadas |
| `./gradlew spotlessApply` | Aplica el formateo uniforme de código (Spotless + ktlint) |
| `git config core.hooksPath .githooks` | Activa el formateo automático pre-commit ([instrucciones](.githooks/README.md)) |

El [CI de GitHub](.github/workflows/android.yml) valida compilación, pruebas y estilo en cada push a `main`.

---

## Tecnologías

| Tecnología | Para qué se usa |
| --- | --- |
| **Kotlin** | Lenguaje principal del proyecto |
| **Jetpack Compose + Material 3** | Interfaz de usuario declarativa y diseño cohesivo |
| **Navigation Compose** | Navegación multipantalla con `NavController`, `NavHost` y rutas tipadas |
| **Hilt (Dagger)** | Inyección de dependencias para repositorios, DAOs, base de datos y ViewModels |
| **Room Database** | Persistencia local en SQLite con `@Entity`, `@Dao`, `TypeConverters` y flujos `Flow` |
| **Retrofit 2 + OkHttp + Gson** | Cliente HTTP para consumo de API REST sobre MockAPI con operaciones CRUD |
| **StateFlow + ViewModel** | Manejo de estado reactivo desacoplado del ciclo de vida de la UI |
| **JUnit + Coroutines Test** | Pruebas unitarias para reglas de negocio, persistencia y estado |
| **Spotless + ktlint** | Formato de código automatizado y uniforme |

Dependencias declaradas en [gradle/libs.versions.toml](gradle/libs.versions.toml) y [app/build.gradle.kts](app/build.gradle.kts).

---

## Pantallas

La barra inferior adapta sus pestañas según el rol activo:
* **Cliente:** Inicio, Explorar, Pedidos y Perfil.
* **Negocio:** Inicio, Ofertas (Mis ofertas), Pedidos y Perfil.
* **Detalle** y **Publicar oferta** ocultan la barra de navegación para enfocar la acción del usuario.

### Dos flujos integrados: Cliente y Negocio

La app implementa los flujos de cliente y negocio. Se alternan desde Perfil mediante el botón de cambio de rol de demostración.

| Flujo | Qué hace | Pantallas en Figma | En la app |
| --- | --- | --- | --- |
| **Cliente** | Explora ofertas con filtros dinámicos, reserva y sigue el estado de sus pedidos | `Inicio`, `Explorar`, `Pedidos`, `Historial`, `Perfil` | Implementado |
| **Negocio** | Publica ofertas, gestiona estados de pedidos, edita o elimina ofertas y consulta métricas | `Inicio - Negocio`, `Ofertas - Negocio`, `Crear Oferta - Negocio`, `Pedidos - Negocio` | Implementado, sin lector QR |

Consecuencias en el código:
- El botón **"Publicar oferta"** pertenece al flujo negocio y está oculto en el flujo cliente: [muestraBotonPublicar](app/src/main/java/com/rescatapp/navigation/DestinoPrincipal.kt#L21).
- Las **acciones sobre un pedido** (cancelar, marcar como preparando o listo, confirmar retiro) son del negocio: sus casos de uso existen y están probados.
- Los archivos del Figma están en `PANTALLAS FIGMA/`. La separación de roles implementada por Ariany tiene prioridad para los flujos.
- La pestaña Ofertas muestra las publicaciones del negocio de demostración, con controles para editar y eliminar ofertas.

### Inicio (dashboard)

| Qué | Dónde |
| --- | --- |
| Pantalla | [InicioScreen.kt](app/src/main/java/com/rescatapp/features/inicio/ui/InicioScreen.kt#L15) |
| Encabezado: ubicación, saludo, notificaciones y buscador | [EncabezadoInicio](app/src/main/java/com/rescatapp/features/inicio/ui/EncabezadoInicio.kt#L25) |
| Bloque "Tu impacto esta semana" | [BloqueImpacto](app/src/main/java/com/rescatapp/features/inicio/ui/SeccionesInicio.kt#L33) |
| Tarjetas "Explora por categoría" | [CategoriasInicio](app/src/main/java/com/rescatapp/features/inicio/ui/CategoriasInicio.kt#L28) |
| Secciones "Ofertas cerca de ti", "Se están agotando", "Lo mejor de hoy" y "Puede interesarte" | [SeccionOfertas](app/src/main/java/com/rescatapp/features/inicio/ui/SeccionesInicio.kt#L54) |
| Estado de la pantalla | [InicioViewModel.kt](app/src/main/java/com/rescatapp/features/inicio/ui/InicioViewModel.kt) y [InicioUiState.kt](app/src/main/java/com/rescatapp/features/inicio/ui/InicioUiState.kt) |
| Cálculo del impacto (`filter`, `count`, `sumOf`) | [CalcularImpactoUseCase.kt](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt#L9) |
| Selección de ofertas por sección | [SeleccionarOfertasInicioUseCase.kt](app/src/main/java/com/rescatapp/features/inicio/domain/SeleccionarOfertasInicioUseCase.kt#L9) |

### Explorar

| Qué | Dónde |
| --- | --- |
| Pantalla | [ExplorarScreen.kt](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarScreen.kt#L27) |
| Cuadrícula de ofertas | [CuadriculaOfertas](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarScreen.kt#L60) |
| Chips de filtro y orden, menú de categorías | [FiltrosExplorar.kt](app/src/main/java/com/rescatapp/features/explorar/ui/FiltrosExplorar.kt#L26) |
| Búsqueda y filtro por categoría con SQL en Room | [OfertaDao](app/src/main/java/com/rescatapp/core/data) y [FiltrarOfertasUseCase.kt](app/src/main/java/com/rescatapp/features/explorar/domain/FiltrarOfertasUseCase.kt#L9) |
| "N ofertas encontradas" (`count`) | [ExplorarUiState.kt](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarUiState.kt#L11) |
| Categoría recibida desde Inicio | [ExplorarViewModel.kt](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarViewModel.kt#L29) |

### Detalle

| Qué | Dónde |
| --- | --- |
| Pantalla | [DetalleScreen.kt](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L40) |
| Información de la oferta y botón Reservar | [InformacionOferta](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L64) |
| Lee el id de la ruta con `SavedStateHandle` | [DetalleViewModel.kt](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleViewModel.kt#L26) |
| Diálogo de confirmación de reserva | [DialogoReserva.kt](app/src/main/java/com/rescatapp/features/detalle/ui/DialogoReserva.kt#L22) |
| Descuenta una unidad del stock de la oferta | [ReservarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/detalle/domain/ReservarOfertaUseCase.kt#L12) |

### Publicar oferta (formulario)

| Qué | Dónde |
| --- | --- |
| Pantalla | [RegistroScreen.kt](app/src/main/java/com/rescatapp/features/registro/ui/RegistroScreen.kt#L30) |
| Campos: nombre, comercio, descripción, peso, precios, cantidad y horario | [FormularioOferta.kt](app/src/main/java/com/rescatapp/features/registro/ui/FormularioOferta.kt#L21) |
| Selector de foto (cámara o galería) | [SeccionFotoOferta.kt](app/src/main/java/com/rescatapp/features/registro/ui/SeccionFotoOferta.kt#L27) |
| Selector de categoría | [SelectorCategoriaOferta.kt](app/src/main/java/com/rescatapp/features/registro/ui/SelectorCategoriaOferta.kt#L21) |
| Validación de campos, números, horas y precio de rescate < normal | [ValidarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/registro/domain/ValidarOfertaUseCase.kt#L10) |
| Guarda en Room y emite nueva oferta | [RegistrarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/registro/domain/RegistrarOfertaUseCase.kt#L10) |
| Estado del formulario | [RegistroViewModel.kt](app/src/main/java/com/rescatapp/features/registro/ui/RegistroViewModel.kt) |

### Mis pedidos

| Qué | Dónde |
| --- | --- |
| Pantalla (pestañas "Activos" e "Historial") | [PedidosScreen.kt](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosScreen.kt#L31) |
| Tarjeta de pedido con comercio, total y estado | [TarjetaPedido.kt](app/src/main/java/com/rescatapp/core/designsystem/components/TarjetaPedido.kt#L29) |
| Barra de progreso (Reservado → Preparando → Listo → Recogido) | [BarraProgresoPedido.kt](app/src/main/java/com/rescatapp/core/designsystem/components/BarraProgresoPedido.kt#L24) |
| Selector de pestañas | [SelectorPestanasPedidos.kt](app/src/main/java/com/rescatapp/features/pedidos/ui/SelectorPestanasPedidos.kt#L15) |
| Diálogos para cambiar de estado o cancelar | [DialogosPedido.kt](app/src/main/java/com/rescatapp/features/pedidos/ui/DialogosPedido.kt#L15) |
| Avanzar estado (solo negocio) | [AvanzarPedidoUseCase.kt](app/src/main/java/com/rescatapp/features/pedidos/domain/AvanzarPedidoUseCase.kt#L8) |
| Cancelar reserva y devolver stock | [CancelarPedidoUseCase.kt](app/src/main/java/com/rescatapp/features/pedidos/domain/CancelarPedidoUseCase.kt#L8) |
| Estado y separación por pestañas | [PedidosViewModel.kt](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosViewModel.kt) |

### Perfil

| Qué | Dónde |
| --- | --- |
| Pantalla | [PerfilScreen.kt](app/src/main/java/com/rescatapp/features/perfil/ui/PerfilScreen.kt#L25) |
| Encabezado con avatar, nombre, nivel de rescatista y cambio de rol | [EncabezadoPerfil.kt](app/src/main/java/com/rescatapp/features/perfil/ui/EncabezadoPerfil.kt#L21) |
| Datos de la cuenta (correo, teléfono, dirección) | [InformacionCuenta.kt](app/src/main/java/com/rescatapp/features/perfil/ui/InformacionCuenta.kt#L18) |
| Impacto acumulado y ODS | [SeccionSostenibilidadODS.kt](app/src/main/java/com/rescatapp/features/perfil/ui/SeccionSostenibilidadODS.kt#L16) |
| Estado del perfil | [PerfilViewModel.kt](app/src/main/java/com/rescatapp/features/perfil/ui/PerfilViewModel.kt) |

### Componentes compartidos

| Componente | Dónde |
| --- | --- |
| Tarjeta de oferta (usada en Inicio y Explorar) | [TarjetaOferta.kt](app/src/main/java/com/rescatapp/core/designsystem/components/TarjetaOferta.kt#L31) |
| Tarjeta de pedido | [TarjetaPedido.kt](app/src/main/java/com/rescatapp/core/designsystem/components/TarjetaPedido.kt) |
| Campo de texto con mensaje de error | [CampoConError.kt](app/src/main/java/com/rescatapp/core/designsystem/components/CampoConError.kt#L12) |
| Fila de rescates, ahorro y kg (Inicio y Perfil) | [FilaImpacto.kt](app/src/main/java/com/rescatapp/core/designsystem/components/FilaImpacto.kt) |
| Todos los componentes | [core/designsystem/components/](app/src/main/java/com/rescatapp/core/designsystem/components) |
| Colores y tema | [Color.kt](app/src/main/java/com/rescatapp/core/designsystem/theme/Color.kt) · [RescatAppTheme.kt](app/src/main/java/com/rescatapp/core/designsystem/theme/RescatAppTheme.kt#L28) |

---

## Estructura del proyecto

El código reside en [app/src/main/java/com/rescatapp/](app/src/main/java/com/rescatapp) organizado por responsabilidades claras según la Sección 5 de la guía técnica:

```
com.rescatapp
├── MainActivity.kt                  Punto de entrada de la actividad principal
├── RescatAppApplication.kt          Clase Application anotada con @HiltAndroidApp
│
├── core/                            Módulos y utilidades compartidas
│   ├── data/
│   │   ├── local/                   Persistencia local con Room
│   │   │   ├── converters/          TypeConverters para enums y listas estructuradas
│   │   │   ├── dao/                 OfertaDao y PedidoDao con consultas SQL y Flow
│   │   │   ├── database/            RescatAppDatabase y ModuloBaseDatos (Hilt)
│   │   │   └── entities/            OfertaEntity y PedidoEntity con @PrimaryKey
│   │   ├── remote/                  Servicios remotos con Retrofit
│   │   │   ├── api/                 ApiService (GET, POST, PUT, DELETE)
│   │   │   ├── dto/                 OfertaDto y mapeadores hacia el dominio
│   │   │   └── network/             ModuloRed y configuración de BASE_URL (MockAPI)
│   │   └── repository/              Repositorios únicos (Single Source of Truth)
│   ├── domain/                      Casos de uso transversales (cálculo de impacto y roles)
│   ├── model/                       Modelos de dominio puros (Oferta, Pedido, Categoria, etc.)
│   ├── designsystem/                Tema accesible (colores, tipografía) y componentes visuales
│   └── util/                        Conversores seguros, formateadores y extensiones
│
├── features/                        Módulos funcionales de pantalla (MVVM)
│   ├── inicio/                      domain/ + ui/ (Dashboard de impacto y secciones)
│   ├── explorar/                    domain/ + ui/ (Búsqueda y filtro SQL dinámico)
│   ├── detalle/                     domain/ + ui/ (Información completa y reserva)
│   ├── registro/                    domain/ + ui/ (Formulario de publicación y validaciones)
│   ├── pedidos/                     domain/ + ui/ (Seguimiento, avance de estados y cancelación)
│   └── perfil/                      ui/ (Métricas del usuario y cambio de rol)
│
└── navigation/                      Configuración de rutas, NavHost y barra inferior
```

| Carpeta | Contiene | Enlace |
| --- | --- | --- |
| `core/model` | `Oferta`, `Pedido`, los enum y el resultado de las operaciones | [abrir](app/src/main/java/com/rescatapp/core/model) |
| `core/data` | Persistencia local Room, servicios remotos Retrofit y repositorios | [abrir](app/src/main/java/com/rescatapp/core/data) |
| `core/domain` | `CalcularImpactoUseCase`, `SeleccionarPedidosPorRol` | [abrir](app/src/main/java/com/rescatapp/core/domain) |
| `core/designsystem` | Tema y componentes visuales | [abrir](app/src/main/java/com/rescatapp/core/designsystem) |
| `core/util` | `Conversiones`, `Formateadores` | [abrir](app/src/main/java/com/rescatapp/core/util) |
| `features` | Una carpeta por pantalla con arquitectura MVVM | [abrir](app/src/main/java/com/rescatapp/features) |
| `navigation` | `Rutas`, `RescatAppNavHost`, barra inferior | [abrir](app/src/main/java/com/rescatapp/navigation) |
| Pruebas unitarias | Misma estructura que el código (25 archivos, 78 pruebas) | [abrir](app/src/test/java/com/rescatapp) |

---

## Arquitectura

### Capas y reglas de dependencia

```
Screen  ──►  ViewModel  ──►  UseCase  ──►  Repositorio
(dibuja)     (estado)        (regla)       (datos)
```

- `features/*/ui` usa `features/*/domain`, y ambos usan `core`.
- **Una feature nunca importa a otra.** Lo que se comparte está en `core` (por ejemplo, [CalcularImpactoUseCase](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt) lo usan Inicio y Perfil).
- **`core` no importa ninguna feature.**
- Solo [navigation/](app/src/main/java/com/rescatapp/navigation) conoce todas las pantallas.

### Sincronización entre dispositivos (Single Source of Truth)

La base de datos local (Room) es la única fuente de verdad para la interfaz mediante flujos reactivos (`Flow`), mientras que la capa remota (Retrofit) sincroniza con el backend en MockAPI:

```
Teléfono A (Negocio)               Internet               Teléfono B (Cliente)
┌────────────────────┐                                   ┌────────────────────┐
│ Pantallas          │                                   │ Pantallas          │
│   ▲ observa Flow   │                                   │   ▲ observa Flow   │
│ Room (local)       │                                   │ Room (local)       │
│   ▲ guarda         │                                   │   ▲ guarda         │
│ Retrofit ──────────┼────────► ┌───────────────┐ ◄──────┼───┤ Retrofit       │
└────────────────────┘          │ API (MockAPI) │        └────────────────────┘
                                │ información   │
                                │ común         │
                                └───────────────┘
```

1. **Publicación y persistencia:** Cuando el negocio publica una oferta en el Teléfono A, se registra localmente en Room y se envía a la API remota mediante `POST`.
2. **Sincronización remota:** El cliente en el Teléfono B consulta la API mediante `GET` para obtener las publicaciones más recientes y las guarda en su Room local.
3. **Capacidad sin conexión (Offline-first):** Una vez que los datos residen en Room, el cliente puede consultar las ofertas y pedidos aunque no disponga de conexión a internet.
4. **Reactividad automática:** La interfaz nunca lee directamente de la red; observa Room mediante `Flow`. Cualquier inserción, actualización o eliminación redibuja automáticamente las pantallas con `collectAsStateWithLifecycle()`.

### Flujo de una reserva (estado y recomposición)

| Paso | Qué pasa | Dónde |
| --- | --- | --- |
| 1 | El cliente toca "Reservar" | [DetalleScreen.kt](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L64) |
| 2 | El ViewModel pide la reserva | [DetalleViewModel.reservar()](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleViewModel.kt#L36) |
| 3 | El caso de uso valida que haya unidades | [ReservarOfertaUseCase](app/src/main/java/com/rescatapp/features/detalle/domain/ReservarOfertaUseCase.kt#L12) |
| 4 | Se descuenta una unidad de la oferta en Room | `cambiarUnidadesDisponibles()` en `RepositorioOfertas.kt` |
| 5 | Se crea el pedido en estado Reservado en SQLite | `crearPedido()` y `insertar()` en `RepositorioPedidos.kt` |
| 6 | Los `Flow` de los DAOs de Room emiten las listas actualizadas | `OfertaDao.observarTodas()` y `PedidoDao.observarTodos()` |
| 7 | Cada ViewModel combina los datos y crea un `UiState` nuevo | [InicioViewModel](app/src/main/java/com/rescatapp/features/inicio/ui/InicioViewModel.kt) · [PedidosViewModel](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosViewModel.kt) |
| 8 | Cada pantalla lo lee con `collectAsStateWithLifecycle()` y Compose redibuja solo lo que cambió | [DetalleScreen](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L41) · [InicioScreen](app/src/main/java/com/rescatapp/features/inicio/ui/InicioScreen.kt#L20) |

Resultado: Detalle, Explorar, Inicio, Pedidos y Perfil se actualizan solos de manera atómica.

### Decisiones técnicas

| Decisión | Por qué |
| --- | --- |
| **Hilt** para inyección de dependencias | Los ViewModels reciben repositorios y casos de uso sin crearlos a mano. Con `@Singleton`, todas las pantallas comparten la misma instancia de cada componente. |
| **Repositorios concretos con Single Source of Truth** | Cada entidad tiene su repositorio que centraliza el acceso a Room y coordina la sincronización con Retrofit. |
| **`Flow` y `StateFlow`** | Permiten propagar cambios automáticamente desde SQLite hacia la UI sin recargas manuales. |
| **Un caso de uso por regla** | Cada clase hace una sola cosa (principio de responsabilidad única) y se prueba de forma aislada. |
| **Formato con Spotless** | Todo el equipo escribe con el mismo estilo; el CI rechaza código sin formatear. |

### Cambios respecto al diseño de Figma

| Cambio | Motivo |
| --- | --- |
| **Detalle** no existe en el Figma | La guía técnica exige una pantalla de detalle; se construyó con los componentes del diseño. |
| **Publicar oferta** solo aparece en el flujo negocio | En Figma es `Crear Oferta - Negocio`; en la app cliente el botón está oculto ([DestinoPrincipal.kt](app/src/main/java/com/rescatapp/navigation/DestinoPrincipal.kt#L21)). |
| Íconos de Material en lugar de emojis y fotos | No hay fotos por producto; se usa un ícono por categoría. |
| Naranja más oscuro (`#C2410C`) para texto | El naranja original sobre blanco no alcanza el contraste mínimo de 4.5:1. |
| Perfil sin menú (pagos, ayuda, cerrar sesión…) | Sin backend esas opciones serían botones que no hacen nada. |
| Sin lector QR ni pantalla independiente de Stats | El negocio gestiona estados con botones y consulta métricas en Inicio. |
| El corazón de favoritos es solo visual; no hay calificaciones ni distancia | Un favorito real y la calificación requieren datos y acciones que la app no tiene. |

---

## Persistencia local con Room

La persistencia local garantiza que la información permanezca intacta tras cerrar forzosamente la aplicación o reiniciar el dispositivo (Prueba Técnica 8).

* **Entidades relacionales:**
  * `OfertaEntity`: almacena id, nombre, comercio, categoría, descripción, peso, precios, unidades disponibles, horarios de retiro y uri de imagen.
  * `PedidoEntity`: almacena id, ofertaId, nombreOferta, comercio, precioUnitario, estado, pasos de progreso, código de retiro y horario.
* **DAOs reactivos:**
  * `OfertaDao`: expone consultas con `Flow<List<OfertaEntity>>`, operaciones `suspend` (`insertar`, `actualizar`, `eliminar`) y la consulta con filtro dinámico `filtrar(categoria, texto)`.
  * `PedidoDao`: expone `observarTodos(): Flow<List<PedidoEntity>>` y operaciones `suspend` para registrar y avanzar pedidos.
* **Base de datos y precarga inicial:**
  * `RescatAppDatabase`: hereda de `RoomDatabase`. Incorpora un `RoomDatabase.Callback` que pre-puebla los datos iniciales de [DatosDeEjemplo.kt](app/src/main/java/com/rescatapp/core/data/DatosDeEjemplo.kt) en la primera instalación, garantizando que el docente vea información inmediata sin pantallas vacías.

---

## Consumo de API REST con Retrofit y MockAPI

El consumo de servicios remotos está configurado con Retrofit 2 y OkHttp contra un proyecto dedicado en **MockAPI.io**:

* **Permiso de red:** Declarado en [AndroidManifest.xml](app/src/main/AndroidManifest.xml) con `<uses-permission android:name="android.permission.INTERNET" />`.
* **Contrato ApiService:**
  * `@GET("ofertas") suspend fun getOfertas(): List<OfertaDto>`
  * `@POST("ofertas") suspend fun postOferta(@Body oferta: OfertaDto): OfertaDto`
  * `@PUT("ofertas/{id}") suspend fun putOferta(@Path("id") id: String, @Body oferta: OfertaDto): OfertaDto`
  * `@DELETE("ofertas/{id}") suspend fun deleteOferta(@Path("id") id: String): Response<Unit>`
* **Modelos DTO y desacoplamiento:** `OfertaDto` utiliza anotaciones `@SerializedName` de Gson y se transforma hacia/desde los modelos de dominio mediante mapeadores aislados.
* **Manejo de estados asíncronos:** La capa de presentación maneja `EstadoRemoto`:
  * **Cargando:** muestra un `CircularProgressIndicator` mientras la petición está en curso.
  * **Éxito:** presenta los datos remotos sincronizados.
  * **Error:** captura `IOException` o `HttpException` sin cerrar la aplicación y presenta un mensaje legible con botón de **"Reintentar"**.

---

## Dónde se cumple cada punto de la guía técnica

### §2. Producto técnico
* **Kotlin y Jetpack Compose:** Desarrollado 100% en Kotlin con Compose y Material 3.
* **Compilación y ejecución:** Compila en Android Studio, pasa la suite de pruebas unitarias y se valida en GitHub Actions.
* **Organización en paquetes:** Estructura modularizada por capas (`data/local`, `data/remote`, `repository`, `domain`, `viewmodel`, `ui`).

### §3.1. Aplicación multipantalla y navegación
* **NavController y NavHost:** Centralizados en [RescatAppNavHost.kt](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt).
* **Flujo sin bloqueos:** Rutas claras en [Rutas.kt](app/src/main/java/com/rescatapp/navigation/Rutas.kt) con avance (`navigate()`) y retroceso seguro (`popBackStack()`).
* **Pantallas principales:** Inicio (Dashboard), Registro (Formulario), Explorar (Listado filtrado), Detalle (Operaciones de registro), Pedidos y Perfil.

### §3.2. Arquitectura y organización
* **Separación de responsabilidades:** Las pantallas solo dibujan y emiten eventos; la lógica de estado reside en los ViewModels; el acceso a datos en Repositories.
* **Inyección de dependencias:** Hilt gestiona instancias `@Singleton` para la base de datos, clientes HTTP y repositorios.

### §3.3. Persistencia local con Room
* **Entity, DAO y Database:** Implementados en `core/data/local/` con tipos consistentes y TypeConverters.
* **Persistencia confirmada:** Los datos registrados, editados o eliminados sobreviven al cierre forzado y reinicio de la app.
* **Operaciones asíncronas:** Escritura implementada con funciones `suspend` y lectura con `Flow`.

### §3.4. Flujo reactivo con Flow y StateFlow
* **Observación de base de datos:** Los DAOs de Room emiten `Flow`.
* **ViewModel y UI:** Los ViewModels transforman los flujos a `StateFlow` y Compose los consume mediante `collectAsStateWithLifecycle()`, logrando actualización automática sin recargas manuales.

### §3.5. Consultas y filtros dinámicos
* **Filtro SQL dinámico:** `OfertaDao.filtrar(categoria, texto)` implementa una consulta SQL real con cláusula `WHERE`:
  ```sql
  SELECT * FROM ofertas 
  WHERE (:categoria IS NULL OR categoria = :categoria) 
    AND (nombre LIKE '%' || :texto || '%' OR comercio LIKE '%' || :texto || '%')
  ```
* **Recuperación completa:** Al seleccionar la categoría "Todas" y limpiar el texto, la consulta recupera la lista completa de registros.

### §3.6. Consumo de API REST con Retrofit
* **Conexión a MockAPI:** `BASE_URL` funcional con `ApiService`, DTOs de red y mapeadores de dominio.
* **Operaciones CRUD:** Soporte completo de operaciones `GET`, `POST`, `PUT` y `DELETE` remotas.
* **Permiso de Internet:** Declarado en el manifest de la aplicación.

### §3.7. Manejo de estados de carga y errores
* **Indicador de progreso:** `CircularProgressIndicator` visible mientras se consulta el backend.
* **Control de excepciones:** Bloques `try/catch` y `Result` capturan problemas de conexión o servidor sin crasheos.
* **Recuperación:** Tarjeta de error con botón "Reintentar" y diferenciación clara entre catálogo local y catálogo en línea.

### §3.8. Formularios y validaciones
* **Validación completa:** [ValidarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/registro/domain/ValidarOfertaUseCase.kt) valida campos vacíos, precio rescate menor al normal, cantidades enteras positivas y rango de horas (`HH:mm`, hora fin posterior a inicio).
* **Feedback inline:** Mensajes de error específicos bajo cada campo con `CampoConError`.

---

## Guion de la demostración técnica (12 pasos)

El siguiente guion permite comprobar en vivo los 12 pasos exigidos en la Sección 4 de la guía técnica:

| Paso | Acción en la app | Resultado esperado | Criterio de la guía |
| :---: | :--- | :--- | :--- |
| **1** | Abrir el proyecto en Android Studio y presionar **Run** | La aplicación compila, instala y abre en Inicio sin errores | Compilación e instalación |
| **2** | Recorrer la navegación: Inicio → Explorar → Pedidos → Perfil | La barra inferior resalta la pestaña activa; transición fluida sin bloqueos | Flujo multipantalla |
| **3** | Cambiar a rol negocio en Perfil, abrir "Publicar oferta" y colocar: Precio Normal: 40, Precio Rescate: 50, Hora fin: 08:00 (inicio: 10:00) | Se muestran mensajes de error inline y el botón no permite registrar | Validación de formulario |
| **4** | Corregir datos válidos (Pack Croissants · Panadería · 40 · 20 · 4 un. · 18:00 a 20:00) y pulsar "Publicar oferta" | La pantalla se cierra y la oferta aparece de primera en Explorar e Inicio | Reflejo en listado |
| **5** | Tocar la oferta recién creada en la lista | Abre la pantalla **Detalle** mostrando precio normal, precio de rescate, ahorro, peso y horario | Detalle de registro |
| **6** | Desde el flujo de negocio, editar la oferta cambiando el precio o stock | El cambio se guarda y se refleja inmediatamente en toda la app sin recargar | Actualización de registro |
| **7** | En la sección del negocio, pulsar "Eliminar oferta" y confirmar en el diálogo | La oferta desaparece de la base de datos local y de todas las pantallas | Operación DELETE con confirmación |
| **8** | Cerrar la aplicación desde la multitarea del emulador (Swipe-to-kill) y volver a abrirla | Las ofertas creadas o modificadas siguen presentes en Room | Persistencia local con Room |
| **9** | En Explorar, escribir "Pan" y seleccionar la categoría "Panadería" | La consulta SQL de Room filtra con `WHERE` dinámico; al limpiar texto y volver a "Todas" se restaura la lista | Filtro dinámico en SQLite |
| **10** | Ir a la pestaña "En línea" en Mis ofertas | Se muestra un indicador de carga mientras Retrofit ejecuta el `GET` sobre MockAPI y lista las ofertas remotas | Consulta GET remota |
| **11** | Pulsar "Publicar en línea" sobre una oferta local | Se ejecuta `POST` contra MockAPI y el registro aparece en el servidor remoto | Operaciones remotas adicionales |
| **12** | Desconectar el Wi-Fi / Datos en el emulador y pulsar "Reintentar" | Se captura el error de red, no hay crash y se muestra el banner de fallo con botón de reintento | Estados de carga y error |

---

## Preguntas de la defensa técnica

**1. ¿Cuál es la función de NavHost y NavController?**  
El `NavController` guarda en qué pantalla estamos y la pila de pantallas anteriores; es quien ejecuta la navegación. El `NavHost` es el contenedor que dibuja la pantalla que corresponde a la ruta actual, y en él se declara un `composable(ruta)` por pantalla.  
Código: [rememberNavController()](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L34) · [NavHost y sus composable](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L56).

**2. ¿Cuál es la diferencia entre navigate() y popBackStack()?**  
`navigate(ruta)` agrega una pantalla encima de la pila (por ejemplo, `navigate("detalle/14")`). `popBackStack()` quita la pantalla actual y regresa a la anterior en el historial de navegación sin generar pantallas duplicadas.  
Código: [navigate a Detalle](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L37) · [popBackStack](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L38).

**3. ¿Por qué separaron las pantallas en archivos diferentes?**  
Cada archivo tiene una sola responsabilidad: facilita la lectura, la realización de pruebas unitarias y evita conflictos al trabajar concurrentemente cinco desarrolladores en Git.

**4. ¿Cuál es el rol de Room y cómo se garantiza la persistencia?**  
Room es una capa de abstracción sobre SQLite. Los datos residen en el almacenamiento interno del dispositivo. Gracias a `@Dao` y funciones `suspend`, las operaciones de inserción y actualización se graban permanentemente en disco, de modo que si el sistema destruye el proceso, los datos continúan presentes al reiniciar la app.

**5. ¿Cómo funciona la arquitectura Single Source of Truth (SSOT)?**  
La base de datos local (Room) es la única fuente de verdad para la interfaz. Las pantallas observan flujos `Flow` provenientes del DAO. Cuando Retrofit descarga datos de MockAPI, estos se insertan en Room, y es Room quien automáticamente notifica a la UI mediante recomposición reactiva.

**6. ¿Cómo se implementa el filtro dinámico con WHERE en Room?**  
A través de una consulta SQL en `OfertaDao`:
```kotlin
@Query("SELECT * FROM ofertas WHERE (:categoria IS NULL OR categoria = :categoria) AND (nombre LIKE '%' || :texto || '%' OR comercio LIKE '%' || :texto || '%')")
fun filtrar(categoria: String?, texto: String): Flow<List<OfertaEntity>>
```
Si el parámetro de categoría es nulo, la condición se anula y SQLite devuelve todos los registros que coincidan con el texto.

**7. ¿Cómo se envían datos entre pantallas?**  
- **Argumentos de ruta**, solo con identificadores mínimos: [detalle/{ofertaId}](app/src/main/java/com/rescatapp/navigation/Rutas.kt#L19) y [explorar?categoria=POSTRES](app/src/main/java/com/rescatapp/navigation/Rutas.kt#L16). El ViewModel de destino los lee con `SavedStateHandle`.
- **Repositorios compartidos**: los datos completos residen en Room y los observan los ViewModels con flujos reactivos.

**8. ¿Cómo se manejan los errores de red en Retrofit sin colgar la app?**  
Las llamadas del `ApiService` se envuelven en bloques `try/catch` dentro del repositorio remoto capturando `IOException` (fallos de conexión) y `HttpException` (errores HTTP 4xx/5xx). El ViewModel traduce la excepción a un `EstadoRemoto.Error(mensaje)` observable por Compose, mostrando un mensaje comprensible y un botón de reintento.

**9. ¿Qué previene que se queden estados de carga colgados?**  
En el ViewModel, las corrutinas aseguran la desactivación de la bandera de carga en bloques estructurados o mediante el sellado del estado (`EstadoRemoto.Cargando` -> `EstadoRemoto.Exito` o `EstadoRemoto.Error`), garantizando que la UI siempre pase a un estado terminal accionable.

**10. ¿Qué validaciones realiza el formulario?**  
Campos obligatorios no vacíos, formato numérico positivo para precios y peso, precio de rescate estrictamente menor al precio normal, cantidad entera positiva y horarios en formato `HH:mm` con hora de fin posterior a la de inicio ([ValidarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/registro/domain/ValidarOfertaUseCase.kt#L10)).

---

## Objetivos de Desarrollo Sostenible

| ODS | Nivel | Implementación en RescatApp |
| --- | --- | --- |
| **12. Producción y consumo responsables** (Meta 12.3: reducir a la mitad el desperdicio de alimentos) | **Principal, con métricas en vivo** | Conecta excedentes con compradores antes de su descarte. El indicador **"Aprovechado"** calcula los kilos de comida rescatados del desperdicio. |
| **1. Fin de la pobreza** | Secundario, con indicador | El indicador **"Ahorrado"** suma el beneficio económico directo para las familias al acceder a alimentos con hasta un 50% de descuento. |

Las métricas de impacto se calculan de manera reactiva en [CalcularImpactoUseCase.kt](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt) a partir de los pedidos confirmados en Room:

| Indicador | ODS | Cómo se calcula | Valor inicial de ejemplo |
| --- | --- | --- | --- |
| **Rescates** | 12 | Cantidad de pedidos no cancelados (`count`) | 4 |
| **Aprovechado** | 12 | Suma de los kg de esos pedidos (`sumOf`) | 3.6 kg |
| **Ahorrado** | 1 | Suma de (precio normal − precio de rescate) de esos pedidos (`sumOf`) | Bs.102 |

Pruebas automáticas en el CI ([CalcularImpactoUseCaseTest.kt](app/src/test/java/com/rescatapp/core/domain/CalcularImpactoUseCaseTest.kt#L12)) comprueban que las cifras provienen de cálculos reales y no de valores fijos en pantalla.

---

## Equipo

| Integrante | GitHub |
| --- | --- |
| **Alejandro Ramirez** | [alejandroramirezucb](https://github.com/alejandroramirezucb) |
| **Dariana Pol Aramayo** | [D4ri4na](https://github.com/D4ri4na) |
| **Josue Balbontin** | [josue-balbontin](https://github.com/josue-balbontin) |
| **Fernando Terrazas** | [FernandoTerrazasLl](https://github.com/FernandoTerrazasLl) |
| **Ariany Lopez** | [arianylopez](https://github.com/arianylopez) |
