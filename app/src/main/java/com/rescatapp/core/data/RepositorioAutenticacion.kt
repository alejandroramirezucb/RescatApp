package com.rescatapp.core.data

import com.rescatapp.core.data.local.SesionDao
import com.rescatapp.core.data.local.UsuarioDao
import com.rescatapp.core.data.local.toDomain
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.auth.data.SeguridadAuth
import com.rescatapp.features.auth.domain.model.DatosRegistro
import com.rescatapp.features.auth.domain.model.SesionUsuario
import com.rescatapp.features.auth.domain.model.Usuario
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@Singleton
class RepositorioAutenticacion @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val sesionDao: SesionDao
) {

    val sesionActual: Flow<SesionUsuario?> = sesionDao.observarSesion()
        .map { it?.toDomain() }
        .onEach { sesion ->
            if (sesion != null) {
                UsuarioDemo.cambiarRol(sesion.rol)
            }
        }

    suspend fun obtenerSesionActual(): SesionUsuario? = sesionDao.obtenerSesion()?.toDomain()

    suspend fun sincronizarSesion(): SesionUsuario? {
        val sesion = sesionDao.obtenerSesion()?.toDomain()
        if (sesion != null) {
            UsuarioDemo.cambiarRol(sesion.rol)
        }
        return sesion
    }

    suspend fun buscarPorCorreo(correo: String): Usuario? =
        usuarioDao.buscarPorCorreo(correo.trim().lowercase())?.toDomain()

    suspend fun buscarPorId(id: String): Usuario? = usuarioDao.buscarPorId(id)?.toDomain()

    suspend fun iniciarSesion(correo: String, contrasena: String): ResultadoOperacion {
        val correoNormalizado = correo.trim().lowercase()
        val usuario = usuarioDao.buscarPorCorreo(correoNormalizado)?.toDomain()
            ?: return ResultadoOperacion.Error(
                "No existe una cuenta registrada con este correo electrónico."
            )

        val contrasenaValida = SeguridadAuth.verificarContrasena(
            contrasenaPlana = contrasena,
            hashAlmacenado = usuario.contrasenaHash
        )
        if (!contrasenaValida) {
            return ResultadoOperacion.Error("La contraseña ingresada es incorrecta.")
        }

        val sesion = SesionUsuario(
            usuarioId = usuario.id,
            nombre = usuario.nombre,
            correo = usuario.correo,
            rol = usuario.rol,
            nombreNegocio = usuario.nombreNegocio,
            token = "token-${usuario.id}-${System.currentTimeMillis()}"
        )
        sesionDao.guardarSesion(sesion.toEntity())
        UsuarioDemo.cambiarRol(usuario.rol)
        return ResultadoOperacion.Exito(sesion)
    }

    suspend fun registrarUsuario(datos: DatosRegistro): ResultadoOperacion {
        val correoNormalizado = datos.correo.trim().lowercase()
        val existente = usuarioDao.buscarPorCorreo(correoNormalizado)
        if (existente != null) {
            return ResultadoOperacion.Error(
                "Ya existe una cuenta registrada con este correo electrónico."
            )
        }

        val nuevoUsuario = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = datos.nombre.trim(),
            correo = correoNormalizado,
            contrasenaHash = SeguridadAuth.hashearContrasena(datos.contrasena),
            rol = datos.rol,
            telefono = datos.telefono.trim(),
            direccion = datos.direccion.trim(),
            nombreNegocio = if (datos.rol == RolUsuario.NEGOCIO) {
                datos.nombreNegocio.trim().ifBlank { datos.nombre.trim() }
            } else {
                null
            }
        )

        usuarioDao.insertar(nuevoUsuario.toEntity())

        val sesion = SesionUsuario(
            usuarioId = nuevoUsuario.id,
            nombre = nuevoUsuario.nombre,
            correo = nuevoUsuario.correo,
            rol = nuevoUsuario.rol,
            nombreNegocio = nuevoUsuario.nombreNegocio,
            token = "token-${nuevoUsuario.id}-${System.currentTimeMillis()}"
        )
        sesionDao.guardarSesion(sesion.toEntity())
        UsuarioDemo.cambiarRol(nuevoUsuario.rol)
        return ResultadoOperacion.Exito(sesion)
    }

    suspend fun cerrarSesion() {
        sesionDao.limpiarSesion()
        UsuarioDemo.cambiarRol(RolUsuario.CLIENTE)
    }

    suspend fun iniciarSesionRapidaDemo(esNegocio: Boolean): ResultadoOperacion {
        val correo = if (esNegocio) "contacto@lacentral.com" else "invitado@example.com"
        val usuario = usuarioDao.buscarPorCorreo(correo)?.toDomain()
        return if (usuario != null) {
            val sesion = SesionUsuario(
                usuarioId = usuario.id,
                nombre = usuario.nombre,
                correo = usuario.correo,
                rol = usuario.rol,
                nombreNegocio = usuario.nombreNegocio,
                token = "token-demo-${System.currentTimeMillis()}"
            )
            sesionDao.guardarSesion(sesion.toEntity())
            UsuarioDemo.cambiarRol(usuario.rol)
            ResultadoOperacion.Exito(sesion)
        } else {
            val rol = if (esNegocio) RolUsuario.NEGOCIO else RolUsuario.CLIENTE
            val sesion = SesionUsuario(
                usuarioId = if (esNegocio) "demo-negocio" else "demo-cliente",
                nombre = if (esNegocio) UsuarioDemo.NEGOCIO_DEMO else UsuarioDemo.NOMBRE,
                correo = correo,
                rol = rol,
                nombreNegocio = if (esNegocio) UsuarioDemo.NEGOCIO_DEMO else null,
                token = "token-demo-${System.currentTimeMillis()}"
            )
            sesionDao.guardarSesion(sesion.toEntity())
            UsuarioDemo.cambiarRol(rol)
            ResultadoOperacion.Exito(sesion)
        }
    }
}
