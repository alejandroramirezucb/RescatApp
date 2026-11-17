package com.rescatapp.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.supabase.SupabaseConexion
import com.rescatapp.core.model.ResultadoOperacion
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SupabaseAuthViewModel @Inject constructor(private val conexion: SupabaseConexion) :
    ViewModel() {
    val sesion = conexion.sesion.asStateFlow()
    val inicializado = conexion.inicializado.asStateFlow()
    val recuperando = conexion.recuperandoContrasena.asStateFlow()
    val aviso = conexion.mensajeAuth.asStateFlow()
    val errorSincronizacion = conexion.errorSincronizacion.asStateFlow()
    private val _ocupado = MutableStateFlow(false)
    val ocupado = _ocupado.asStateFlow()
    val mensaje = MutableStateFlow<String?>(null)

    fun recuperar(correo: String) = ejecutar { conexion.solicitarRecuperacion(correo) }
    fun cambiarContrasena(clave: String) = ejecutar { conexion.cambiarContrasena(clave) }
    fun sincronizar() {
        viewModelScope.launch { conexion.sincronizar() }
    }
    fun limpiarAviso() {
        conexion.mensajeAuth.value = null
    }
    fun cerrarRecuperacion() {
        conexion.recuperandoContrasena.value = false
    }

    private fun ejecutar(accion: suspend () -> ResultadoOperacion) {
        if (_ocupado.value) return
        _ocupado.value = true
        viewModelScope.launch {
            try {
                mensaje.value = when (val resultado = accion()) {
                    is ResultadoOperacion.Exito -> resultado.valor.toString()
                    is ResultadoOperacion.Error -> resultado.mensaje
                }
            } finally {
                _ocupado.value = false
            }
        }
    }
}
