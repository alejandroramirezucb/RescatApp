package com.rescatapp.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.domain.SincronizarDatosUseCase
import com.rescatapp.features.auth.domain.model.SesionUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PrincipalViewModel @Inject constructor(
    repositorioAutenticacion: RepositorioAutenticacion,
    private val sincronizarDatos: SincronizarDatosUseCase
) : ViewModel() {

    val sesion: StateFlow<SesionUsuario?> = repositorioAutenticacion.sesionActual
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _errorConexion = MutableStateFlow<String?>(null)
    val errorConexion: StateFlow<String?> = _errorConexion.asStateFlow()

    suspend fun sincronizar() {
        _errorConexion.value = sincronizarDatos()
    }

    fun reintentar() {
        viewModelScope.launch { sincronizar() }
    }
}
