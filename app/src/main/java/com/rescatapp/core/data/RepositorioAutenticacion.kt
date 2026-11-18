package com.rescatapp.core.data

import com.rescatapp.core.data.local.SesionDao
import com.rescatapp.core.data.local.SesionEntity
import com.rescatapp.core.data.remote.SupabaseApi
import com.rescatapp.core.data.remote.TokenDto
import com.rescatapp.core.data.remote.mensajeDeError
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.DatosRegistro
import com.rescatapp.features.auth.domain.model.SesionUsuario
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

@Singleton
class RepositorioAutenticacion @Inject constructor(
    private val api: SupabaseApi,
    private val sesionDao: SesionDao
) {
    val sesionActual: Flow<SesionUsuario?> = sesionDao.observarSesion().map { it?.toDomain() }

    val rol: Flow<RolUsuario> = sesionActual.map { it?.rol ?: RolUsuario.CLIENTE }

    suspend fun sesion(): SesionUsuario? = sesionDao.obtenerSesion()?.toDomain()

    suspend fun iniciarSesion(correo: String, contrasena: String): ResultadoOperacion = try {
        val token = api.iniciarSesion(
            mapOf("email" to correo.trim().lowercase(), "password" to contrasena)
        )
        ResultadoOperacion.Exito(abrirSesion(token))
    } catch (error: Exception) {
        ResultadoOperacion.Error(mensajeDeError(error))
    }

    suspend fun registrarUsuario(datos: DatosRegistro): ResultadoOperacion = try {
        val token = api.registrarUsuario(
            mapOf(
                "email" to datos.correo.trim().lowercase(),
                "password" to datos.contrasena,
                "data" to mapOf(
                    "nombre" to datos.nombre.trim(),
                    "rol" to datos.rol.name,
                    "nombreNegocio" to datos.nombreNegocio.trim(),
                    "telefono" to datos.telefono.trim(),
                    "direccion" to datos.direccion.trim()
                )
            )
        )
        if (token.accessToken == null) {
            ResultadoOperacion.Exito(CONFIRMAR_CORREO)
        } else {
            ResultadoOperacion.Exito(abrirSesion(token))
        }
    } catch (error: Exception) {
        ResultadoOperacion.Error(mensajeDeError(error))
    }

    suspend fun cerrarSesion() {
        sesionDao.limpiarSesion()
    }

    suspend fun token(): String? {
        val sesion = sesionDao.obtenerSesion() ?: return null
        if (sesion.expira > ahora() + MARGEN_SEGUNDOS) return "Bearer ${sesion.token}"
        return try {
            val nuevo = api.renovarToken(mapOf("refresh_token" to sesion.refreshToken))
            sesionDao.guardarSesion(
                sesion.copy(
                    token = nuevo.accessToken.orEmpty(),
                    refreshToken = nuevo.refreshToken.orEmpty(),
                    expira = ahora() + (nuevo.expiraEn ?: 0)
                )
            )
            "Bearer ${nuevo.accessToken}"
        } catch (_: HttpException) {
            cerrarSesion()
            null
        }
    }

    suspend fun tokenObligatorio(): String = token() ?: error("Inicia sesión para continuar.")

    private suspend fun abrirSesion(token: TokenDto): SesionUsuario {
        val acceso = "Bearer ${token.accessToken}"
        val usuario = checkNotNull(token.user)
        val perfil = api.obtenerPerfil(acceso, "eq.${usuario.id}").first()
        val rol = RolUsuario.valueOf(perfil.rol)
        val negocio = if (rol == RolUsuario.NEGOCIO) {
            api.obtenerNegocio(acceso, "eq.${usuario.id}").firstOrNull()?.nombre
        } else {
            null
        }
        val sesion = SesionEntity(
            usuarioId = usuario.id,
            correo = usuario.email,
            nombre = perfil.nombre,
            rol = rol,
            nombreNegocio = negocio,
            direccion = perfil.direccion,
            token = token.accessToken.orEmpty(),
            refreshToken = token.refreshToken.orEmpty(),
            expira = ahora() + (token.expiraEn ?: 0)
        )
        sesionDao.guardarSesion(sesion)
        return sesion.toDomain()
    }

    private fun ahora(): Long = System.currentTimeMillis() / 1000

    companion object {
        const val CONFIRMAR_CORREO =
            "Te enviamos un correo. Confirma tu cuenta y luego inicia sesión."
        private const val MARGEN_SEGUNDOS = 60
    }
}
