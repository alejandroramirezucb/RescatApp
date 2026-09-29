package com.rescatapp.features.inicio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.features.inicio.domain.InicioUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class InicioViewModel @Inject constructor(
    repositorioOfertas: RepositorioOfertas,
    repositorioPedidos: RepositorioPedidos,
    private val calcularImpacto: CalcularImpactoUseCase
) : ViewModel() {
    val uiState: StateFlow<InicioUiState> = combine(
        repositorioOfertas.ofertas,
        repositorioPedidos.pedidos
    ) { ofertas, pedidos ->
        InicioUiState(
            ofertasCerca = ofertas.filterNot { it.estaAgotada }.sortedByDescending { it.id },
            ofertasAgotando = ofertas
                .filter { it.cantidadDisponible in 1..2 }
                .sortedByDescending { it.id },
            impacto = calcularImpacto(pedidos)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InicioUiState(
            ofertasCerca = repositorioOfertas.ofertas.value
                .filterNot { it.estaAgotada }
                .sortedByDescending { it.id },
            ofertasAgotando = repositorioOfertas.ofertas.value
                .filter { it.cantidadDisponible in 1..2 }
                .sortedByDescending { it.id },
            impacto = calcularImpacto(repositorioPedidos.pedidos.value)
        )
    )
}
