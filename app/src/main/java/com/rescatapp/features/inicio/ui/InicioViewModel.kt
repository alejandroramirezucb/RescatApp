package com.rescatapp.features.inicio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.inicio.domain.SeleccionarOfertasInicioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class InicioViewModel @Inject constructor(
    repositorioOfertas: RepositorioOfertas,
    repositorioPedidos: RepositorioPedidos,
    private val calcularImpacto: CalcularImpactoUseCase,
    private val seleccionarOfertas: SeleccionarOfertasInicioUseCase
) : ViewModel() {
    val uiState: StateFlow<InicioUiState> =
        combine(repositorioOfertas.ofertas, repositorioPedidos.pedidos, ::crearEstado).stateIn(
            scope = viewModelScope,
            started = suscripcionPantalla,
            initialValue = crearEstado(
                repositorioOfertas.ofertas.value,
                repositorioPedidos.pedidos.value
            )
        )

    private fun crearEstado(ofertas: List<Oferta>, pedidos: List<Pedido>) = InicioUiState(
        impacto = calcularImpacto(pedidos),
        ofertas = seleccionarOfertas(ofertas)
    )
}
