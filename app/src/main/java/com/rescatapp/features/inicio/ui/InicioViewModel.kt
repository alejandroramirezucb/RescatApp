package com.rescatapp.features.inicio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioNotificaciones
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.inicio.domain.SeleccionarOfertasInicioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class InicioViewModel @Inject constructor(
    repositorioOfertas: RepositorioOfertas,
    repositorioPedidos: RepositorioPedidos,
    private val calcularImpacto: CalcularImpactoUseCase,
    private val seleccionarOfertas: SeleccionarOfertasInicioUseCase,
    repositorioNotificaciones: RepositorioNotificaciones? = null
) : ViewModel() {
    private val noLeidasFlow: Flow<Int> =
        repositorioNotificaciones?.contarNoLeidas(RolUsuario.CLIENTE) ?: flowOf(0)

    val uiState: StateFlow<InicioUiState> =
        combine(
            repositorioOfertas.ofertas,
            repositorioPedidos.pedidos,
            noLeidasFlow,
            ::crearEstado
        ).stateIn(
            scope = viewModelScope,
            started = suscripcionPantalla,
            initialValue = crearEstado(
                repositorioOfertas.ofertas.value,
                emptyList(),
                0
            )
        )

    private fun crearEstado(ofertas: List<Oferta>, pedidos: List<Pedido>, noLeidas: Int) =
        InicioUiState(
            impacto = calcularImpacto(pedidos),
            ofertas = seleccionarOfertas(ofertas),
            notificacionesNoLeidas = noLeidas
        )
}
