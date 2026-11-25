package com.rescatapp.core.data

import com.rescatapp.core.model.Carrito
import com.rescatapp.core.model.ItemCarrito
import com.rescatapp.core.model.Oferta
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class RepositorioCarrito @Inject constructor() {
    private val estado = MutableStateFlow(Carrito())
    val carrito: StateFlow<Carrito> = estado.asStateFlow()

    fun agregar(oferta: Oferta) {
        cambiarCantidad(oferta, estado.value.cantidadDe(oferta.id) + 1)
    }

    fun cambiarCantidad(oferta: Oferta, cantidad: Int) {
        val nueva = cantidad.coerceIn(0, oferta.cantidadDisponible)
        estado.update { carrito ->
            val items = if (carrito.cantidadDe(oferta.id) > 0) {
                carrito.items.map {
                    if (it.oferta.id == oferta.id) ItemCarrito(oferta, nueva) else it
                }
            } else {
                carrito.items + ItemCarrito(oferta, nueva)
            }
            Carrito(items.filter { it.cantidad > 0 })
        }
    }

    fun eliminar(ofertaId: Int) {
        estado.update { carrito -> Carrito(carrito.items.filterNot { it.oferta.id == ofertaId }) }
    }
}
