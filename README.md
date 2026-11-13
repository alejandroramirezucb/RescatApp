# RescatApp

Aplicación Android para rescatar alimentos. Los comercios publican sus excedentes del día como **ofertas a precio reducido**; los clientes las exploran, las reservan y siguen su pedido hasta retirarlo. La app muestra el impacto acumulado: rescates, dinero ahorrado y kilos de comida aprovechados.

**Problema que resuelve:** cada día los comercios de comida (panaderías, restaurantes, cafeterías) desechan productos en buen estado que no alcanzaron a vender. RescatApp los conecta con personas que los compran más baratos antes del cierre, lo que reduce el desperdicio (ODS 12: producción y consumo responsables).

---

## Contenido

1. [Cómo ejecutar](#cómo-ejecutar)
2. [Tecnologías](#tecnologías)
3. [Pantallas y flujos](#pantallas-y-flujos)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Arquitectura del sistema](#arquitectura-del-sistema)
6. [Persistencia local con Room](#persistencia-local-con-room)
7. [Consumo de API REST con Retrofit y MockAPI](#consumo-de-api-rest-con-retrofit-y-mockapi)
8. [Dónde se cumple cada punto de la guía técnica](#dónde-se-cumple-cada-punto-de-la-guía-técnica)
9. [Guion de la demostración técnica (12 pasos)](#guion-de-la-demostración-técnica-12-pasos)
10. [Preguntas de la defensa técnica](#preguntas-de-la-defensa-técnica)
11. [Objetivos de Desarrollo Sostenible](#objetivos-de-desarrollo-sostenible)
12. [Equipo e Issues del proyecto](#equipo-e-issues-del-proyecto)

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

## Pantallas y flujos

La barra inferior adapta sus pestañas según el rol activo:
* **Cliente:** Inicio, Explorar, Pedidos y Perfil.
* **Negocio:** Inicio, Mis ofertas, Pedidos y Perfil.
* **Detalle** y **Publicar oferta** ocultan la barra de navegación para enfocar la acción del usuario.

### Dos flujos integrados: Cliente y Negocio

La app cuenta con ambos flujos funcionales, alternables en cualquier momento desde **Perfil** mediante el selector de rol demostrativo.

| Flujo | Funcionalidad principal | Pantallas asociadas | Estado en la app |
| --- | --- | --- | --- |
| **Cliente** | Explora ofertas con filtros dinámicos, reserva y sigue el estado de sus pedidos | `Inicio`, `Explorar`, `Detalle`, `Pedidos`, `Perfil` | Funcional |
| **Negocio** | Publica ofertas, gestiona estados de pedidos, edita o elimina ofertas y consulta métricas | `Inicio`, `Ofertas (Mis ofertas)`, `Registro (Crear oferta)`, `Pedidos`, `Perfil` | Funcional |

---

## Estructura del proyecto

El proyecto organiza sus paquetes por responsabilidades claras según la Sección 5 de la guía técnica, manteniendo separados los orígenes de datos local y remoto:

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
│   ├── designsystem/                Tema accesible (colores, tipografía) y componentes reutilizables
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

---

## Arquitectura del sistema

La aplicación implementa el patrón **Single Source of Truth (Fuente Única de Verdad)** donde la base de datos local (Room) es la única fuente que alimenta a la interfaz mediante flujos reactivos (`Flow`), mientras que la capa remota (Retrofit) sincroniza con el backend (MockAPI).

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

### Principios del flujo de datos

1. **Publicación y persistencia:** Cuando el negocio publica una oferta en el Teléfono A, se registra localmente en Room y se envía a la API remota mediante `POST`.
2. **Sincronización remota:** El cliente en el Teléfono B consulta la API mediante `GET` para obtener las publicaciones más recientes y las guarda en su Room local.
3. **Capacidad sin conexión (Offline-first):** Una vez que los datos residen en Room, el cliente puede consultar las ofertas y pedidos aunque no disponga de conexión a internet.
4. **Reactividad automática:** La interfaz nunca lee directamente de la red; observa Room mediante `Flow`. Cualquier inserción, actualización o eliminación redibuja automáticamente las pantallas con `collectAsStateWithLifecycle()`.

---

## Persistencia local con Room

La persistencia local garantiza que la información permanezca intacta tras cerrar forzosamente la aplicación o reiniciar el emulador.

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

**1. ¿Cuál es el rol de Room y cómo se garantiza la persistencia?**  
Room es una capa de abstracción sobre SQLite. Los datos residen en el almacenamiento interno del dispositivo. Gracias a `@Dao` y funciones `suspend`, las operaciones de inserción y actualización se graban permanentemente en disco, de modo que si el sistema destruye el proceso, los datos continúan presentes al reiniciar la app.

**2. ¿Cómo funciona la arquitectura Single Source of Truth (SSOT)?**  
La base de datos local (Room) es la única fuente de verdad para la interfaz. Las pantallas observan flujos `Flow` provenientes del DAO. Cuando Retrofit descarga datos de MockAPI, estos se insertan en Room, y es Room quien automáticamente notifica a la UI mediante recomposición reactiva.

**3. ¿Cómo se implementa el filtro dinámico con WHERE en Room?**  
A través de una consulta SQL en `OfertaDao`:
```kotlin
@Query("SELECT * FROM ofertas WHERE (:categoria IS NULL OR categoria = :categoria) AND (nombre LIKE '%' || :texto || '%' OR comercio LIKE '%' || :texto || '%')")
fun filtrar(categoria: String?, texto: String): Flow<List<OfertaEntity>>
```
Si el parámetro de categoría es nulo, la condición se anula y SQLite devuelve todos los registros que coincidan con el texto.

**4. ¿Cómo se manejan los errores de red en Retrofit sin colgar la app?**  
Las llamadas del `ApiService` se envuelven en bloques `try/catch` dentro del repositorio remoto capturando `IOException` (fallos de conexión) y `HttpException` (errores HTTP 4xx/5xx). El ViewModel traduce la excepción a un `EstadoRemoto.Error(mensaje)` observable por Compose, mostrando un mensaje comprensible y un botón de reintento.

**5. ¿Qué previene que se queden estados de carga colgados?**  
En el ViewModel, las corrutinas aseguran la desactivación de la bandera de carga en bloques estructurados o mediante el sellado del estado (`EstadoRemoto.Cargando` -> `EstadoRemoto.Exito` o `EstadoRemoto.Error`), garantizando que la UI siempre pase a un estado terminal accionable.

---

## Objetivos de Desarrollo Sostenible

| ODS | Nivel | Implementación en RescatApp |
| --- | --- | --- |
| **12. Producción y consumo responsables** (Meta 12.3: reducir a la mitad el desperdicio de alimentos) | **Principal, con métricas en vivo** | Conecta excedentes con compradores antes de su descarte. El indicador **"Aprovechado"** calcula los kilos de comida rescatados del desperdicio. |
| **1. Fin de la pobreza** | Secundario, con indicador | El indicador **"Ahorrado"** suma el beneficio económico directo para las familias al acceder a alimentos con hasta un 50% de descuento. |

Las métricas de impacto se calculan de manera reactiva en [CalcularImpactoUseCase.kt](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt) a partir de los pedidos confirmados en Room.

---

## Equipo e Issues del proyecto

El equipo distribuyó las responsabilidades técnicas de la predefensa en 5 roles (Persona 1 a 5) con 2 issues por integrante, cubriendo los requisitos de persistencia local, consumo remoto, filtros SQL, validaciones, CRUD y métricas:

| Rol | Integrante | GitHub | Labels | Issues asignadas para la predefensa |
| :---: | --- | --- | --- | --- |
| **Persona 1** | **Alejandro Ramirez** | [alejandroramirezucb](https://github.com/alejandroramirezucb) | `retrofit` · `indicadores` | [#70](https://github.com/alejandroramirezucb/RescatApp/issues/70) Cliente Retrofit y CRUD remoto de ofertas<br>[#79](https://github.com/alejandroramirezucb/RescatApp/issues/79) Indicadores del negocio calculados con datos reales |
| **Persona 2** | **Josue Balbontin** | [josue-balbontin](https://github.com/josue-balbontin) | `room` · `pedidos` | [#74](https://github.com/alejandroramirezucb/RescatApp/issues/74) Entidad y DAO de pedidos<br>[#75](https://github.com/alejandroramirezucb/RescatApp/issues/75) Repositorio de pedidos sobre Room |
| **Persona 3** | **Dariana Pol Aramayo** | [D4ri4na](https://github.com/D4ri4na) | `room` | [#72](https://github.com/alejandroramirezucb/RescatApp/issues/72) Entidad y DAO de ofertas con filtro SQL<br>[#73](https://github.com/alejandroramirezucb/RescatApp/issues/73) Base de datos Room con datos iniciales |
| **Persona 4** | **Fernando Terrazas** | [FernandoTerrazasLl](https://github.com/FernandoTerrazasLl) | `validaciones` · `estado-remoto` | [#78](https://github.com/alejandroramirezucb/RescatApp/issues/78) Validaciones en la edición de ofertas<br>[#71](https://github.com/alejandroramirezucb/RescatApp/issues/71) Estados de carga y error para ofertas en línea |
| **Persona 5** | **Ariany Lopez** | [arianylopez](https://github.com/arianylopez) | `explorar` · `room` | [#76](https://github.com/alejandroramirezucb/RescatApp/issues/76) Repositorio de ofertas sobre Room y filtro SQL en Explorar<br>[#77](https://github.com/alejandroramirezucb/RescatApp/issues/77) Eliminar oferta del negocio |

### Historial de entregas anteriores (Base del prototipo)

| Integrante | Áreas históricas | Issues base completadas |
| --- | --- | --- |
| **Alejandro Ramirez** | `setup` · `navegacion` · `modelo` | [#31](https://github.com/alejandroramirezucb/RescatApp/issues/31) Proyecto base · [#33](https://github.com/alejandroramirezucb/RescatApp/issues/33) Navegación · [#34](https://github.com/alejandroramirezucb/RescatApp/issues/34) Barra inferior · [#35](https://github.com/alejandroramirezucb/RescatApp/issues/35) Modelo oferta · [#36](https://github.com/alejandroramirezucb/RescatApp/issues/36) Modelo pedido · [#37](https://github.com/alejandroramirezucb/RescatApp/issues/37) Conversión y formato |
| **Dariana Pol Aramayo** | `datos` · `perfil` · `pedidos` | [#38](https://github.com/alejandroramirezucb/RescatApp/issues/38) Repositorios en memoria · [#39](https://github.com/alejandroramirezucb/RescatApp/issues/39) Datos de ejemplo · [#49](https://github.com/alejandroramirezucb/RescatApp/issues/49) Perfil · [#58](https://github.com/alejandroramirezucb/RescatApp/issues/58) Progreso pedidos · [#59](https://github.com/alejandroramirezucb/RescatApp/issues/59) Cancelación e historial |
| **Josue Balbontin** | `registro` · `inicio` | [#45](https://github.com/alejandroramirezucb/RescatApp/issues/45) Validación formulario · [#46](https://github.com/alejandroramirezucb/RescatApp/issues/46) Registro válido · [#47](https://github.com/alejandroramirezucb/RescatApp/issues/47) Publicar oferta · [#55](https://github.com/alejandroramirezucb/RescatApp/issues/55) Impacto Inicio · [#56](https://github.com/alejandroramirezucb/RescatApp/issues/56) Secciones ofertas · [#57](https://github.com/alejandroramirezucb/RescatApp/issues/57) Encabezado y categorías |
| **Fernando Terrazas** | `explorar` · `detalle` | [#50](https://github.com/alejandroramirezucb/RescatApp/issues/50) Filtrado y orden · [#51](https://github.com/alejandroramirezucb/RescatApp/issues/51) Explorar · [#52](https://github.com/alejandroramirezucb/RescatApp/issues/52) Reserva · [#53](https://github.com/alejandroramirezucb/RescatApp/issues/53) Detalle · [#54](https://github.com/alejandroramirezucb/RescatApp/issues/54) Propagación estado |
| **Ariany Lopez** | `design-system` · Figma | [#40](https://github.com/alejandroramirezucb/RescatApp/issues/40) Tema visual · [#41](https://github.com/alejandroramirezucb/RescatApp/issues/41) Tarjeta oferta · [#42](https://github.com/alejandroramirezucb/RescatApp/issues/42) Búsqueda y chips · [#43](https://github.com/alejandroramirezucb/RescatApp/issues/43) Componentes pedidos y perfil |
