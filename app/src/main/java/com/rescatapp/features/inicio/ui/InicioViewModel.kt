package com.rescatapp.features.inicio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.features.inicio.domain.InicioUiState
import com.rescatapp.features.inicio.domain.SeleccionarOfertasInicioUseCase
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
    private val calcularImpacto: CalcularImpactoUseCase,
    private val seleccionarOfertas: SeleccionarOfertasInicioUseCase =
        SeleccionarOfertasInicioUseCase()
) : ViewModel() {
    val uiState: StateFlow<InicioUiState> = combine(
        repositorioOfertas.ofertas,
        repositorioPedidos.pedidos
    ) { ofertas, pedidos ->
        val seleccion = seleccionarOfertas(ofertas)
        InicioUiState(
            usuario = "Invitado",
            impacto = calcularImpacto(pedidos),
            ofertasDisponibles = seleccion.ofertasDisponibles,
            seEstanAgotando = seleccion.seEstanAgotando
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = run {
            val seleccion = seleccionarOfertas(repositorioOfertas.ofertas.value)
            InicioUiState(
                usuario = "Invitado",
                impacto = calcularImpacto(repositorioPedidos.pedidos.value),
                ofertasDisponibles = seleccion.ofertasDisponibles,
                seEstanAgotando = seleccion.seEstanAgotando
            )
        }
    )
}
