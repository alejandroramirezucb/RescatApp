package com.rescatapp.features.perfil.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.domain.CalcularImpactoSemanalUseCase
import com.rescatapp.features.perfil.domain.PerfilUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PerfilViewModel @Inject constructor(
    repositorioPedidos: RepositorioPedidos,
    calcularImpacto: CalcularImpactoSemanalUseCase
) : ViewModel() {

    val uiState: StateFlow<PerfilUiState> = repositorioPedidos.pedidos
        .map { listaPedidos ->
            val impacto = calcularImpacto(listaPedidos)
            PerfilUiState(
                nombre = "Invitado",
                correo = "invitado@example.com",
                inicial = "I",
                rescates = impacto.reservas, // Propiedad de ImpactoSemanal
                ahorrado = impacto.ahorro,   // Propiedad de ImpactoSemanal
                aprovechado = impacto.pesoKg // Propiedad de ImpactoSemanal
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PerfilUiState()
        )
}