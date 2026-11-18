package com.rescatapp.core.domain

import com.rescatapp.core.data.RepositorioNotificaciones
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.remote.mensajeDeError
import com.rescatapp.core.notification.NotificadorSistemaAndroid
import javax.inject.Inject

class SincronizarDatosUseCase @Inject constructor(
    private val repositorioOfertas: RepositorioOfertas,
    private val repositorioPedidos: RepositorioPedidos,
    private val repositorioNotificaciones: RepositorioNotificaciones,
    private val notificador: NotificadorSistemaAndroid
) {
    suspend operator fun invoke(): String? = try {
        repositorioOfertas.sincronizar()
        repositorioPedidos.sincronizar()
        repositorioNotificaciones.sincronizar().forEach {
            notificador.mostrarNotificacion(it.id.hashCode(), it.titulo, it.mensaje)
        }
        null
    } catch (error: Exception) {
        mensajeDeError(error)
    }
}
