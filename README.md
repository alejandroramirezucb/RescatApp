# RescatApp

Aplicación Android para rescatar alimentos. Los comercios publican sus excedentes del día como **ofertas a precio reducido**; los clientes las exploran, las reservan y siguen su pedido hasta retirarlo. La app muestra el impacto acumulado: rescates, dinero ahorrado y kilos de comida aprovechados.

**Problema que resuelve:** cada día los comercios de comida (panaderías, restaurantes, cafeterías) desechan productos en buen estado que no alcanzaron a vender. RescatApp los conecta con personas que los compran más baratos antes del cierre, lo que reduce el desperdicio (ODS 12: producción y consumo responsables).

---

## Contenido

1. [Cómo ejecutar](#cómo-ejecutar)
2. [Tecnologías](#tecnologías)
3. [Pantallas: dónde está cada cosa](#pantallas-dónde-está-cada-cosa)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Arquitectura](#arquitectura)
6. [Por qué no usamos Room](#por-qué-no-usamos-room)
7. [Dónde se cumple cada punto de la rúbrica](#dónde-se-cumple-cada-punto-de-la-rúbrica)
8. [Guion de la demostración](#guion-de-la-demostración)
9. [Preguntas de la defensa técnica](#preguntas-de-la-defensa-técnica)
10. [Objetivos de Desarrollo Sostenible](#objetivos-de-desarrollo-sostenible)
11. [Equipo](#equipo)

---

## Cómo ejecutar

1. Abrir la carpeta del proyecto en Android Studio.
2. Esperar la sincronización de Gradle.
3. Elegir un emulador o dispositivo y presionar **Run**.

| Comando                               | Qué hace                                                                                  |
| ------------------------------------- | ----------------------------------------------------------------------------------------- |
| `./gradlew :app:assembleDebug`        | Compila la app                                                                            |
| `./gradlew :app:testDebugUnitTest`    | Ejecuta las pruebas unitarias                                                             |
| `./gradlew spotlessApply`             | Formatea el código (Spotless + ktlint)                                                    |
| `git config core.hooksPath .githooks` | Activa el formateo automático antes de cada commit ([instrucciones](.githooks/README.md)) |

El [CI de GitHub](.github/workflows/android.yml) verifica formato, pruebas y compilación en cada push a `main`.

## Tecnologías

| Tecnología                      | Para qué se usa                                                     |
| ------------------------------- | ------------------------------------------------------------------- |
| Kotlin                          | Lenguaje del proyecto                                               |
| Jetpack Compose + Material 3    | Interfaz de usuario                                                 |
| Navigation Compose              | Navegación entre pantallas                                          |
| Hilt                            | Inyección de dependencias (ViewModels, casos de uso y repositorios) |
| StateFlow + ViewModel           | Estado observable que sobrevive a cambios de configuración          |
| JUnit + kotlinx-coroutines-test | Pruebas unitarias                                                   |
| Spotless + ktlint               | Formato uniforme del código                                         |

Dependencias declaradas en [libs.versions.toml](gradle/libs.versions.toml) y [app/build.gradle.kts](app/build.gradle.kts).

---

## Pantallas

La barra inferior tiene 4 pestañas (Inicio, Explorar, Pedidos, Perfil). Detalle y Publicar oferta se abren encima y ocultan la barra.

### Inicio (dashboard)

| Qué                                                   | Dónde                                                                                                                                                                                 |
| ----------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Pantalla                                              | [InicioScreen.kt](app/src/main/java/com/rescatapp/features/inicio/ui/InicioScreen.kt#L23)                                                                                             |
| Encabezado: ubicación, saludo y buscador              | [EncabezadoInicio](app/src/main/java/com/rescatapp/features/inicio/ui/InicioScreen.kt#L60)                                                                                            |
| Bloque "Tu impacto esta semana"                       | [BloqueImpacto](app/src/main/java/com/rescatapp/features/inicio/ui/SeccionesInicio.kt#L38)                                                                                            |
| Chips "Explora por categoría"                         | [CategoriasInicio](app/src/main/java/com/rescatapp/features/inicio/ui/SeccionesInicio.kt#L59)                                                                                         |
| Secciones "Ofertas cerca de ti" y "Se están agotando" | [SeccionOfertas](app/src/main/java/com/rescatapp/features/inicio/ui/SeccionesInicio.kt#L91)                                                                                           |
| Estado de la pantalla                                 | [InicioViewModel.kt](app/src/main/java/com/rescatapp/features/inicio/ui/InicioViewModel.kt) y [InicioUiState.kt](app/src/main/java/com/rescatapp/features/inicio/ui/InicioUiState.kt) |
| Cálculo del impacto (`filter`, `count`, `sumOf`)      | [CalcularImpactoUseCase.kt](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt#L9)                                                                                 |
| Separar disponibles y las que se agotan               | [SeleccionarOfertasInicioUseCase.kt](app/src/main/java/com/rescatapp/features/inicio/domain/SeleccionarOfertasInicioUseCase.kt#L9)                                                    |

### Explorar

| Qué                                         | Dónde                                                                                                            |
| ------------------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| Pantalla                                    | [ExplorarScreen.kt](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarScreen.kt#L27)                  |
| Cuadrícula de ofertas                       | [CuadriculaOfertas](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarScreen.kt#L60)                  |
| Chips de filtro y orden, menú de categorías | [FiltrosExplorar.kt](app/src/main/java/com/rescatapp/features/explorar/ui/FiltrosExplorar.kt#L26)                |
| Búsqueda, filtro por categoría y orden      | [FiltrarOfertasUseCase.kt](app/src/main/java/com/rescatapp/features/explorar/domain/FiltrarOfertasUseCase.kt#L9) |
| "N ofertas encontradas" (`count`)           | [ExplorarUiState.kt](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarUiState.kt#L11)                |
| Categoría recibida desde Inicio             | [ExplorarViewModel.kt](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarViewModel.kt#L29)            |

### Detalle

| Qué                                                      | Dónde                                                                                                            |
| -------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| Pantalla                                                 | [DetalleScreen.kt](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L40)                     |
| Información de la oferta y botón Reservar                | [InformacionOferta](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L64)                    |
| Lee el id de la ruta con `SavedStateHandle`              | [DetalleViewModel.kt](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleViewModel.kt#L26)               |
| Acción de reservar                                       | [reservar()](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleViewModel.kt#L36)                        |
| Regla de reserva (descuenta una unidad y crea el pedido) | [ReservarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/detalle/domain/ReservarOfertaUseCase.kt#L12) |

### Publicar oferta (registro)

| Qué                                              | Dónde                                                                                                               |
| ------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------- |
| Pantalla                                         | [RegistroScreen.kt](app/src/main/java/com/rescatapp/features/registro/ui/RegistroScreen.kt)                         |
| Los 10 campos del formulario                     | [FormularioOferta.kt](app/src/main/java/com/rescatapp/features/registro/ui/FormularioOferta.kt#L21)                 |
| Validaciones                                     | [ValidarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/registro/domain/ValidarOfertaUseCase.kt#L10)     |
| Mensajes de error                                | [MensajesValidacion.kt](app/src/main/java/com/rescatapp/features/registro/domain/MensajesValidacion.kt)             |
| Validar y guardar al tocar "Publicar oferta"     | [publicar()](app/src/main/java/com/rescatapp/features/registro/ui/RegistroViewModel.kt#L30)                         |
| Borrar el error al corregir un campo             | [onCampoCambiado()](app/src/main/java/com/rescatapp/features/registro/ui/RegistroViewModel.kt#L23)                  |
| Convertir los textos en una `Oferta` y guardarla | [RegistrarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/registro/domain/RegistrarOfertaUseCase.kt#L19) |
| Volver atrás al terminar de guardar              | [LaunchedEffect](app/src/main/java/com/rescatapp/features/registro/ui/RegistroScreen.kt#L29)                        |

### Mis pedidos

| Qué                                              | Dónde                                                                                                                              |
| ------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------- |
| Pantalla con pestañas Activos / Historial        | [PedidosScreen.kt](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosScreen.kt#L36)                                       |
| Botones Cancelar, Marcar como…, Confirmar retiro | [AccionesPedido](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosScreen.kt#L102)                                        |
| Separar activos e historial (`filter`, `map`)    | [PedidosViewModel.kt](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosViewModel.kt#L61)                                 |
| Avanzar al siguiente estado                      | [AvanzarPedidoUseCase.kt](app/src/main/java/com/rescatapp/features/pedidos/domain/AvanzarPedidoUseCase.kt#L8)                      |
| Cancelar y devolver la unidad a la oferta        | [CancelarPedidoUseCase.kt](app/src/main/java/com/rescatapp/features/pedidos/domain/CancelarPedidoUseCase.kt#L13)                   |
| Pasos de la barra de progreso (`mapIndexed`)     | [ConstruirProgresoPedidoUseCase.kt](app/src/main/java/com/rescatapp/features/pedidos/domain/ConstruirProgresoPedidoUseCase.kt#L17) |

### Perfil

| Qué                                            | Dónde                                                                                       |
| ---------------------------------------------- | ------------------------------------------------------------------------------------------- |
| Pantalla                                       | [PerfilScreen.kt](app/src/main/java/com/rescatapp/features/perfil/ui/PerfilScreen.kt#L31)   |
| Encabezado naranja con avatar, nombre y correo | [EncabezadoPerfil](app/src/main/java/com/rescatapp/features/perfil/ui/PerfilScreen.kt#L47)  |
| Indicadores (mismo cálculo que Inicio)         | [PerfilViewModel.kt](app/src/main/java/com/rescatapp/features/perfil/ui/PerfilViewModel.kt) |
| Usuario de demostración "Invitado"             | [UsuarioDemo.kt](app/src/main/java/com/rescatapp/core/model/UsuarioDemo.kt)                 |

### Componentes compartidos

| Qué                                             | Dónde                                                                                                                                                                             |
| ----------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Tarjeta de oferta (Inicio y Explorar)           | [TarjetaOferta.kt](app/src/main/java/com/rescatapp/core/designsystem/components/TarjetaOferta.kt#L21)                                                                             |
| Tarjeta de pedido                               | [TarjetaPedido.kt](app/src/main/java/com/rescatapp/core/designsystem/components/TarjetaPedido.kt)                                                                                 |
| Campo de texto con mensaje de error             | [CampoConError.kt](app/src/main/java/com/rescatapp/core/designsystem/components/CampoConError.kt#L12)                                                                             |
| Fila de rescates, ahorro y kg (Inicio y Perfil) | [FilaImpacto.kt](app/src/main/java/com/rescatapp/core/designsystem/components/FilaImpacto.kt)                                                                                     |
| Todos los componentes                           | [core/designsystem/components/](app/src/main/java/com/rescatapp/core/designsystem/components)                                                                                     |
| Colores y tema                                  | [Color.kt](app/src/main/java/com/rescatapp/core/designsystem/theme/Color.kt) · [RescatAppTheme.kt](app/src/main/java/com/rescatapp/core/designsystem/theme/RescatAppTheme.kt#L28) |

---

## Estructura del proyecto

Todo el código está en [app/src/main/java/com/rescatapp/](app/src/main/java/com/rescatapp):

```
com.rescatapp
├── MainActivity.kt
├── RescatAppApplication.kt         Clase Application con @HiltAndroidApp
│
├── core/                           Código compartido por todas las pantallas
│   ├── model/                      Modelo de datos
│   ├── data/                       Repositorios en memoria y datos de ejemplo
│   ├── domain/                     Cálculo del impacto (compartido por Inicio y Perfil)
│   ├── designsystem/               Tema (colores, tipografía) y componentes visuales
│   ├── navigation/                 Rutas
│   └── util/                       Conversión de texto a número/hora y formatos (Bs., kg, %)
│
├── features/                       Pantallas
│   ├── inicio/     domain/ + ui/
│   ├── explorar/   domain/ + ui/
│   ├── detalle/    domain/ + ui/
│   ├── registro/   domain/ + ui/
│   ├── pedidos/    domain/ + ui/
│   └── perfil/     ui/
│
└── navigation/                     Cómo se conectan las pantallas
```

| Carpeta             | Contiene                                                       | Enlace                                                     |
| ------------------- | -------------------------------------------------------------- | ---------------------------------------------------------- |
| `core/model`        | `Oferta`, `Pedido`, los enum y el resultado de las operaciones | [abrir](app/src/main/java/com/rescatapp/core/model)        |
| `core/data`         | `RepositorioOfertas`, `RepositorioPedidos`, `DatosDeEjemplo`   | [abrir](app/src/main/java/com/rescatapp/core/data)         |
| `core/domain`       | `CalcularImpactoUseCase`                                       | [abrir](app/src/main/java/com/rescatapp/core/domain)       |
| `core/designsystem` | Tema y componentes                                             | [abrir](app/src/main/java/com/rescatapp/core/designsystem) |
| `core/util`         | `Conversiones`, `Formateadores`                                | [abrir](app/src/main/java/com/rescatapp/core/util)         |
| `features`          | Una carpeta por pantalla                                       | [abrir](app/src/main/java/com/rescatapp/features)          |
| `navigation`        | `Rutas`, `RescatAppNavHost`, barra inferior                    | [abrir](app/src/main/java/com/rescatapp/navigation)        |
| Pruebas             | Misma estructura que el código (24 archivos)                   | [abrir](app/src/test/java/com/rescatapp)                   |

Dentro de cada feature:

| Carpeta   | Contiene                                                                                     | Ejemplo                                                                                                                                                                                                                                                                     |
| --------- | -------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `domain/` | Casos de uso: una regla de negocio por clase, sin código de interfaz                         | [ReservarOfertaUseCase](app/src/main/java/com/rescatapp/features/detalle/domain/ReservarOfertaUseCase.kt)                                                                                                                                                                   |
| `ui/`     | `Screen` (dibuja), `ViewModel` (maneja el estado) y `UiState` (datos que dibuja la pantalla) | [DetalleScreen](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt), [DetalleViewModel](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleViewModel.kt), [DetalleUiState](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleUiState.kt) |

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

### Flujo de una reserva (estado y recomposición)

| Paso | Qué pasa                                                                                       | Dónde                                                                                                                                                                                  |
| ---- | ---------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | El cliente toca "Reservar"                                                                     | [DetalleScreen.kt](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L64)                                                                                           |
| 2    | El ViewModel pide la reserva                                                                   | [DetalleViewModel.reservar()](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleViewModel.kt#L36)                                                                             |
| 3    | El caso de uso valida que haya unidades                                                        | [ReservarOfertaUseCase](app/src/main/java/com/rescatapp/features/detalle/domain/ReservarOfertaUseCase.kt#L12)                                                                          |
| 4    | Se descuenta una unidad de la oferta                                                           | [cambiarUnidadesDisponibles()](app/src/main/java/com/rescatapp/core/data/RepositorioOfertas.kt#L35)                                                                                    |
| 5    | Se crea el pedido en estado Reservado                                                          | [crearPedido()](app/src/main/java/com/rescatapp/core/model/Oferta.kt#L39)                                                                                                              |
| 6    | Los `StateFlow` de los repositorios emiten listas nuevas                                       | [RepositorioOfertas](app/src/main/java/com/rescatapp/core/data/RepositorioOfertas.kt#L19) · [RepositorioPedidos](app/src/main/java/com/rescatapp/core/data/RepositorioPedidos.kt#L17)  |
| 7    | Cada ViewModel combina los datos y crea un `UiState` nuevo                                     | [InicioViewModel](app/src/main/java/com/rescatapp/features/inicio/ui/InicioViewModel.kt) · [PedidosViewModel](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosViewModel.kt) |
| 8    | Cada pantalla lo lee con `collectAsStateWithLifecycle()` y Compose redibuja solo lo que cambió | [DetalleScreen](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L41) · [InicioScreen](app/src/main/java/com/rescatapp/features/inicio/ui/InicioScreen.kt#L28)     |

Resultado: Detalle, Explorar, Inicio, Pedidos y Perfil se actualizan solos.

### Decisiones técnicas

| Decisión                                                       | Por qué                                                                                                                                                                                                                                                                                                                                                               |
| -------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Hilt** para inyección de dependencias                        | Los ViewModels reciben repositorios y casos de uso sin crearlos a mano. Con [`@Singleton`](app/src/main/java/com/rescatapp/core/data/RepositorioOfertas.kt#L13), todas las pantallas comparten la misma instancia de cada repositorio y por eso ven los mismos datos. Se activa en [RescatAppApplication](app/src/main/java/com/rescatapp/RescatAppApplication.kt#L6) |
| **Dos repositorios concretos**, sin interfaz ni clase genérica | Cada tipo de dato (ofertas y pedidos) tiene su repositorio. Una interfaz no aporta nada mientras exista una sola implementación, y el código queda más simple de leer                                                                                                                                                                                                 |
| **`StateFlow`** en repositorios y ViewModels                   | Es un flujo observable: cuando cambia su valor, quien lo observa se entera y la pantalla se redibuja sola                                                                                                                                                                                                                                                             |
| **Un caso de uso por regla**                                   | Cada clase hace una sola cosa (principio de responsabilidad única) y se puede probar sola                                                                                                                                                                                                                                                                             |
| **Formato con Spotless**                                       | Todo el equipo escribe con el mismo estilo; el CI rechaza código sin formatear                                                                                                                                                                                                                                                                                        |

### Cambios respecto al diseño de Figma

| Cambio                                                    | Motivo                                                                        |
| --------------------------------------------------------- | ----------------------------------------------------------------------------- |
| Se agregaron **Publicar oferta** y **Detalle**            | El PDF exige un formulario y una pantalla de detalle, y el Figma no las tenía |
| Botón flotante **"Publicar oferta"** en Inicio y Explorar | El Figma no tiene acceso al formulario                                        |
| Íconos de Material en lugar de emojis y fotos             | No hay fotos por producto; se usa un ícono por categoría                      |
| Naranja más oscuro (`#C2410C`) para texto                 | El naranja original sobre blanco no alcanza el contraste mínimo de 4.5:1      |
| Perfil sin menú (pagos, ayuda, cerrar sesión…)            | Sin backend esas opciones serían botones que no hacen nada                    |
| Sin favoritos, calificaciones ni distancia                | Requieren datos que la app no tiene                                           |

---

## Por qué no usamos Room

Decidimos **no implementar Room** por alcance. El objetivo del primer bloque es demostrar navegación, formulario, validaciones y estado compartido, y todo eso funciona igual con datos en memoria.

| Pregunta                                   | Respuesta                                                                                                                                                                                                                                                                                  |
| ------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| ¿Dónde se guardan los datos?               | En memoria, dentro de [RepositorioOfertas](app/src/main/java/com/rescatapp/core/data/RepositorioOfertas.kt#L18) y [RepositorioPedidos](app/src/main/java/com/rescatapp/core/data/RepositorioPedidos.kt), cada uno con un `MutableStateFlow`                                                |
| ¿Por qué todas las pantallas ven lo mismo? | Hilt crea una sola instancia de cada repositorio ([`@Singleton`](app/src/main/java/com/rescatapp/core/data/RepositorioOfertas.kt#L13)) y la comparte                                                                                                                                       |
| ¿Qué pasa al cerrar la app?                | Los datos vuelven a los de ejemplo. Por eso el **paso 10 de la demostración no aplica**                                                                                                                                                                                                    |
| ¿Por qué hay datos al abrirla?             | Los repositorios empiezan con [DatosDeEjemplo.kt](app/src/main/java/com/rescatapp/core/data/DatosDeEjemplo.kt#L14): [14 ofertas](app/src/main/java/com/rescatapp/core/data/DatosDeEjemplo.kt#L14) y [5 pedidos](app/src/main/java/com/rescatapp/core/data/DatosDeEjemplo.kt#L73) del Figma |
| ¿Cómo se agregaría Room después?           | Solo cambiaría el interior de los dos repositorios: seguirían exponiendo `StateFlow`, así que pantallas, ViewModels y casos de uso no se tocarían                                                                                                                                          |

---

## Dónde se cumple cada punto de la rúbrica

### §2 Producto

| Requisito                            | Dónde                                                                           |
| ------------------------------------ | ------------------------------------------------------------------------------- |
| Kotlin y Jetpack Compose             | Todo el proyecto; configuración en [app/build.gradle.kts](app/build.gradle.kts) |
| Se ejecuta en emulador o dispositivo | Botón Run; el [CI](.github/workflows/android.yml) compila en cada push          |
| Organizado en archivos y paquetes    | [Estructura del proyecto](#estructura-del-proyecto)                             |
| Repositorio con evidencia de trabajo | `git shortlog -sn`, las issues de GitHub y la tabla de [Equipo](#equipo)        |

### §3.1 Aplicación multipantalla (mínimo 4)

| Pantalla pedida    | Nuestra pantalla                                                                                                                                                                |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Inicio / Dashboard | [InicioScreen.kt](app/src/main/java/com/rescatapp/features/inicio/ui/InicioScreen.kt)                                                                                           |
| Registro           | [RegistroScreen.kt](app/src/main/java/com/rescatapp/features/registro/ui/RegistroScreen.kt)                                                                                     |
| Listado            | [ExplorarScreen.kt](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarScreen.kt)                                                                                     |
| Detalle            | [DetalleScreen.kt](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt)                                                                                        |
| Adicionales        | [PedidosScreen.kt](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosScreen.kt), [PerfilScreen.kt](app/src/main/java/com/rescatapp/features/perfil/ui/PerfilScreen.kt) |

### §3.2 Navegación

| Requisito                      | Dónde                                                                                                                                                                                                                                                          |
| ------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| NavController                  | [rememberNavController()](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L34)                                                                                                                                                                  |
| NavHost                        | [NavHost](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L56), con destino inicial [Inicio](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L58)                                                                                |
| Una ruta por pantalla          | [Rutas.kt](app/src/main/java/com/rescatapp/navigation/Rutas.kt#L7)                                                                                                                                                                                             |
| `navigate()` para avanzar      | [ir a Detalle](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L37) · [ir a Registro](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L48) · [ir a Explorar](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L63) |
| `popBackStack()` para regresar | [volver](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L38), usado en [Detalle](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L82) y en [Registro](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L85)       |
| Flujo sin bloqueos             | [Barra inferior](app/src/main/java/com/rescatapp/navigation/BarraNavegacionInferior.kt#L27) y [cambio de pestaña](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L93)                                                                          |

### §3.3 Modelo de datos

| Requisito                                  | Dónde                                                                                                                                                                                                                                                                                                                    |
| ------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| data class principal                       | [Oferta](app/src/main/java/com/rescatapp/core/model/Oferta.kt#L9) (también [Pedido](app/src/main/java/com/rescatapp/core/model/Pedido.kt#L3))                                                                                                                                                                            |
| enum class                                 | [Categoria](app/src/main/java/com/rescatapp/core/model/Categoria.kt#L3), [EstadoPedido](app/src/main/java/com/rescatapp/core/model/EstadoPedido.kt#L3), [Disponibilidad](app/src/main/java/com/rescatapp/core/model/Disponibilidad.kt#L3), [OrdenOfertas](app/src/main/java/com/rescatapp/core/model/OrdenOfertas.kt#L3) |
| Estructura consistente desde el formulario | [RegistrarOfertaUseCase](app/src/main/java/com/rescatapp/features/registro/domain/RegistrarOfertaUseCase.kt#L19) crea la misma `Oferta` que los datos de ejemplo                                                                                                                                                         |
| Nombres descriptivos                       | En español y sin abreviaturas en todo el código                                                                                                                                                                                                                                                                          |

### §3.4 Formulario y validaciones

| Requisito                            | Dónde                                                                                                                                                                                                                                                   |
| ------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Formulario funcional                 | [FormularioOferta.kt](app/src/main/java/com/rescatapp/features/registro/ui/FormularioOferta.kt#L21)                                                                                                                                                     |
| Obligatorios no aceptan vacíos       | [ValidarOfertaUseCase](app/src/main/java/com/rescatapp/features/registro/domain/ValidarOfertaUseCase.kt#L10)                                                                                                                                            |
| Números validados antes de convertir | [aDecimalOrNull](app/src/main/java/com/rescatapp/core/util/Conversiones.kt#L11) · [aEnteroPositivoOrNull](app/src/main/java/com/rescatapp/core/util/Conversiones.kt#L16) · [aHoraOrNull](app/src/main/java/com/rescatapp/core/util/Conversiones.kt#L18) |
| Mensaje con datos inválidos          | [MensajesValidacion.kt](app/src/main/java/com/rescatapp/features/registro/domain/MensajesValidacion.kt), mostrados por [CampoConError](app/src/main/java/com/rescatapp/core/designsystem/components/CampoConError.kt#L12)                               |
| El registro aparece en el listado    | [agregar()](app/src/main/java/com/rescatapp/core/data/RepositorioOfertas.kt#L25) pone la oferta nueva primera; Explorar e Inicio la muestran solos                                                                                                      |

### §3.5 Datos y estado

| Requisito                         | Dónde                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Datos compartidos entre pantallas | [RepositorioOfertas](app/src/main/java/com/rescatapp/core/data/RepositorioOfertas.kt#L13) y [RepositorioPedidos](app/src/main/java/com/rescatapp/core/data/RepositorioPedidos.kt) (`@Singleton`)                                                                                                                                                                                                                                                        |
| La interfaz se actualiza sola     | `StateFlow` en cada ViewModel, leído con [collectAsStateWithLifecycle()](app/src/main/java/com/rescatapp/features/inicio/ui/InicioScreen.kt#L28)                                                                                                                                                                                                                                                                                                        |
| `filter`                          | [CalcularImpactoUseCase](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt#L9) · [FiltrarOfertasUseCase](app/src/main/java/com/rescatapp/features/explorar/domain/FiltrarOfertasUseCase.kt#L9) · [SeleccionarOfertasInicioUseCase](app/src/main/java/com/rescatapp/features/inicio/domain/SeleccionarOfertasInicioUseCase.kt#L12) · [PedidosViewModel](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosViewModel.kt#L61) |
| `count`                           | [rescates](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt#L11) · ["N ofertas encontradas"](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarUiState.kt#L11)                                                                                                                                                                                                                                                          |
| `map`                             | [PedidosViewModel](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosViewModel.kt#L62) · [ConstruirProgresoPedidoUseCase](app/src/main/java/com/rescatapp/features/pedidos/domain/ConstruirProgresoPedidoUseCase.kt#L17) (`mapIndexed`)                                                                                                                                                                                                        |
| `sumOf`                           | [ahorro y kg aprovechados](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt#L12)                                                                                                                                                                                                                                                                                                                                                   |

### §3.6 Persistencia

No aplica: ver [Por qué no usamos Room](#por-qué-no-usamos-room).

### §3.7 Interfaz

| Requisito                | Dónde                                                                                                                                                                                                               |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Presentación consistente | Mismo tema en todas las pantallas: [RescatAppTheme](app/src/main/java/com/rescatapp/core/designsystem/theme/RescatAppTheme.kt#L28), aplicado en [MainActivity](app/src/main/java/com/rescatapp/MainActivity.kt#L18) |
| Textos claros            | Botones con verbo: "Publicar oferta", "Reservar", "Confirmar retiro" ([AccionesPedido](app/src/main/java/com/rescatapp/features/pedidos/ui/PedidosScreen.kt#L102))                                                  |
| Contraste suficiente     | [Color.kt](app/src/main/java/com/rescatapp/core/designsystem/theme/Color.kt#L5): texto naranja oscuro, contraste de al menos 4.5:1                                                                                  |
| Navegación comprensible  | [Barra inferior](app/src/main/java/com/rescatapp/navigation/BarraNavegacionInferior.kt#L27) con la pestaña activa resaltada y botón atrás en Detalle y Registro                                                     |

---

## Guion de la demostración

| Paso | Acción                                                                                                                   | Qué se ve                                                                                      |
| ---- | ------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------- |
| 1    | Abrir el proyecto y presionar Run                                                                                        | La app abre en Inicio                                                                          |
| 2    | Explicar el problema                                                                                                     | Inicio: "Tu impacto esta semana" con **4 rescates · Bs.102 · 3.6kg**                           |
| 3    | Recorrer Inicio → Explorar → Pedidos → Perfil con la barra inferior                                                      | Cada pestaña se resalta en naranja                                                             |
| 4    | Tocar "Publicar oferta"                                                                                                  | Se abre el formulario                                                                          |
| 5    | Poner precio normal **40** y rescate **45**, hora hasta **25:00**, cantidad **2.5**                                      | "Debe ser menor al precio normal", "Usa el formato HH:mm", "Ingresa una cantidad válida"       |
| 6    | Llenar: Pack Salteñas · Panadería La Central · Panadería · Salteñas del día · peso **1,5** · 40 · 20 · 4 · 09:00 · 11:00 | La pantalla se cierra sola                                                                     |
| 7    | Ir a Explorar                                                                                                            | **15 ofertas encontradas**, con Pack Salteñas primera y "-50%"                                 |
| 8    | Tocar Pack Croissants                                                                                                    | Detalle con precio, ahorro, peso y horario                                                     |
| 9    | Tocar **Reservar** y volver a Inicio; en Explorar buscar "café", filtrar Postres y ordenar por mayor descuento           | Impacto **5 · Bs.117 · 4.2kg**; 3 resultados; 2 resultados; Pack Frutas primera                |
| 10   | Cerrar y reabrir                                                                                                         | **No aplica**: explicar [por qué no usamos Room](#por-qué-no-usamos-room)                      |
| 11   | Mostrar estructura y repositorio                                                                                         | [Estructura del proyecto](#estructura-del-proyecto), `git shortlog -sn` y las issues en GitHub |

---

## Preguntas de la defensa técnica

**1. ¿Cuál es la función de NavHost y NavController?**
El `NavController` guarda en qué pantalla estamos y la pila de pantallas anteriores; es quien ejecuta la navegación. El `NavHost` es el contenedor que dibuja la pantalla que corresponde a la ruta actual, y en él se declara un `composable(ruta)` por pantalla.
Código: [rememberNavController()](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L34) · [NavHost y sus composable](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L56).

**2. ¿Cuál es la diferencia entre navigate() y popBackStack()?**
`navigate(ruta)` **agrega** una pantalla encima de la pila; por ejemplo, tocar una oferta ejecuta `navigate("detalle/14")`. `popBackStack()` **quita** la pantalla actual y vuelve a la anterior; por ejemplo, la flecha atrás de Detalle o terminar de publicar una oferta.
Código: [navigate a Detalle](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L37) · [popBackStack](app/src/main/java/com/rescatapp/navigation/RescatAppNavHost.kt#L38).

**3. ¿Por qué separaron las pantallas en archivos diferentes?**
Cada archivo tiene una sola responsabilidad: es más fácil de leer, de probar y de encontrar. Además, cinco personas pueden trabajar a la vez sin editar el mismo archivo, lo que evita conflictos en Git. Ningún archivo pasa de 150 líneas.
Estructura: [features/](app/src/main/java/com/rescatapp/features), una carpeta por pantalla.

**4. ¿Qué representa la data class principal?**
`Oferta` representa el excedente que publica un comercio: nombre, comercio, categoría, peso, precio normal, precio de rescate, unidades disponibles y horario de retiro. También calcula el [porcentaje de descuento](app/src/main/java/com/rescatapp/core/model/Oferta.kt#L22) y la [disponibilidad](app/src/main/java/com/rescatapp/core/model/Oferta.kt#L31). Al ser `data class` tiene `copy()`, que usamos para crear una versión modificada sin alterar la original.
Código: [Oferta.kt](app/src/main/java/com/rescatapp/core/model/Oferta.kt#L9).

**5. ¿Qué significa que una lista o variable sea estado observable?**
Que cuando su valor cambia, avisa automáticamente a quien la está observando. Nuestras listas son `StateFlow`: al reservar, el repositorio emite una lista nueva y todas las pantallas que la observan se actualizan sin recargar nada a mano.
Código: [ofertas: StateFlow](app/src/main/java/com/rescatapp/core/data/RepositorioOfertas.kt#L19) y su lectura con [collectAsStateWithLifecycle()](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleScreen.kt#L41).

**6. ¿Qué ocurre durante una recomposición?**
Compose vuelve a ejecutar las funciones `@Composable` cuyo estado cambió para redibujar esa parte de la pantalla. Solo se recompone lo que depende del dato modificado, no toda la pantalla. Por ejemplo, al reservar cambia el número de rescates y se redibuja el [bloque de impacto](app/src/main/java/com/rescatapp/features/inicio/ui/SeccionesInicio.kt#L38).
Ver el [flujo de una reserva](#flujo-de-una-reserva-estado-y-recomposición).

**7. ¿Cómo se envía información desde una pantalla hacia otra?**
De dos formas:

- **Argumentos de ruta**, solo con identificadores: [detalle/{ofertaId}](app/src/main/java/com/rescatapp/navigation/Rutas.kt#L19) envía el id de la oferta y [explorar?categoria=POSTRES](app/src/main/java/com/rescatapp/navigation/Rutas.kt#L16) envía la categoría. El ViewModel de destino los lee con `SavedStateHandle` ([Detalle](app/src/main/java/com/rescatapp/features/detalle/ui/DetalleViewModel.kt#L26), [Explorar](app/src/main/java/com/rescatapp/features/explorar/ui/ExplorarViewModel.kt#L29)).
- **Repositorios compartidos**: los datos completos no viajan entre pantallas; cada pantalla los lee del mismo repositorio.

**8. ¿Qué validaciones realiza el formulario?**

| Campo                         | Regla                                                                                                                                   | Mensaje                                                           |
| ----------------------------- | --------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------- |
| Nombre, comercio, descripción | No vacíos                                                                                                                               | "Ingresa el nombre de la oferta", etc.                            |
| Categoría                     | Una elegida                                                                                                                             | "Selecciona una categoría"                                        |
| Peso, precios                 | Número mayor que 0; acepta coma o punto                                                                                                 | "Ingresa un peso válido en kg", etc.                              |
| Precio de rescate             | [Menor al precio normal](app/src/main/java/com/rescatapp/features/registro/domain/ValidarOfertaUseCase.kt#L35)                          | "Debe ser menor al precio normal"                                 |
| Cantidad                      | Entero mayor que 0                                                                                                                      | "Ingresa una cantidad válida"                                     |
| Horas                         | [Formato `HH:mm`; la final después de la inicial](app/src/main/java/com/rescatapp/features/registro/domain/ValidarOfertaUseCase.kt#L47) | "Usa el formato HH:mm" / "Debe ser posterior a la hora de inicio" |

Cada número se valida **antes** de convertirlo ([Conversiones.kt](app/src/main/java/com/rescatapp/core/util/Conversiones.kt)). Los errores aparecen bajo cada campo y se van corrigiendo mientras se escribe ([onCampoCambiado](app/src/main/java/com/rescatapp/features/registro/ui/RegistroViewModel.kt#L23)).
Código: [ValidarOfertaUseCase.kt](app/src/main/java/com/rescatapp/features/registro/domain/ValidarOfertaUseCase.kt#L10).

**9. ¿Dónde se almacenan los datos de la aplicación?**
En memoria, en [RepositorioOfertas](app/src/main/java/com/rescatapp/core/data/RepositorioOfertas.kt#L18) y [RepositorioPedidos](app/src/main/java/com/rescatapp/core/data/RepositorioPedidos.kt). Hilt crea una sola instancia de cada uno para toda la app. No usamos Room: ver [Por qué no usamos Room](#por-qué-no-usamos-room).

**10. ¿Qué aportó cada integrante del equipo?**
Ver la tabla de [Equipo](#equipo), con las issues de cada integrante y su código. Cada uno puede explicar el código de su parte.

---

## Objetivos de Desarrollo Sostenible

| ODS                                                                                        | Nivel                                | Qué hace la app                                                                                                                                                       |
| ------------------------------------------------------------------------------------------ | ------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **12. Producción y consumo responsables** (meta 12.3: reducir el desperdicio de alimentos) | **Principal, con indicador**         | Los comercios publican el excedente que iban a desechar y los clientes lo reservan. El indicador es **"Aprovechado"**: kilos de comida que dejaron de desperdiciarse  |
| **1. Fin de la pobreza**                                                                   | Secundario, con indicador aproximado | El indicador **"Ahorrado"** muestra cuánto dinero ahorra el cliente al comprar el excedente a precio reducido. Es un ahorro en el gasto en alimentos; no mide pobreza |
| **2. Hambre cero**                                                                         | Efecto esperado, **sin indicador**   | El alimento llega a más personas a menor precio. No medimos comidas donadas ni acceso alimentario                                                                     |
| **8. Trabajo decente y crecimiento económico**                                             | Efecto esperado, **sin indicador**   | Los comercios recuperan ingresos de lo que antes botaban. La app no calcula ingresos del comercio                                                                     |

### Cumplimiento

**Se ve en la app.** El bloque [Tu impacto esta semana](app/src/main/java/com/rescatapp/features/inicio/ui/SeccionesInicio.kt#L38) de Inicio y la fila de indicadores del [Perfil](app/src/main/java/com/rescatapp/features/perfil/ui/PerfilScreen.kt#L31) muestran tres cifras, que se dibujan con [FilaImpacto](app/src/main/java/com/rescatapp/core/designsystem/components/FilaImpacto.kt#L23):

| Indicador   | ODS | Cómo se calcula                                                                                                                                    | Valor con los datos de ejemplo |
| ----------- | --- | -------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------ |
| Rescates    | 12  | Cantidad de pedidos no cancelados ([`count`](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt#L11))                           | 4                              |
| Aprovechado | 12  | Suma de los kg de esos pedidos ([`sumOf`](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt#L13))                              | 3.6 kg                         |
| Ahorrado    | 1   | Suma de (precio normal − precio de rescate) de esos pedidos ([`sumOf`](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt#L12)) | Bs.102                         |

El cálculo está en un solo lugar: [CalcularImpactoUseCase.kt](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt#L9). Lo usan Inicio y Perfil, así que ambas pantallas siempre muestran las mismas cifras. Un pedido cancelado [no cuenta](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt#L9).

**Se comprueba con pruebas automáticas.** Corren en cada push del [CI](.github/workflows/android.yml):

| Prueba                                                                                                                                                                                                                                                      | Qué comprueba                                           | Por qué sirve como evidencia                                            |
| ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------- | ----------------------------------------------------------------------- |
| [`conLosPedidosDeEjemploIgnoraElCancelado`](app/src/test/java/com/rescatapp/core/domain/CalcularImpactoUseCaseTest.kt#L12)                                                                                                                                  | 4 rescates, Bs.102 y 3.6 kg                             | Las cifras de la demo salen del cálculo real y no están escritas a mano |
| [`cancelarUnPedidoLoRestaDelImpacto`](app/src/test/java/com/rescatapp/core/domain/CalcularImpactoUseCaseTest.kt#L21)                                                                                                                                        | Cancelar un pedido baja a 3, Bs.82 y 2.6 kg             | Solo cuenta lo que realmente se aprovecha                               |
| [`sinPedidosElImpactoEsCero`](app/src/test/java/com/rescatapp/core/domain/CalcularImpactoUseCaseTest.kt#L34)                                                                                                                                                | Sin pedidos todo vale 0                                 | No hay error ni valores inventados                                      |
| [`unaReservaActualizaImpactoYSeccionesSinRecargar`](app/src/test/java/com/rescatapp/features/inicio/ui/InicioViewModelTest.kt#L36)                                                                                                                          | Reservar sube el impacto a 5, Bs.117 y 4.2 kg           | El indicador refleja cada nueva reserva al instante                     |
| [`muestraElImpactoDeLosPedidosDeEjemplo`](app/src/test/java/com/rescatapp/features/perfil/ui/PerfilViewModelTest.kt#L20) y [`cancelarUnPedidoActualizaElImpactoSinRecargar`](app/src/test/java/com/rescatapp/features/perfil/ui/PerfilViewModelTest.kt#L29) | El Perfil muestra las mismas cifras y se actualiza solo | Inicio y Perfil coinciden                                               |

**En la demostración** (paso 9 del [guion](#guion-de-la-demostración)): se reserva Pack Croissants y el bloque pasa de 4 · Bs.102 · 3.6kg a **5 · Bs.117 · 4.2kg**.

### Limitaciones

- **Medimos lo reservado, no lo retirado.** "Aprovechado" suma todo pedido que no esté cancelado, incluso los que aún están en Reservado, Preparando o Listo. Lo estrictamente rescatado es lo que llega a Recogido.
- **Los kg son los declarados** por el comercio al publicar la oferta; no hay una confirmación del peso real recibido.
- **Los valores son de demostración.** Al no haber base de datos, las cifras arrancan con los pedidos de ejemplo del Figma.
- **ODS 2 y 8 no tienen indicador**, por eso no los presentamos como cumplidos.

---

## Equipo

Cada integrante trabajó las issues de su área. Las issues se ven en la [pestaña Issues](https://github.com/alejandroramirezucb/RescatApp/issues) del repositorio.

| Integrante          | GitHub                                                        | Áreas                             | Issues                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  | Código                                                                                                                                                                                                               |
| ------------------- | ------------------------------------------------------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Alejandro Ramirez   | [alejandroramirezucb](https://github.com/alejandroramirezucb) | `setup` · `navegacion` · `modelo` | [#31](https://github.com/alejandroramirezucb/RescatApp/issues/31) Proyecto base · [#33](https://github.com/alejandroramirezucb/RescatApp/issues/33) Navegación · [#34](https://github.com/alejandroramirezucb/RescatApp/issues/34) Barra inferior · [#35](https://github.com/alejandroramirezucb/RescatApp/issues/35) Modelo de oferta · [#36](https://github.com/alejandroramirezucb/RescatApp/issues/36) Modelo de pedido · [#37](https://github.com/alejandroramirezucb/RescatApp/issues/37) Conversión y formato                                    | [navigation/](app/src/main/java/com/rescatapp/navigation), [core/model/](app/src/main/java/com/rescatapp/core/model), [core/util/](app/src/main/java/com/rescatapp/core/util), [configuración](app/build.gradle.kts) |
| Dariana Pol Aramayo | [D4ri4na](https://github.com/D4ri4na)                         | `datos` · `perfil` · `pedidos`    | [#38](https://github.com/alejandroramirezucb/RescatApp/issues/38) Repositorios en memoria · [#39](https://github.com/alejandroramirezucb/RescatApp/issues/39) Datos de ejemplo · [#49](https://github.com/alejandroramirezucb/RescatApp/issues/49) Perfil · [#58](https://github.com/alejandroramirezucb/RescatApp/issues/58) Progreso de pedidos · [#59](https://github.com/alejandroramirezucb/RescatApp/issues/59) Cancelación e historial                                                                                                           | [core/data/](app/src/main/java/com/rescatapp/core/data), [features/pedidos/](app/src/main/java/com/rescatapp/features/pedidos), [features/perfil/](app/src/main/java/com/rescatapp/features/perfil)                  |
| Josue Balbontin     | [josue-balbontin](https://github.com/josue-balbontin)         | `registro` · `inicio`             | [#45](https://github.com/alejandroramirezucb/RescatApp/issues/45) Validación del formulario · [#46](https://github.com/alejandroramirezucb/RescatApp/issues/46) Registro válido · [#47](https://github.com/alejandroramirezucb/RescatApp/issues/47) Pantalla Publicar oferta · [#55](https://github.com/alejandroramirezucb/RescatApp/issues/55) Impacto en Inicio · [#56](https://github.com/alejandroramirezucb/RescatApp/issues/56) Secciones de ofertas · [#57](https://github.com/alejandroramirezucb/RescatApp/issues/57) Encabezado y categorías | [features/registro/](app/src/main/java/com/rescatapp/features/registro), [features/inicio/](app/src/main/java/com/rescatapp/features/inicio)                                                                         |
| Fernando Terrazas   | [FernandoTerrazasLl](https://github.com/FernandoTerrazasLl)   | `explorar` · `detalle`            | [#50](https://github.com/alejandroramirezucb/RescatApp/issues/50) Filtrado y orden · [#51](https://github.com/alejandroramirezucb/RescatApp/issues/51) Pantalla Explorar · [#52](https://github.com/alejandroramirezucb/RescatApp/issues/52) Reserva · [#53](https://github.com/alejandroramirezucb/RescatApp/issues/53) Pantalla Detalle · [#54](https://github.com/alejandroramirezucb/RescatApp/issues/54) Propagación del estado                                                                                                                    | [features/explorar/](app/src/main/java/com/rescatapp/features/explorar), [features/detalle/](app/src/main/java/com/rescatapp/features/detalle)                                                                       |
| Ariany Lopez        | [arianylopez](https://github.com/arianylopez)                 | `design-system` y todo el Figma   | [#40](https://github.com/alejandroramirezucb/RescatApp/issues/40) Tema visual · [#41](https://github.com/alejandroramirezucb/RescatApp/issues/41) Tarjeta de oferta · [#42](https://github.com/alejandroramirezucb/RescatApp/issues/42) Búsqueda, chips y formulario · [#43](https://github.com/alejandroramirezucb/RescatApp/issues/43) Componentes de pedidos y perfil                                                                                                                                                                                | Diseño de las 5 pantallas del Figma [core/designsystem/](app/src/main/java/com/rescatapp/core/designsystem)                                                                                                          |

### Qué área cubre cada issue

| Área            | Qué abarca                                               | Responsable |
| --------------- | -------------------------------------------------------- | ----------- |
| `setup`         | Proyecto base con Compose y Hilt                         | Alejandro   |
| `navegacion`    | NavHost, rutas y barra inferior                          | Alejandro   |
| `modelo`        | data classes, enums y utilidades de conversión y formato | Alejandro   |
| `datos`         | Repositorios en memoria y datos de ejemplo               | Dariana     |
| `perfil`        | Pantalla Perfil                                          | Dariana     |
| `pedidos`       | Progreso, cancelación e historial de pedidos             | Dariana     |
| `registro`      | Formulario de publicación y sus validaciones             | Josue       |
| `inicio`        | Impacto, secciones de ofertas, encabezado y categorías   | Josue       |
| `explorar`      | Búsqueda, filtros y listado                              | Fernando    |
| `detalle`       | Detalle, reserva y propagación del estado                | Fernando    |
| `design-system` | Tema y componentes visuales, a partir del Figma          | Ariany      |
