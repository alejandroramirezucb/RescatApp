package com.rescatapp.features.notificaciones.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioNotificaciones
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.core.util.suscripcionPantalla
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    val uiState: StateFlow<NotificacionesUiState> =
        UsuarioDemo.rol.flatMapLatest { rol ->
            combine(
                repositorioNotificaciones.observarPorRol(rol),
                repositorioNotificaciones.contarNoLeidas(rol)
            ) { notificaciones, noLeidas ->
                NotificacionesUiState(
                    notificaciones = notificaciones,
                    conteoNoLeidas = noLeidas,
                    rol = rol
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = suscripcionPantalla,
            initialValue = NotificacionesUiState(rol = UsuarioDemo.rol.value)
        )

    fun marcarComoLeida(id: String) {
        viewModelScope.launch {
            repositorioNotificaciones.marcarComoLeida(id)
        }
    }

    fun marcarTodasComoLeidas() {
        viewModelScope.launch {
            repositorioNotificaciones.marcarTodasComoLeidas(UsuarioDemo.rol.value)
        }
    }

    fun eliminar(id: String) {
        viewModelScope.launch {
            repositorioNotificaciones.eliminar(id)
        }
    }

    fun limpiarTodas() {
        viewModelScope.launch {
            repositorioNotificaciones.eliminarTodas(UsuarioDemo.rol.value)
        }
    }
}
