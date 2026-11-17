package com.rescatapp.features.notificaciones.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioNotificaciones
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.core.util.suscripcionPantalla
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificacionesViewModel @Inject constructor(
    private val repositorioNotificaciones: RepositorioNotificaciones
) : ViewModel() {

    private val mensaje = MutableStateFlow<String?>(null)

    val uiState: StateFlow<NotificacionesUiState> =
        UsuarioDemo.rol.flatMapLatest { rol ->
            combine(
                repositorioNotificaciones.observarPorRol(rol),
                repositorioNotificaciones.contarNoLeidas(rol),
                mensaje
            ) { notificaciones, noLeidas, error ->
                NotificacionesUiState(
                    notificaciones = notificaciones,
                    conteoNoLeidas = noLeidas,
                    error = error
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = suscripcionPantalla,
            initialValue = NotificacionesUiState()
        )

    fun marcarComoLeida(id: String) {
        operar {
            repositorioNotificaciones.marcarComoLeida(id)
        }
    }

    fun marcarTodasComoLeidas() {
        operar {
            repositorioNotificaciones.marcarTodasComoLeidas(UsuarioDemo.rol.value)
        }
    }

    fun eliminar(id: String) {
        operar {
            repositorioNotificaciones.eliminar(id)
        }
    }

    fun limpiarTodas() {
        operar {
            repositorioNotificaciones.eliminarTodas(UsuarioDemo.rol.value)
        }
    }
    fun limpiarError() {
        mensaje.value = null
    }

    private fun operar(accion: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                accion()
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                mensaje.value =
                    "No se pudo actualizar la notificacion. Revisa tu conexion y reintenta."
            }
        }
    }
}
