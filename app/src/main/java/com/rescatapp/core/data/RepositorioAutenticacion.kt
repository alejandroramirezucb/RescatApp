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

@Singleton
class RepositorioAutenticacion @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val sesionDao: SesionDao
) {
    val sesionActual: Flow<SesionUsuario?> = sesionDao.observarSesion().map { it?.toDomain() }

    suspend fun sincronizarSesion() {
        sesionDao.obtenerSesion()?.let { UsuarioDemo.cambiarRol(it.rol) }
    }

    suspend fun iniciarSesion(correo: String, contrasena: String): ResultadoOperacion {
        val usuario = buscarUsuario(correo)
            ?: return ResultadoOperacion.Error(
                "No existe una cuenta registrada con este correo electrónico."
            )
        if (!SeguridadAuth.verificarContrasena(contrasena, usuario.contrasenaHash)) {
            return ResultadoOperacion.Error("La contraseña ingresada es incorrecta.")
        }
        return ResultadoOperacion.Exito(abrirSesion(usuario))
    }

    suspend fun iniciarSesionDemo(esNegocio: Boolean): ResultadoOperacion {
        val correo = if (esNegocio) UsuarioDemo.CORREO_NEGOCIO else UsuarioDemo.CORREO
        val usuario = buscarUsuario(correo)
            ?: return ResultadoOperacion.Error("No se encontró la cuenta de demostración.")
        return ResultadoOperacion.Exito(abrirSesion(usuario))
    }

    suspend fun registrarUsuario(datos: DatosRegistro): ResultadoOperacion {
        if (buscarUsuario(datos.correo) != null) {
            return ResultadoOperacion.Error(
                "Ya existe una cuenta registrada con este correo electrónico."
            )
        }
        val usuario = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = datos.nombre.trim(),
            correo = datos.correo.trim().lowercase(),
            contrasenaHash = SeguridadAuth.hashearContrasena(datos.contrasena),
            rol = datos.rol,
            telefono = datos.telefono.trim(),
            direccion = datos.direccion.trim(),
            nombreNegocio = datos.nombreNegocio.trim().takeIf { datos.rol == RolUsuario.NEGOCIO }
        )
        usuarioDao.insertar(usuario.toEntity())
        return ResultadoOperacion.Exito(abrirSesion(usuario))
    }

    suspend fun cerrarSesion() {
        sesionDao.limpiarSesion()
        UsuarioDemo.cambiarRol(RolUsuario.CLIENTE)
    }

    private suspend fun buscarUsuario(correo: String): Usuario? =
        usuarioDao.buscarPorCorreo(correo.trim().lowercase())?.toDomain()

    private suspend fun abrirSesion(usuario: Usuario): SesionUsuario {
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
        return sesion
    }
}
