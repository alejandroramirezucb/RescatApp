# RescatApp

Aplicación Android para rescatar excedentes de alimentos. Los negocios publican
ofertas a precio reducido y los clientes las exploran, reservan y retiran. El
proyecto es una demo académica; los pagos son simulados.

## Funcionalidades

- Registro e inicio de sesión con Supabase Auth y perfiles de cliente o negocio.
- Catálogo compartido entre dispositivos: lo que publica un negocio lo reserva cualquier cliente.
- Búsqueda, categorías y orden por descuento.
- Creación, edición, pausa, reactivación y eliminación de ofertas propias.
- Fotografías compartidas mediante Supabase Storage.
- Reservas, cancelaciones, preparación y entrega mediante operaciones del servidor.
- Pago simulado con código QR o en efectivo al retirar.
- Confirmación de entrega con el código de rescate del cliente.
- Notificaciones al reservar, cancelar y cambiar el estado de un pedido.
- Historial y notificaciones privados para cada cuenta.
- Indicadores de ahorro, rescates recogidos y alimentos aprovechados.
- Caché local en Room y actualización automática con la app visible.

## Tecnologías

| Tecnología | Uso |
| --- | --- |
| Kotlin | Lenguaje principal |
| Jetpack Compose y Material 3 | Interfaz de usuario |
| Navigation Compose | Navegación entre pantallas |
| ViewModel, Flow y StateFlow | Estado y presentación reactiva |
| Hilt | Inyección de dependencias |
| Supabase Auth | Usuarios, autenticación y sesiones |
| PostgreSQL y RLS | Persistencia compartida y permisos por usuario |
| Supabase Storage | Fotografías de las ofertas |
| Retrofit y Gson | Comunicación REST y serialización JSON |
| Room | Caché local de ofertas, pedidos, notificaciones y sesión |
| JUnit y Robolectric | Pruebas automatizadas |
| Spotless y ktlint | Formato uniforme del código |

Las versiones se declaran en [gradle/libs.versions.toml](gradle/libs.versions.toml).

## Arquitectura

La aplicación utiliza MVVM. Las pantallas Compose consumen el estado de los
ViewModels; los casos de uso coordinan las reglas del dominio y los repositorios
piden los datos a Supabase con Retrofit y los guardan en Room.

| Ubicación | Responsabilidad |
| --- | --- |
| `core/model` | Modelos de ofertas, pedidos, usuarios y notificaciones |
| `core/domain` | Reglas compartidas y cálculo de impacto |
| `core/data` | Repositorios |
| `core/data/remote` | `SupabaseApi` (Retrofit), DTOs, `ModuloRed` y subida de fotos |
| `core/data/local` | Entidades, DAOs y base Room usada como caché |
| `core/designsystem` | Tema y componentes visuales reutilizables |
| `features` | Pantallas, ViewModels y casos de uso por funcionalidad |
| `navigation` | Rutas y navegación principal |
| `supabase/sql` | Definición y verificación del servidor |

Supabase conserva los datos compartidos y privados. Room (`rescatapp.db`) guarda
lo último que devolvió el servidor y la sesión activa; las pantallas leen Room con
`Flow`. Explorar filtra con SQL en `OfertaDao.filtrar`.

La conexión y los contratos del servidor se describen en
[Documentación de Supabase](docs/supabase/conexion-android.md).

## Requisitos técnicos

| Requisito | Implementación |
| --- | --- |
| Room | `OfertaEntity`, `PedidoEntity`, `NotificacionEntity` y `SesionEntity`; sus DAO y `RescatAppDatabase` |
| CRUD de ofertas | Crear en `RegistroScreen`, leer en Explorar, editar, pausar y eliminar en Mis ofertas |
| Flow y StateFlow | Los DAO devuelven `Flow`; los ViewModels exponen `StateFlow` y Compose usa `collectAsStateWithLifecycle()` |
| Filtro dinámico | `OfertaDao.filtrar(categoria, texto)` con parámetros SQL; el orden se elige en Explorar |
| Retrofit | `SupabaseApi` con GET, POST, PATCH y DELETE; Gson convierte los DTO de `core/data/remote` |
| Carga y errores | Indicadores de progreso, `try/catch/finally` en los ViewModels, avisos temporales y aviso sin conexión |
| Navegación | `NavHost` en `GrafoNavegacion`; rutas en `Rutas` y uso de `navigate()` y `popBackStack()` |
| Validaciones | Campos obligatorios, números, horas `HH:mm`, precio de rescate menor y correo y contraseña |
| Diseño adaptable | Columnas según el ancho y el tamaño de texto; contenido centrado hasta 600 dp en tablets |

## Configuración y ejecución

Requisitos: Android Studio, JDK 17 o compatible con Gradle y un dispositivo Android
con API 29 o superior. Las versiones de SDK y herramientas están declaradas en
[app/build.gradle.kts](app/build.gradle.kts).

1. Abrir el proyecto en Android Studio.
2. Copiar `supabase.local.properties.example` a `supabase.local.properties`.
3. Completar la URL del proyecto y su clave pública, sin comillas:

```properties
SUPABASE_URL=https://IDENTIFICADOR.supabase.co
SUPABASE_PUBLISHABLE_KEY=sb_publishable_CLAVE_PUBLICA
```

4. Sincronizar Gradle y ejecutar la aplicación en un emulador o dispositivo.

El archivo local está excluido de Git. Gradle también admite esas dos variables
de entorno. Sin ellas el proyecto compila igual, pero la app muestra el aviso de
sin conexión. El workflow de GitHub Actions las obtiene de las variables del
repositorio con los mismos nombres. Android no utiliza claves secretas ni
`service_role`.

El servidor requiere las tablas, políticas y funciones documentadas en
`supabase/sql`. Los scripts de creación de estructura se aplican una sola vez
en una base nueva; no es necesario repetirlos para ejecutar Android contra un
proyecto ya configurado.

## Comandos de desarrollo

| Comando en PowerShell | Finalidad |
| --- | --- |
| `.\gradlew.bat spotlessApply` | Aplicar formato |
| `.\gradlew.bat spotlessCheck` | Comprobar formato |
| `.\gradlew.bat :app:testDebugUnitTest` | Ejecutar pruebas unitarias |
| `.\gradlew.bat :app:assembleDebug` | Generar el APK de desarrollo |

El hook opcional se activa con `git config core.hooksPath .githooks`. Formatea
los archivos Kotlin preparados para el commit y se detiene si también tienen
cambios sin preparar. El workflow de [GitHub Actions](.github/workflows/android.yml)
comprueba formato, pruebas unitarias y generación del APK.

## Alcance de la demo

Los pagos no realizan cobros reales. El catálogo, los pedidos y las notificaciones
se actualizan mediante sondeo REST cada 10 segundos con la app visible. La entrega
de notificaciones push con la app cerrada no está implementada.

La confirmación de correo es configurable en Supabase; la demo admite registro
inmediato al desactivarla. La app no incluye recuperación de contraseña.

## Impacto

El objetivo principal se relaciona con el ODS 12, producción y consumo responsables:
reducir el desperdicio de alimentos conectando excedentes con compradores. El
ahorro económico también facilita el acceso a alimentos a menor precio.

El impacto se calcula sobre los pedidos recogidos mediante
[CalcularImpactoUseCase](app/src/main/java/com/rescatapp/core/domain/CalcularImpactoUseCase.kt).

## Equipo

| Integrante | GitHub |
| --- | --- |
| **Alejandro Ramirez** | [alejandroramirezucb](https://github.com/alejandroramirezucb) |
| **Dariana Pol Aramayo** | [D4ri4na](https://github.com/D4ri4na) |
| **Josue Balbontin** | [josue-balbontin](https://github.com/josue-balbontin) |
| **Fernando Terrazas** | [FernandoTerrazasLl](https://github.com/FernandoTerrazasLl) |
| **Ariany Lopez** | [arianylopez](https://github.com/arianylopez) |
