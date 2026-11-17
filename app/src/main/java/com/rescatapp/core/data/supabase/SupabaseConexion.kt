package com.rescatapp.core.data.supabase

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonWriter
import com.google.gson.stream.JsonReader
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.rescatapp.BuildConfig
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.auth.domain.model.DatosRegistro
import com.rescatapp.features.auth.domain.model.SesionUsuario
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

data class RegistroPendiente(val correo: String)

internal data class CredencialesSupabase(
    val accessToken: String,
    val refreshToken: String,
    val expira: Long,
    val sesion: SesionUsuario
)

private data class DatosCuenta(
    val cuenta: String = "",
    val ofertas: List<Oferta> = emptyList(),
    val pedidos: List<Pedido> = emptyList(),
    val notificaciones: List<Notificacion> = emptyList()
)

/** Una sola fuente compartida por todas las pantallas; RLS vuelve a validar cada operacion. */
@Singleton
class SupabaseConexion @Inject constructor(
    @param:ApplicationContext private val contexto: Context,
    private val almacen: AlmacenCredenciales,
    private val cache: CacheDeCuenta,
    private val http: SupabaseHttp
) {
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, object : TypeAdapter<LocalDate>() {
            override fun write(out: JsonWriter, value: LocalDate?) { out.value(value?.toString()) }
            override fun read(input: JsonReader): LocalDate = LocalDate.parse(input.nextString())
        })
        .registerTypeAdapter(LocalDateTime::class.java, object : TypeAdapter<LocalDateTime>() {
            override fun write(out: JsonWriter, value: LocalDateTime?) { out.value(value?.toString()) }
            override fun read(input: JsonReader): LocalDateTime =
                LocalDateTime.parse(input.nextString())
        }).create()
    private val bloqueo = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var sondeo: Job? = null
    private var credenciales: CredencialesSupabase? = null
    private val datos = MutableStateFlow(DatosCuenta())
    val sesion = MutableStateFlow<SesionUsuario?>(null)
    val errorSincronizacion = MutableStateFlow<String?>(null)
    val recuperandoContrasena = MutableStateFlow(false)
    val mensajeAuth = MutableStateFlow<String?>(null)
    val inicializado = MutableStateFlow(false)

    val ofertas = combine(datos, sesion) { estado, actual ->
        if (estado.cuenta == (actual?.usuarioId ?: PUBLICO)) estado.ofertas else emptyList()
    }
    val pedidos = combine(datos, sesion) { estado, actual ->
        if (actual != null && estado.cuenta == actual.usuarioId) estado.pedidos else emptyList()
    }
    val notificaciones = combine(datos, sesion) { estado, actual ->
        if (actual != null && estado.cuenta == actual.usuarioId) estado.notificaciones else emptyList()
    }

    suspend fun inicializar() = bloqueo.withLock {
        if (inicializado.value) return@withLock
        credenciales = almacen.leer("sesion")?.let {
            runCatching { gson.fromJson(it, CredencialesSupabase::class.java) }.getOrNull()
        }
        sesion.value = credenciales?.sesion
        UsuarioDemo.actualizarNegocio(sesion.value?.nombreNegocio)
        UsuarioDemo.cambiarRol(sesion.value?.rol ?: RolUsuario.CLIENTE)
        try {
            cargarCache()
        } catch (_: Exception) {
            datos.value = DatosCuenta(sesion.value?.usuarioId ?: PUBLICO)
        } finally {
            inicializado.value = true
        }
    }

    fun activarSincronizacion(activa: Boolean) {
        sondeo?.cancel()
        sondeo = if (activa) scope.launch {
            inicializar()
            while (isActive) {
                sincronizar()
                delay(10_000)
            }
        } else null
    }

    suspend fun sincronizar() {
        bloqueo.withLock {
            try {
                refrescarDatos()
                errorSincronizacion.value = null
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                errorSincronizacion.value = "Sin conexion con el servidor. Mostrando los ultimos datos disponibles."
            }
        }
    }

    suspend fun login(correo: String, contrasena: String): ResultadoOperacion = resultado {
        bloqueo.withLock {
            val respuesta = http.ejecutar(
                "/auth/v1/token?grant_type=password", "POST",
                mapOf("email" to correo.trim(), "password" to contrasena)
            ).asJsonObject
            aceptarSesion(respuesta)
            sesion.value!!
        }
    }

    suspend fun registrar(registro: DatosRegistro): ResultadoOperacion = resultado {
        bloqueo.withLock {
            val reto = nuevoPkce("registro")
            val respuesta = http.ejecutar(
                "/auth/v1/signup?redirect_to=${Uri.encode(CALLBACK)}", "POST",
                mapOf(
                    "email" to registro.correo.trim(), "password" to registro.contrasena,
                    "code_challenge" to reto, "code_challenge_method" to "s256",
                    "data" to mapOf(
                        "nombre" to registro.nombre.trim(), "rol" to registro.rol.name,
                        "nombreNegocio" to registro.nombreNegocio.trim(),
                        "telefono" to registro.telefono.trim(), "direccion" to registro.direccion.trim()
                    )
                )
            ).asJsonObject
            if (respuesta.texto("access_token").isNotBlank()) {
                aceptarSesion(respuesta)
                sesion.value!!
            } else {
                RegistroPendiente(registro.correo.trim())
            }
        }
    }

    suspend fun solicitarRecuperacion(correo: String): ResultadoOperacion = resultado {
        bloqueo.withLock {
            val reto = nuevoPkce("recuperacion")
            http.ejecutar(
                "/auth/v1/recover?redirect_to=${Uri.encode(CALLBACK)}", "POST",
                mapOf("email" to correo.trim(), "code_challenge" to reto, "code_challenge_method" to "s256")
            )
            "Si el correo esta registrado, recibiras un enlace para recuperar el acceso."
        }
    }

    suspend fun recibirCallback(uri: Uri) {
        if (uri.scheme != "rescatapp" || uri.host != "auth" || uri.path != "/callback") return
        val resultado = resultado {
            bloqueo.withLock {
                require(uri.getQueryParameter("error") == null) { "El enlace no es valido o ha vencido. Solicita uno nuevo." }
                val codigo = uri.getQueryParameter("code") ?: error("El enlace no contiene un codigo de confirmacion.")
                val verificador = almacen.leer("pkce") ?: error("Abre el enlace en el mismo telefono donde lo solicitaste.")
                val tipo = almacen.leer("pkce_tipo")
                val respuesta = http.ejecutar(
                    "/auth/v1/token?grant_type=pkce", "POST",
                    mapOf("auth_code" to codigo, "code_verifier" to verificador)
                ).asJsonObject
                aceptarSesion(respuesta)
                almacen.guardar("pkce", null)
                almacen.guardar("pkce_tipo", null)
                recuperandoContrasena.value = tipo == "recuperacion"
                "Correo confirmado. Tu sesion esta iniciada."
            }
        }
        mensajeAuth.value = when (resultado) {
            is ResultadoOperacion.Exito -> if (recuperandoContrasena.value) null else resultado.valor.toString()
            is ResultadoOperacion.Error -> resultado.mensaje
        }
    }

    suspend fun cambiarContrasena(contrasena: String): ResultadoOperacion = resultado {
        bloqueo.withLock {
            require(contrasena.length >= 8) { "La contrasena debe tener al menos 8 caracteres." }
            pedir("/auth/v1/user", "PUT", mapOf("password" to contrasena))
            recuperandoContrasena.value = false
            "Contrasena actualizada."
        }
    }

    suspend fun logout() = bloqueo.withLock {
        val token = credenciales?.accessToken
        val cuenta = sesion.value?.usuarioId
        limpiarSesion()
        cuenta?.let { cache.borrar(it) }
        // El cierre local funciona aun sin red. El refresh remoto se revoca si hay conexion.
        try {
            if (token != null) http.ejecutar("/auth/v1/logout?scope=local", "POST", token = token)
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (_: IOException) {
            // La sesion local ya esta eliminada.
        }
    }

    suspend fun guardarOferta(oferta: Oferta, nueva: Boolean): Oferta = bloqueo.withLock {
        val cuenta = exigirSesion()
        require(cuenta.rol == RolUsuario.NEGOCIO) { "Ingresa con una cuenta de negocio." }
        val negocio = pedir("/rest/v1/negocios?propietario_id=eq.${cuenta.usuarioId}&select=id,nombre").asJsonArray.firstOrNull()?.asJsonObject
            ?: error("No se encontro el negocio asociado a tu cuenta.")
        val foto = subirImagen(oferta.imagenUri, cuenta.usuarioId)
        val campos = mutableMapOf<String, Any?>(
            "nombre" to oferta.nombre, "categoria" to oferta.categoria.name,
            "descripcion" to oferta.descripcion, "peso_kg" to oferta.pesoKg,
            "precio_normal" to oferta.precioNormal, "precio_rescate" to oferta.precioRescate,
            "cantidad_disponible" to oferta.cantidadDisponible,
            "fecha_retiro" to oferta.fechaRetiro.toString(),
            "hora_retiro_desde" to oferta.horaRetiroDesde,
            "hora_retiro_hasta" to oferta.horaRetiroHasta,
            "imagen_url" to foto
        )
        if (nueva) campos["negocio_id"] = negocio.entero("id")
        val respuesta = pedir(
            if (nueva) "/rest/v1/ofertas" else "/rest/v1/ofertas?id=eq.${oferta.id}",
            if (nueva) "POST" else "PATCH", campos
        ).asJsonArray.firstOrNull()?.asJsonObject ?: error("La oferta ya no existe o no te pertenece.")
        respuesta.add("negocios", JsonObject().apply {
            addProperty("nombre", negocio.texto("nombre"))
            addProperty("propietario_id", cuenta.usuarioId)
        })
        val guardada = respuesta.aOferta()
        datos.value = datos.value.copy(ofertas = listOf(guardada) + datos.value.ofertas.filterNot { it.id == guardada.id })
        refrescarSinFallar()
        guardada
    }

    suspend fun eliminarOferta(id: Int) = bloqueo.withLock {
        exigirSesion()
        pedir("/rest/v1/rpc/eliminar_oferta", "POST", mapOf("p_oferta_id" to id))
        datos.value = datos.value.copy(ofertas = datos.value.ofertas.filterNot { it.id == id })
        refrescarSinFallar()
    }

    suspend fun cambiarEstadoOferta(id: Int, estado: String) = bloqueo.withLock {
        exigirSesion()
        pedir("/rest/v1/rpc/cambiar_estado_oferta", "POST", mapOf("p_oferta_id" to id, "p_estado" to estado))
        datos.value = datos.value.copy(ofertas = datos.value.ofertas.map { if (it.id == id) it.copy(estadoPublicacion = estado) else it })
        refrescarSinFallar()
    }

    suspend fun reservar(id: Int): ResultadoOperacion = resultado {
        bloqueo.withLock {
            val cuenta = exigirSesion()
            require(cuenta.rol == RolUsuario.CLIENTE) { "Las reservas requieren una cuenta de cliente." }
            val clave = "reserva_${cuenta.usuarioId}_$id"
            val solicitud = almacen.leer(clave) ?: UUID.randomUUID().toString().also { almacen.guardar(clave, it) }
            val respuesta = pedir("/rest/v1/rpc/reservar_oferta", "POST", mapOf("p_oferta_id" to id, "p_solicitud_id" to solicitud))
            val pedido = respuesta.asJsonArray.single().asJsonObject.aPedido()
            almacen.guardar(clave, null)
            datos.value = datos.value.copy(pedidos = listOf(pedido) + datos.value.pedidos.filterNot { it.id == pedido.id })
            refrescarSinFallar()
            pedido
        }
    }

    suspend fun cancelar(id: Int): ResultadoOperacion = operarPedido("cancelar_pedido", mapOf("p_pedido_id" to id))

    suspend fun avanzar(id: Int, estadoEsperado: String, codigo: String?): ResultadoOperacion = operarPedido(
        "avanzar_pedido", mapOf("p_pedido_id" to id, "p_estado_esperado" to estadoEsperado, "p_codigo_rescate" to codigo)
    )

    suspend fun modificarNotificaciones(id: String?, todas: Boolean, eliminar: Boolean) = bloqueo.withLock {
        val cuenta = exigirSesion()
        val filtro = if (todas) "destinatario_id=eq.${cuenta.usuarioId}" else "id=eq.${Uri.encode(id)}"
        pedir("/rest/v1/notificaciones?$filtro", if (eliminar) "DELETE" else "PATCH", mapOf("leida" to true))
        datos.value = datos.value.copy(notificaciones = datos.value.notificaciones.mapNotNull {
            if (todas || it.id == id) { if (eliminar) null else it.copy(leida = true) } else it
        })
        refrescarSinFallar()
    }

    private suspend fun operarPedido(rpc: String, campos: Map<String, Any?>): ResultadoOperacion = resultado {
        bloqueo.withLock {
            exigirSesion()
            val pedido = pedir("/rest/v1/rpc/$rpc", "POST", campos).asJsonArray.single().asJsonObject.aPedido()
            datos.value = datos.value.copy(pedidos = listOf(pedido) + datos.value.pedidos.filterNot { it.id == pedido.id })
            refrescarSinFallar()
            pedido
        }
    }

    private suspend fun aceptarSesion(respuesta: JsonObject) {
        val token = respuesta.texto("access_token")
        val usuario = http.ejecutar("/auth/v1/user", token = token).asJsonObject
        val id = usuario.texto("id")
        val perfil = http.ejecutar("/rest/v1/perfiles?id=eq.$id&select=*", token = token)
            .asJsonArray.firstOrNull()?.asJsonObject ?: error("Tu cuenta no tiene perfil. Revisa el trigger de usuarios en Supabase.")
        val rol = RolUsuario.valueOf(perfil.texto("rol"))
        val negocio = if (rol == RolUsuario.NEGOCIO) {
            http.ejecutar("/rest/v1/negocios?propietario_id=eq.$id&select=nombre", token = token)
                .asJsonArray.firstOrNull()?.asJsonObject?.texto("nombre")
        } else null
        val nueva = SesionUsuario(id, perfil.texto("nombre"), usuario.texto("email"), rol, negocio)
        credenciales = CredencialesSupabase(token, respuesta.texto("refresh_token"), System.currentTimeMillis() / 1000 + respuesta.get("expires_in").asLong, nueva)
        almacen.guardar("sesion", gson.toJson(credenciales))
        datos.value = DatosCuenta()
        sesion.value = nueva
        UsuarioDemo.actualizarNegocio(negocio)
        UsuarioDemo.cambiarRol(rol)
        cargarCache()
        refrescarSinFallar()
    }

    private suspend fun pedir(ruta: String, metodo: String = "GET", campos: Any? = null, archivo: ByteArray? = null, mime: String = "application/json"): JsonElement {
        var actual = credenciales
        if (actual != null && actual.expira <= System.currentTimeMillis() / 1000 + 60) {
            renovarToken()
            actual = credenciales
        }
        return try {
            http.ejecutar(ruta, metodo, campos, actual?.accessToken, archivo, mime)
        } catch (error: ErrorSupabase) {
            if (error.codigo != 401 || actual == null) throw error
            renovarToken()
            http.ejecutar(ruta, metodo, campos, credenciales?.accessToken, archivo, mime)
        }
    }

    private suspend fun renovarToken() {
        val anterior = credenciales ?: error("Inicia sesion para continuar.")
        val respuesta = try {
            http.ejecutar("/auth/v1/token?grant_type=refresh_token", "POST", mapOf("refresh_token" to anterior.refreshToken)).asJsonObject
        } catch (error: ErrorSupabase) {
            if (error.codigo == 400 || error.codigo == 401 || error.codigo == 403) limpiarSesion()
            throw error
        }
        credenciales = anterior.copy(
            accessToken = respuesta.texto("access_token"), refreshToken = respuesta.texto("refresh_token"),
            expira = System.currentTimeMillis() / 1000 + respuesta.get("expires_in").asLong
        )
        almacen.guardar("sesion", gson.toJson(credenciales))
    }

    private suspend fun leerTodas(ruta: String): JsonArray {
        val todas = JsonArray()
        var pagina: JsonArray
        do {
            pagina = pedir("$ruta&limit=500&offset=${todas.size()}").asJsonArray
            todas.addAll(pagina)
        } while (pagina.size() == 500)
        return todas
    }

    private suspend fun refrescarDatos() {
        val cuenta = sesion.value?.usuarioId ?: PUBLICO
        val lista = leerTodas("/rest/v1/ofertas?select=*,negocios(nombre,propietario_id)&estado=neq.ELIMINADA&order=id.desc")
        val pedidos = if (sesion.value != null) leerTodas("/rest/v1/pedidos?select=*&order=creado_at.desc,id.desc") else JsonArray()
        val avisos = if (sesion.value != null) leerTodas("/rest/v1/notificaciones?select=*&order=creado_at.desc,id.desc") else JsonArray()
        // Publicar un snapshot completo evita mezclar respuestas de diferentes cuentas.
        if (cuenta != (sesion.value?.usuarioId ?: PUBLICO)) return
        val snapshot = DatosCuenta(cuenta, lista.map { it.asJsonObject.aOferta() }, pedidos.map { it.asJsonObject.aPedido() }, avisos.map { it.asJsonObject.aNotificacion() })
        cache.guardar(cuenta, "snapshot", gson.toJson(snapshot))
        datos.value = snapshot
    }

    private suspend fun refrescarSinFallar() {
        try {
            val cuenta = sesion.value?.usuarioId ?: PUBLICO
            if (datos.value.cuenta == cuenta) cache.guardar(cuenta, "snapshot", gson.toJson(datos.value))
            refrescarDatos()
            errorSincronizacion.value = null
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (_: Exception) {
            errorSincronizacion.value = "Operacion guardada. No pudimos actualizar todas las listas; reintentaremos automaticamente."
        }
    }

    private suspend fun cargarCache() {
        val cuenta = sesion.value?.usuarioId ?: PUBLICO
        datos.value = cache.leer(cuenta, "snapshot")?.let {
            runCatching { gson.fromJson(it, DatosCuenta::class.java) }.getOrNull()
        }?.takeIf { it.cuenta == cuenta } ?: DatosCuenta(cuenta)
    }

    private fun limpiarSesion() {
        datos.value = DatosCuenta()
        sesion.value = null
        credenciales = null
        recuperandoContrasena.value = false
        almacen.guardar("sesion", null)
        UsuarioDemo.actualizarNegocio(null)
        UsuarioDemo.cambiarRol(RolUsuario.CLIENTE)
    }

    private fun exigirSesion(): SesionUsuario = sesion.value ?: error("Inicia sesion para continuar.")

    private fun nuevoPkce(tipo: String): String {
        val bytes = ByteArray(48).also { SecureRandom().nextBytes(it) }
        val verificador = Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        almacen.guardar("pkce", verificador)
        almacen.guardar("pkce_tipo", tipo)
        return Base64.encodeToString(MessageDigest.getInstance("SHA-256").digest(verificador.toByteArray(Charsets.US_ASCII)), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private suspend fun subirImagen(ruta: String?, propietario: String): String? {
        if (ruta.isNullOrBlank()) return null
        if (ruta.startsWith("https://")) return ruta
        return withContext(Dispatchers.IO) {
            val uri = Uri.parse(ruta)
            require(uri.scheme == "content") { "Selecciona otra vez la fotografia de la oferta." }
            val mime = contexto.contentResolver.getType(uri) ?: error("No se pudo identificar la imagen.")
            val extension = mapOf("image/jpeg" to "jpg", "image/png" to "png", "image/webp" to "webp")[mime]
                ?: error("Usa una fotografia JPG, PNG o WebP.")
            val contenido = contexto.contentResolver.openInputStream(uri)?.use { java.io.ByteArrayOutputStream().apply {
                    val buffer = ByteArray(8192)
                    while (size() <= 5 * 1024 * 1024) {
                        val n = it.read(buffer)
                        if (n == -1) break
                        write(buffer, 0, n)
                    }
                }.toByteArray() }
                ?: error("No se pudo leer la fotografia.")
            require(contenido.size <= 5 * 1024 * 1024) { "La fotografia supera los 5 MiB." }
            val destino = "$propietario/${UUID.randomUUID()}.$extension"
            pedir("/storage/v1/object/ofertas/$destino", "POST", archivo = contenido, mime = mime)
            "${BuildConfig.SUPABASE_URL.trimEnd('/')}/storage/v1/object/public/ofertas/$destino"
        }
    }

    private suspend fun resultado(accion: suspend () -> Any): ResultadoOperacion = try {
        ResultadoOperacion.Exito(accion())
    } catch (cancelacion: CancellationException) {
        throw cancelacion
    } catch (error: ErrorSupabase) {
        ResultadoOperacion.Error(when (error.codigo) {
            401 -> "La sesion o las credenciales no son validas. Vuelve a iniciar sesion."
            429 -> "Demasiados intentos. Espera un momento antes de reintentar."
            else -> error.message ?: "El servidor rechazo la operacion."
        })
    } catch (_: IOException) {
        ResultadoOperacion.Error("No pudimos conectarnos. Revisa tu conexion e intenta nuevamente.")
    } catch (error: Exception) {
        ResultadoOperacion.Error(error.message ?: "No se pudo completar la operacion.")
    }

    companion object {
        const val CALLBACK = "rescatapp://auth/callback"
        private const val PUBLICO = "publico"

        fun visibleEnCatalogo(oferta: Oferta): Boolean = oferta.estadoPublicacion == "ACTIVA" &&
            oferta.cantidadDisponible > 0 &&
            oferta.fechaRetiro.atTime(LocalTime.parse(oferta.horaRetiroHasta)).atZone(zonaBolivia).toInstant().isAfter(java.time.Instant.now())
    }
}
