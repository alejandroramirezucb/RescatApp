package com.rescatapp.features.pedidos.ui

import androidx.lifecycle.ViewModel
import com.rescatapp.features.pedidos.domain.ObtenerPedidosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PedidosViewModel @Inject constructor(obtenerPedidos: ObtenerPedidosUseCase) : ViewModel() {
    val pedidos = obtenerPedidos.obtener()
}
