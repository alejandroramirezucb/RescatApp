package com.rescatapp.features.perfil.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoSemanalUseCase
import com.rescatapp.features.perfil.domain.PerfilUiState
import com.rescatapp.core.model.ImpactoSemanal
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PerfilViewModel @Inject constructor(
    repositorioPedidos: RepositorioPedidos,
    private val calcularImpacto: CalcularImpactoSemanalUseCase
) : ViewModel() {
    val uiState: StateFlow<PerfilUiState> = repositorioPedidos.pedidos
        .map { pedidos -> crearEstado(calcularImpacto(pedidos)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = crearEstado(calcularImpacto(repositorioPedidos.pedidos.value))
        )

    private fun crearEstado(impacto: ImpactoSemanal) = PerfilUiState(
        impacto = impacto,
        rescates = impacto.reservas,
        ahorrado = impacto.ahorro,
        aprovechado = impacto.pesoKg
    )
}
