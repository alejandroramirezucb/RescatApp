package com.rescatapp.features.perfil.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.util.suscripcionPantalla
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PerfilViewModel @Inject constructor(
    repositorioPedidos: RepositorioPedidos,
    private val calcularImpacto: CalcularImpactoUseCase
) : ViewModel() {
    val uiState: StateFlow<PerfilUiState> = repositorioPedidos.pedidos.map(::crearEstado).stateIn(
        scope = viewModelScope,
        started = suscripcionPantalla,
        initialValue = crearEstado(repositorioPedidos.pedidos.value)
    )

    private fun crearEstado(pedidos: List<Pedido>) = PerfilUiState(calcularImpacto(pedidos))
}
