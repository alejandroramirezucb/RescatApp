package com.rescatapp.features.perfil.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.repository.Repositorio
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.model.Pedido
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PerfilViewModel @Inject constructor(
    repositorio: Repositorio<Pedido>,
    calcularImpactoUseCase: CalcularImpactoUseCase
) : ViewModel() {

    val uiState: StateFlow<PerfilUiState> = repositorio.elementos
        .map { listaPedidos ->
            val impacto = calcularImpactoUseCase(listaPedidos)
            PerfilUiState(
                nombre = "Invitado",
                correo = "invitado@example.com",
                inicial = "I",
                rescates = impacto.rescates,
                ahorrado = impacto.ahorrado,
                aprovechado = impacto.aprovechado
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PerfilUiState()
        )
}
