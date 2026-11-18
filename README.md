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
7. [Conexion REST con Supabase](#conexion-rest-con-supabase)
8. [Dónde se cumple cada punto de la guía técnica](#dónde-se-cumple-cada-punto-de-la-guía-técnica)
9. [Guion de la demostración técnica (12 pasos)](#guion-de-la-demostración-técnica-12-pasos)
10. [Preguntas de la defensa técnica](#preguntas-de-la-defensa-técnica)
11. [Objetivos de Desarrollo Sostenible](#objetivos-de-desarrollo-sostenible)
12. [Equipo](#equipo)

---

## Cómo ejecutar

1. Abrir la carpeta del proyecto en Android Studio.
2. Copiar `supabase.local.properties.example` a `supabase.local.properties` y completar la URL y la clave pública de Supabase. Sin ese archivo el proyecto compila igual, pero la app muestra el aviso de sin conexión.
3. Esperar la sincronización de Gradle.
4. Elegir un emulador o dispositivo físico y presionar **Run**.

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
| **Retrofit + Gson** | Conexión REST con Supabase: autenticación, tablas, funciones RPC y fotos |
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

La app implementa los flujos de cliente y negocio. El rol depende de la cuenta de Supabase con la que se inicia sesión; sin sesión se usa el flujo cliente.

| Flujo | Qué hace | Pantallas en Figma | En la app |
| --- | --- | --- | --- |
| **Cliente** | Explora ofertas con filtros dinámicos, reserva y sigue el estado de sus pedidos | `Inicio`, `Explorar`, `Pedidos`, `Historial`, `Perfil` | Implementado |
| **Negocio** | Publica ofertas, gestiona estados de pedidos, edita o elimina ofertas y consulta métricas | `Inicio - Negocio`, `Ofertas - Negocio`, `Crear Oferta - Negocio`, `Pedidos - Negocio` | Implementado, sin lector QR |

Consecuencias en el código:
- El botón **"Publicar oferta"** pertenece al flujo negocio y está oculto en el flujo cliente: [muestraBotonPublicar](app/src/main/java/com/rescatapp/navigation/DestinoPrincipal.kt#L24).
- Las **acciones sobre un pedido** (cancelar, marcar como preparando o listo, confirmar retiro) son del negocio: sus casos de uso existen y están probados.
- Los archivos del Figma están en `PANTALLAS FIGMA/`. La separación de roles implementada por Ariany tiene prioridad para los flujos.
- La pestaña Ofertas muestra las publicaciones del negocio con sesión iniciada, con controles para editar, pausar y eliminar.

### Inicio (dashboard)

| Qué | Dónde |
| --- | --- |
| Pantalla | [InicioScreen.kt](app/src/main/java/com/rescatapp/features/inicio/ui/InicioScreen.kt#L28) |
| Encabezado: ubicación, saludo, notificaciones y buscador | [EncabezadoInicio](app/src/main/java/com/rescatapp/features/inicio/ui/EncabezadoInicio.kt#L27) |
| Bloque "Tu impacto esta semana" | [BloqueImpacto](app/src/main/java/com/rescatapp/features/inicio/ui/ImpactoInicio.kt#L23) |
| Tarjetas "Explora por categoría" | [CategoriasInicio](app/src/main/java/com/rescatapp/features/inicio/ui/CategoriasInicio.kt#L27) |
| Secciones "Ofertas cerca de ti", "Se están agotando", "Lo mejor de hoy" y "Puede interesarte" | [SeccionOfertas](app/src/main/java/com/rescatapp/features/inicio/ui/SeccionesInicio.kt#L28) |
| Estado de la pantalla | [InicioViewModel.kt](app/src/main/java/com/rescatapp/features/inicio/ui/InicioViewModel.kt) y [InicioUiState.kt](app/src/main/java/com/rescatapp/features/inicio/ui/InicioUiState.kt) |
| Cálculo del impacto (`filter`, `count`, `sumOf`) | [CalcularImpactoUseCase.kt](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt#L7) |
| Selección de ofertas por sección | [SeleccionarOfertasInicioUseCase.kt](app/src/main/java/com/rescatapp/features/inicio/domain/SeleccionarOfertasInicioUseCase.kt#L8) |

### Explorar

| Qué | Dónde |
| --- | --- |
| Pantalla | [ExplorarScreen.kt](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarScreen.kt#L31) |
| Cuadrícula de ofertas | [ExplorarContenido](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarScreen.kt#L50) |
| Chips de filtro y orden, menú de categorías | [FiltrosExplorar.kt](app/src/main/java/com/rescatapp/features/explorar/ui/FiltrosExplorar.kt#L35) |
| Búsqueda y filtro por categoría con SQL en Room | [OfertaDao.filtrar](app/src/main/java/com/rescatapp/core/data/local/OfertaDao.kt#L30) · [RepositorioOfertas.filtrar](app/src/main/java/com/rescatapp/core/data/RepositorioOfertas.kt#L39) |
| "N ofertas encontradas" (`count`) | [ExplorarUiState.kt](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarUiState.kt#L6) |
| Categoría recibida desde Inicio | [ExplorarViewModel.kt](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarViewModel.kt#L24) |

### Detalle

| Qué | Dónde |
| --- | --- |
| Pantalla | [DetalleScreen.kt](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L19) |
| Información de la oferta y botón Reservar | [InformacionOferta.kt](app/src/main/java/com/rescatapp/features/detalle/ui/InformacionOferta.kt#L33) · [RetiroOferta.kt](app/src/main/java/com/rescatapp/features/detalle/ui/RetiroOferta.kt) |
| Lee el id de la ruta con `SavedStateHandle` | [DetalleViewModel.kt](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleViewModel.kt#L29) |
| Pasarela de pago simulada (QR Simple y Efectivo) | [PasarelaPagoDialog.kt](app/src/main/java/com/rescatapp/features/pago/ui/PasarelaPagoDialog.kt) · [PasarelaPagoSimulada.kt](app/src/main/java/com/rescatapp/features/pago/domain/PasarelaPagoSimulada.kt) |
| Renderizador vectorial de QR en Canvas | [CodigoQrCanvas.kt](app/src/main/java/com/rescatapp/features/pago/ui/components/CodigoQrCanvas.kt) |
| Pago con QR (cuenta regresiva y simulación) y resultado del pago | [ContenidoPagoQr.kt](app/src/main/java/com/rescatapp/features/pago/ui/ContenidoPagoQr.kt) · [ResultadoPago.kt](app/src/main/java/com/rescatapp/features/pago/ui/ResultadoPago.kt) |
| Estado de la pasarela (método, QR, comprobante y error) | [EstadoPasarela.kt](app/src/main/java/com/rescatapp/features/pago/model/EstadoPasarela.kt) |
| Reserva en el servidor y actualiza el stock | [ReservarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/detalle/domain/ReservarOfertaUseCase.kt) |

### Publicar oferta (formulario)

| Qué | Dónde |
| --- | --- |
| Pantalla | [RegistroScreen.kt](app/src/main/java/com/rescatapp/features/registro/ui/RegistroScreen.kt#L32) |
| Campos: nombre, comercio, descripción, peso, precios, cantidad y horario | [FormularioOferta.kt](app/src/main/java/com/rescatapp/features/registro/ui/FormularioOferta.kt#L11) |
| Selector de foto (cámara o galería) | [SeccionFotoOferta.kt](app/src/main/java/com/rescatapp/features/registro/ui/SeccionFotoOferta.kt#L38) |
| Selector de categoría | [SelectorCategoriaOferta.kt](app/src/main/java/com/rescatapp/features/registro/ui/SelectorCategoriaOferta.kt#L33) |
| Validación de campos, números, horas y precio de rescate < normal | [ValidarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/registro/domain/ValidarOfertaUseCase.kt#L7) |
| Envía la oferta a Supabase y actualiza la caché de Room | [RegistrarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/registro/domain/RegistrarOfertaUseCase.kt#L10) |
| Estado del formulario | [RegistroViewModel.kt](app/src/main/java/com/rescatapp/features/registro/ui/RegistroViewModel.kt) |

### Mis pedidos

| Qué | Dónde |
| --- | --- |
| Pantalla (pestañas "Activos" e "Historial") | [PedidosScreen.kt](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosScreen.kt#L31) |
| Tarjeta de pedido con comercio, total y estado | [TarjetaPedido.kt](app/src/main/java/com/rescatapp/core/designsystem/components/TarjetaPedido.kt#L30) |
| Barra de progreso (Reservado → Preparando → Listo → Recogido) | [BarraProgresoPedido.kt](app/src/main/java/com/rescatapp/core/designsystem/components/BarraProgresoPedido.kt#L31) |
| Selector de pestañas | [SelectorPestanasPedidos.kt](app/src/main/java/com/rescatapp/features/pedidos/ui/SelectorPestanasPedidos.kt#L23) |
| Diálogos para cambiar de estado o cancelar | [DialogosPedido.kt](app/src/main/java/com/rescatapp/features/pedidos/ui/DialogosPedido.kt) |
| Avanzar estado (solo negocio) | [AvanzarPedidoUseCase.kt](app/src/main/java/com/rescatapp/features/pedidos/domain/AvanzarPedidoUseCase.kt#L7) |
| Cancelar reserva y devolver stock | [CancelarPedidoUseCase.kt](app/src/main/java/com/rescatapp/features/pedidos/domain/CancelarPedidoUseCase.kt#L8) |
| Estado y separación por pestañas | [PedidosViewModel.kt](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosViewModel.kt) |

### Perfil

| Qué | Dónde |
| --- | --- |
| Pantalla | [PerfilScreen.kt](app/src/main/java/com/rescatapp/features/perfil/ui/PerfilScreen.kt#L25) |
| Encabezado con avatar y nombre | [EncabezadoPerfil.kt](app/src/main/java/com/rescatapp/features/perfil/ui/EncabezadoPerfil.kt#L21) |
| Datos de la cuenta (correo, teléfono, dirección) | [InformacionCuenta.kt](app/src/main/java/com/rescatapp/features/perfil/ui/InformacionCuenta.kt#L27) |
| Iniciar sesión, registrarse o cerrar sesión | [SesionPerfil.kt](app/src/main/java/com/rescatapp/features/perfil/ui/SesionPerfil.kt) |
| Impacto acumulado y ODS | [SeccionSostenibilidadODS.kt](app/src/main/java/com/rescatapp/features/perfil/ui/SeccionSostenibilidadODS.kt#L20) |
| Estado del perfil | [PerfilViewModel.kt](app/src/main/java/com/rescatapp/features/perfil/ui/PerfilViewModel.kt) |

### Autenticación y Registro de Usuarios

| Qué | Dónde |
| --- | --- |
| Pantalla de Inicio de Sesión | [LoginScreen.kt](app/src/main/java/com/rescatapp/features/auth/ui/login/LoginScreen.kt) · [LoginViewModel.kt](app/src/main/java/com/rescatapp/features/auth/ui/login/LoginViewModel.kt) |
| Pantalla de Registro (Cliente y Negocio) | [RegistroUsuarioScreen.kt](app/src/main/java/com/rescatapp/features/auth/ui/registro/RegistroUsuarioScreen.kt) · [RegistroUsuarioViewModel.kt](app/src/main/java/com/rescatapp/features/auth/ui/registro/RegistroUsuarioViewModel.kt) |
| Casos de uso de autenticación | [IniciarSesionUseCase.kt](app/src/main/java/com/rescatapp/features/auth/domain/IniciarSesionUseCase.kt) · [RegistrarUsuarioUseCase.kt](app/src/main/java/com/rescatapp/features/auth/domain/RegistrarUsuarioUseCase.kt) |
| Inicio de sesión, registro y renovación del token con Supabase | [RepositorioAutenticacion.kt](app/src/main/java/com/rescatapp/core/data/RepositorioAutenticacion.kt) |
| Sesión guardada en Room | [SesionEntity.kt](app/src/main/java/com/rescatapp/core/data/local/SesionEntity.kt) · [SesionDao.kt](app/src/main/java/com/rescatapp/core/data/local/SesionDao.kt) |
| Componentes comunes de Login y Registro | [ComponentesAuth.kt](app/src/main/java/com/rescatapp/features/auth/ui/ComponentesAuth.kt) |

Se usan cuentas reales de Supabase. Si el proyecto pide confirmar el correo, la app avisa que se revise el correo antes de iniciar sesión.

### Notificaciones

| Qué | Dónde |
| --- | --- |
| Pantalla con historial, marcar como leídas y limpiar | [NotificacionesScreen.kt](app/src/main/java/com/rescatapp/features/notificaciones/ui/NotificacionesScreen.kt) · [TarjetaNotificacion.kt](app/src/main/java/com/rescatapp/features/notificaciones/ui/TarjetaNotificacion.kt) |
| El servidor crea los avisos; al sincronizar, los nuevos se muestran como notificación del sistema | [SincronizarDatosUseCase.kt](app/src/main/java/com/rescatapp/core/domain/SincronizarDatosUseCase.kt) |
| Notificación del sistema Android (un canal) | [NotificadorSistemaAndroid.kt](app/src/main/java/com/rescatapp/core/notification/NotificadorSistemaAndroid.kt) |
| Guardado en Room y conteo de no leídas | [RepositorioNotificaciones.kt](app/src/main/java/com/rescatapp/core/data/RepositorioNotificaciones.kt) · [NotificacionDao.kt](app/src/main/java/com/rescatapp/core/data/local/NotificacionDao.kt) |
| Campana con conteo en Inicio | [BotonNotificaciones.kt](app/src/main/java/com/rescatapp/core/designsystem/components/BotonNotificaciones.kt) |

### Componentes compartidos

| Componente | Dónde |
| --- | --- |
| Tarjeta de oferta (usada en Inicio y Explorar) | [TarjetaOferta.kt](app/src/main/java/com/rescatapp/core/designsystem/components/TarjetaOferta.kt#L30) |
| Tarjeta de pedido | [TarjetaPedido.kt](app/src/main/java/com/rescatapp/core/designsystem/components/TarjetaPedido.kt) |
| Campo de texto con mensaje de error y botón para mostrar u ocultar la contraseña | [CampoConError.kt](app/src/main/java/com/rescatapp/core/designsystem/components/CampoConError.kt#L35) |
| Botones principal y secundario (misma altura y forma) | [BotonPrincipal.kt](app/src/main/java/com/rescatapp/core/designsystem/components/BotonPrincipal.kt#L21) · [BotonSecundario.kt](app/src/main/java/com/rescatapp/core/designsystem/components/BotonSecundario.kt#L18) |
| Aviso temporal abajo de la pantalla (Detalle, Pedidos, Notificaciones y Mis ofertas) | [AvisoTemporal.kt](app/src/main/java/com/rescatapp/core/designsystem/components/AvisoTemporal.kt) |
| Fila de rescates, ahorro y kg (Inicio y Perfil) | [FilaImpacto.kt](app/src/main/java/com/rescatapp/core/designsystem/components/FilaImpacto.kt) |
| Todos los componentes | [core/designsystem/components/](app/src/main/java/com/rescatapp/core/designsystem/components) |
| Colores y tema | [Color.kt](app/src/main/java/com/rescatapp/core/designsystem/theme/Color.kt) · [RescatAppTheme.kt](app/src/main/java/com/rescatapp/core/designsystem/theme/RescatAppTheme.kt#L45) |

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
│   │   ├── local/                   Caché Room de ofertas, pedidos, notificaciones y sesión
│   │   │   ├── RescatAppTypeConverters.kt
│   │   │   ├── PedidoDao.kt
│   │   │   ├── RescatAppDatabase.kt y ModuloBaseDatos.kt
│   │   │   └── Entidades y DAOs de cada tabla
│   │   ├── remote/                  Retrofit: SupabaseApi, DTOs, ModuloRed y subida de fotos
│   │   └── Repositorios: piden los datos a Supabase y los guardan en Room
│   ├── domain/                      Casos de uso transversales (impacto, roles y sincronización)
│   ├── model/                       Modelos de dominio puros (Oferta, Pedido, Notificacion, etc.)
│   ├── notification/                Notificador del sistema y permiso de notificaciones
│   ├── designsystem/                Tema accesible (colores, tipografía) y componentes visuales
│   └── util/                        Conversores seguros, formateadores y extensiones
│
├── features/                        Módulos funcionales de pantalla (MVVM)
│   ├── inicio/                      domain/ + ui/ (Dashboard de impacto y secciones)
│   ├── explorar/                    domain/ + ui/ (Búsqueda y filtro SQL)
│   ├── detalle/                     domain/ + ui/ (Información completa y reserva)
│   ├── registro/                    domain/ + ui/ (Formulario de publicación y validaciones)
│   ├── pedidos/                     domain/ + ui/ (Seguimiento, avance de estados y cancelación)
│   ├── perfil/                      ui/ (Métricas del usuario y sesión)
│   ├── auth/                        domain/ + ui/ (Inicio de sesión y registro de usuarios)
│   ├── notificaciones/              ui/ (Historial de notificaciones)
│   └── pago/                        domain/ + model/ + ui/ (Pasarela de pago simulada)
│
└── navigation/                      Configuración de rutas, NavHost y barra inferior
```

| Carpeta | Contiene | Enlace |
| --- | --- | --- |
| `core/model` | `Oferta`, `Pedido`, los enum y el resultado de las operaciones | [abrir](app/src/main/java/com/rescatapp/core/model) |
| `core/data` | Repositorios, caché Room y conexión Retrofit con Supabase | [abrir](app/src/main/java/com/rescatapp/core/data) |
| `core/domain` | `CalcularImpactoUseCase`, `SeleccionarPedidosPorRol`, `SincronizarDatosUseCase` | [abrir](app/src/main/java/com/rescatapp/core/domain) |
| `core/designsystem` | Tema y componentes visuales | [abrir](app/src/main/java/com/rescatapp/core/designsystem) |
| `core/util` | `Conversiones`, `Formateadores` | [abrir](app/src/main/java/com/rescatapp/core/util) |
| `features` | Una carpeta por pantalla con arquitectura MVVM | [abrir](app/src/main/java/com/rescatapp/features) |
| `navigation` | `Rutas`, `RescatAppNavHost`, `GrafoNavegacion`, barra inferior | [abrir](app/src/main/java/com/rescatapp/navigation) |
| Pruebas automatizadas | Modelos, casos de uso, repositorios, ViewModels e interfaz | [abrir](app/src/test/java/com/rescatapp) |

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

### Supabase y caché en Room

Supabase es la fuente de verdad de ofertas, pedidos y notificaciones. Cada
repositorio pide los datos con Retrofit, los guarda en Room y las pantallas
observan Room con `Flow`. La app sincroniza al iniciar sesión, después de cada
operación y cada 10 segundos mientras está visible
([RescatAppNavHost.kt](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt) ·
[SincronizarDatosUseCase.kt](app/src/main/java/com/rescatapp/core/domain/SincronizarDatosUseCase.kt)).

### Flujo de una reserva (estado y recomposición)

| Paso | Qué pasa | Dónde |
| --- | --- | --- |
| 1 | El cliente toca "Reservar" y paga con QR o elige efectivo en la pasarela | [DetalleScreen.kt](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L19) |
| 2 | El ViewModel pide la reserva | [DetalleViewModel.reservar()](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleViewModel.kt#L94) |
| 3 | El caso de uso pide la reserva | [ReservarOfertaUseCase](app/src/main/java/com/rescatapp/features/detalle/domain/ReservarOfertaUseCase.kt) |
| 4 | El servidor valida el stock y crea el pedido (RPC `reservar_oferta`) | [RepositorioPedidos.reservar()](app/src/main/java/com/rescatapp/core/data/RepositorioPedidos.kt#L35) |
| 5 | Los repositorios vuelven a leer pedidos y ofertas y los guardan en Room | `sincronizar()` en `RepositorioPedidos.kt` y `RepositorioOfertas.kt` |
| 6 | Room emite las listas actualizadas | `RepositorioOfertas.ofertas` y `RepositorioPedidos.pedidos` |
| 7 | Cada ViewModel combina los datos y crea un `UiState` nuevo | [InicioViewModel](app/src/main/java/com/rescatapp/features/inicio/ui/InicioViewModel.kt) · [PedidosViewModel](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosViewModel.kt) |
| 8 | Cada pantalla lo lee con `collectAsStateWithLifecycle()` y Compose redibuja solo lo que cambió | [DetalleScreen](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L19) · [InicioScreen](app/src/main/java/com/rescatapp/features/inicio/ui/InicioScreen.kt#L28) |

Resultado: Detalle, Explorar, Inicio, Pedidos y Perfil observan las listas y se actualizan automáticamente.

### Decisiones técnicas

| Decisión | Por qué |
| --- | --- |
| **Hilt** para inyección de dependencias | Los ViewModels reciben repositorios y casos de uso sin crearlos a mano. Con `@Singleton`, todas las pantallas comparten la misma instancia de cada componente. |
| **Supabase + Room** | Supabase guarda los datos compartidos y valida permisos (RLS); Room es la caché local que alimenta la UI con `Flow`. |
| **`Flow` y `StateFlow`** | Permiten propagar cambios automáticamente desde SQLite hacia la UI sin recargas manuales. |
| **Un caso de uso por regla** | Cada clase hace una sola cosa (principio de responsabilidad única) y se prueba de forma aislada. |
| **Formato con Spotless** | Todo el equipo escribe con el mismo estilo; el CI rechaza código sin formatear. |

### Cambios respecto al diseño de Figma

| Cambio | Motivo |
| --- | --- |
| **Detalle** no existe en el Figma | La guía técnica exige una pantalla de detalle; se construyó con los componentes del diseño. |
| **Publicar oferta** solo aparece en el flujo negocio | En Figma es `Crear Oferta - Negocio`; en la app cliente el botón está oculto ([DestinoPrincipal.kt](app/src/main/java/com/rescatapp/navigation/DestinoPrincipal.kt#L12)). |
| Íconos de Material en lugar de emojis y fotos | No hay fotos por producto; se usa un ícono por categoría. |
| Naranja más oscuro (`#C2410C`) para texto | El naranja original sobre blanco no alcanza el contraste mínimo de 4.5:1. |
| Perfil sin menú de pagos ni ayuda | Sin backend esas opciones serían botones que no hacen nada. |
| Sin lector QR ni pantalla independiente de Stats | El negocio gestiona estados con botones y consulta métricas en Inicio. |
| El corazón de favoritos es solo visual; no hay calificaciones ni distancia | Un favorito real y la calificación requieren datos y acciones que la app no tiene. |

---

## Persistencia local con Room

`rescatapp.db` guarda una copia de lo último que devolvió Supabase: ofertas,
pedidos, notificaciones y la sesión activa (token incluido). Al sincronizar, los
repositorios reemplazan los registros (`insertarTodas`) y borran los que ya no
existen en el servidor (`eliminarExcepto`). Al cerrar sesión se borra la sesión
y la siguiente sincronización vacía los pedidos y notificaciones. Como es una
caché, un cambio de versión recrea la base (`fallbackToDestructiveMigration`).

---

## Conexion REST con Supabase

La URL base y la clave publica se leen de `supabase.local.properties` o de las
variables de entorno equivalentes. El archivo local esta excluido de Git.

| Recurso | Ruta |
| --- | --- |
| Autenticacion y sesiones | `/auth/v1/...` |
| Ofertas, perfiles y negocios | `/rest/v1/ofertas`, `/rest/v1/perfiles`, `/rest/v1/negocios` |
| Pedidos y notificaciones | `/rest/v1/pedidos`, `/rest/v1/notificaciones` |
| Reservas, cancelaciones, estados y eliminacion | `/rest/v1/rpc/...` |
| Fotografias | `/storage/v1/...` |

[SupabaseApi.kt](app/src/main/java/com/rescatapp/core/data/remote/SupabaseApi.kt) declara las rutas con Retrofit
y Gson convierte las respuestas en DTOs (`OfertaDto`, `PedidoDto`, `NotificacionDto`).
[ModuloRed.kt](app/src/main/java/com/rescatapp/core/data/remote/ModuloRed.kt) agrega la clave pública en cada
solicitud y los repositorios envían el token del usuario. Los permisos RLS y las RPC
validan al usuario en el servidor.

El catalogo se actualiza despues de las escrituras y cada 10 segundos mientras
la app esta visible. Las operaciones de reserva y cancelacion modifican el stock
en el servidor. Eliminar una oferta conserva los pedidos anteriores.

La integracion anterior y su URL se retiraron. Los campos `origen=mockapi`,
`legacy_id` y los nombres `mockapi-*.jpg` identifican los datos ya importados;
no realizan conexiones al servicio anterior.

Configuracion y flujos de uso: [conexion-android.md](docs/supabase/conexion-android.md).

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
* **Repositorios integrados:** las ofertas y los pedidos se guardan en Room y sobreviven al reinicio del proceso.
* **Operaciones asíncronas:** Escritura implementada con funciones `suspend` y lectura con `Flow`.

### §3.4. Flujo reactivo con Flow y StateFlow
* **Observación de base de datos:** Los DAOs de Room emiten `Flow`.
* **ViewModel y UI:** Los ViewModels transforman los flujos a `StateFlow` y Compose los consume mediante `collectAsStateWithLifecycle()`, logrando actualización automática sin recargas manuales.

### §3.5. Consultas y filtros dinámicos
* **Filtro SQL:** `OfertaDao.filtrar(categoria, texto)` consulta Room y `ExplorarViewModel` aplica el orden elegido:
  ```sql
  SELECT * FROM ofertas
  WHERE estadoPublicacion = 'ACTIVA' AND cantidadDisponible > 0
    AND (:categoria IS NULL OR categoria = :categoria)
    AND (nombre LIKE '%' || :texto || '%' OR comercio LIKE '%' || :texto || '%')
  ```
* **Recuperación completa:** Al seleccionar la categoría "Todas" y limpiar el texto, la consulta recupera la lista completa de registros.

### §3.6. Consumo de API REST con Supabase
* **Conexión:** URL configurable, Retrofit y conversión con Gson.
* **Operaciones:** GET, POST, PATCH y DELETE segun el recurso; reservas y estados mediante RPC.
* **Permisos:** Internet en Android y RLS por usuario en el servidor.

### §3.7. Manejo de estados de carga y errores
* **Indicador de progreso:** `CircularProgressIndicator` visible mientras se consulta el backend.
* **Control de excepciones:** Bloques `try/catch` y `Result` capturan problemas de conexión o servidor sin crasheos.
* **Recuperación:** Banner de error con botón "Reintentar" bajo la barra de estado; la app sigue mostrando los últimos datos guardados en Room. Los errores de una acción aparecen como aviso temporal abajo de la pantalla.

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
| **3** | Iniciar sesión con una cuenta de negocio, abrir "Publicar oferta" y colocar: Precio Normal: 40, Precio Rescate: 50, Hora fin: 08:00 (inicio: 10:00) | Se muestran mensajes de error inline y el botón no permite registrar | Validación de formulario |
| **4** | Corregir datos válidos (Pack Croissants · Panadería · 40 · 20 · 4 un. · 18:00 a 20:00) y pulsar "Publicar oferta" | La pantalla se cierra y la oferta aparece de primera en Explorar e Inicio | Reflejo en listado |
| **5** | Tocar la oferta recién creada en la lista | Abre la pantalla **Detalle** mostrando precio normal, precio de rescate, ahorro, peso y horario | Detalle de registro |
| **6** | Desde el flujo de negocio, editar la oferta cambiando el precio o stock | El cambio se guarda y se refleja inmediatamente en toda la app sin recargar | Actualización de registro |
| **7** | Eliminar una oferta propia y confirmar | Se oculta del catalogo; el historial de pedidos se conserva |
| **8** | Cerrar y volver a abrir la app | Se restaura la sesion y se sincronizan los datos de esa cuenta |
| **9** | En Explorar, escribir "Pan" y seleccionar "Panadería" | Se filtra mediante SQL; al limpiar texto y volver a "Todas" se restaura la lista | Filtro SQL con Room |
| **10** | Consultar Mis ofertas | Se muestran las ofertas vinculadas al propietario autenticado |
| **11** | Crear una oferta | Se guarda directamente en Supabase y aparece en el catalogo compartido |
| **12** | Desconectar el Wi-Fi / Datos en el emulador y pulsar "Reintentar" | Se captura el error de red, no hay crash y se muestra el banner de fallo con botón de reintento | Estados de carga y error |

---

## Preguntas de la defensa técnica

**1. ¿Cuál es la función de NavHost y NavController?**\
El `NavController` guarda en qué pantalla estamos y la pila de pantallas anteriores; es quien ejecuta la navegación. El `NavHost` es el contenedor que dibuja la pantalla que corresponde a la ruta actual, y en él se declara un `composable(ruta)` por pantalla.\
Código: [rememberNavController()](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L43) · [NavHost](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L77) · [sus composable](app/src/main/java/com/rescatapp/navigation/GrafoNavegacion.kt).

**2. ¿Cuál es la diferencia entre navigate() y popBackStack()?**\
`navigate(ruta)` agrega una pantalla encima de la pila (por ejemplo, `navigate("detalle/14")`). `popBackStack()` quita la pantalla actual y regresa a la anterior en el historial de navegación sin generar pantallas duplicadas.\
Código: [navigate a Detalle](app/src/main/java/com/rescatapp/navigation/GrafoNavegacion.kt) · [popBackStack](app/src/main/java/com/rescatapp/navigation/GrafoNavegacion.kt).

**3. ¿Por qué separaron las pantallas en archivos diferentes?**\
Cada archivo tiene una sola responsabilidad: facilita la lectura, la realización de pruebas unitarias y evita conflictos al trabajar concurrentemente cinco desarrolladores en Git.

**4. ¿Cuál es el rol de Room y cómo se garantiza la persistencia?**\
Room es una capa de abstracción sobre SQLite. Los datos residen en el almacenamiento interno del dispositivo. Gracias a `@Dao` y funciones `suspend`, las operaciones de inserción y actualización se graban permanentemente en disco, de modo que si el sistema destruye el proceso, los datos continúan presentes al reiniciar la app.

**5. ¿Cómo funciona la arquitectura Single Source of Truth (SSOT)?**\
Supabase es la fuente de verdad. Los repositorios guardan sus respuestas en Room y las pantallas solo leen Room, así todas ven los mismos datos.

**6. ¿Cómo se implementa el filtro dinámico con WHERE en Room?**\
La consulta de `OfertaDao` filtra los registros locales por categoría y texto:
```kotlin
@Query("SELECT * FROM ofertas WHERE estadoPublicacion = 'ACTIVA' AND cantidadDisponible > 0 AND (:categoria IS NULL OR categoria = :categoria) AND (nombre LIKE '%' || :texto || '%' OR comercio LIKE '%' || :texto || '%')")
fun filtrar(categoria: String?, texto: String): Flow<List<OfertaEntity>>
```
Si el parámetro de categoría es nulo, la condición se anula y SQLite devuelve todos los registros que coincidan con el texto.

**7. ¿Cómo se envían datos entre pantallas?**\
- **Argumentos de ruta**, solo con identificadores mínimos: [detalle/{ofertaId}](app/src/main/java/com/rescatapp/navigation/Rutas.kt#L11) y [explorar?categoria=POSTRES](app/src/main/java/com/rescatapp/navigation/Rutas.kt#L8). El ViewModel de destino los lee con `SavedStateHandle`.
- **Repositorios compartidos**: los datos locales residen en Room y los observan los ViewModels con flujos reactivos.

**8. ¿Como se manejan los errores de red sin colgar la app?**\
[mensajeDeError()](app/src/main/java/com/rescatapp/core/data/remote/MensajeError.kt) traduce los errores de conexión o del servidor a un mensaje en español. Si falla la sincronización se muestra un banner con "Reintentar" y se conservan los datos de Room; si falla una operación, el ViewModel muestra el mensaje como aviso temporal abajo de la pantalla.

**9. ¿Qué previene que se queden estados de carga colgados?**\
Los ViewModels apagan la bandera de carga en un bloque `finally`, así la pantalla siempre termina en un estado con datos o con un mensaje de error.

**10. ¿Qué validaciones realiza el formulario?**\
Campos obligatorios no vacíos, formato numérico positivo para precios y peso, precio de rescate estrictamente menor al precio normal, cantidad entera positiva y horarios en formato `HH:mm` con hora de fin posterior a la de inicio ([ValidarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/registro/domain/ValidarOfertaUseCase.kt#L7)).

---

## Objetivos de Desarrollo Sostenible

| ODS | Nivel | Implementación en RescatApp |
| --- | --- | --- |
| **12. Producción y consumo responsables** (Meta 12.3: reducir a la mitad el desperdicio de alimentos) | **Principal, con métricas en vivo** | Conecta excedentes con compradores antes de su descarte. El indicador **"Aprovechado"** calcula los kilos de comida rescatados del desperdicio. |
| **1. Fin de la pobreza** | Secundario, con indicador | El indicador **"Ahorrado"** suma el beneficio económico directo para las familias al acceder a alimentos con hasta un 50% de descuento. |

Las métricas de impacto se calculan de manera reactiva en [CalcularImpactoUseCase.kt](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt) a partir de los pedidos persistidos en Room:

| Indicador | ODS | Cómo se calcula | Valor inicial de ejemplo |
| --- | --- | --- | --- |
| **Rescates** | 12 | Cantidad de pedidos no cancelados (`count`) | 4 |
| **Aprovechado** | 12 | Suma de los kg de esos pedidos (`sumOf`) | 3.6 kg |
| **Ahorrado** | 1 | Suma de (precio normal − precio de rescate) de esos pedidos (`sumOf`) | Bs.102 |

Pruebas automáticas en el CI ([CalcularImpactoUseCaseTest.kt](app/src/test/java/com/rescatapp/core/domain/CalcularImpactoUseCaseTest.kt#L8)) comprueban que las cifras provienen de cálculos reales y no de valores fijos en pantalla.

---

## Equipo

| Integrante | GitHub |
| --- | --- |
| **Alejandro Ramirez** | [alejandroramirezucb](https://github.com/alejandroramirezucb) |
| **Dariana Pol Aramayo** | [D4ri4na](https://github.com/D4ri4na) |
| **Josue Balbontin** | [josue-balbontin](https://github.com/josue-balbontin) |
| **Fernando Terrazas** | [FernandoTerrazasLl](https://github.com/FernandoTerrazasLl) |
| **Ariany Lopez** | [arianylopez](https://github.com/arianylopez) |
