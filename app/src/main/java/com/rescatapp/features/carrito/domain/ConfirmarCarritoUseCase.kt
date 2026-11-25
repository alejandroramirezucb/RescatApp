package com.rescatapp.features.carrito.domain

import com.rescatapp.core.data.RepositorioCarrito
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.ResultadoOperacion
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class ConfirmarCarritoUseCase @Inject constructor(
    private val repositorioCarrito: RepositorioCarrito,
    private val repositorioOfertas: RepositorioOfertas,
    private val repositorioPedidos: RepositorioPedidos
) {
    suspend operator fun invoke(): ResultadoOperacion {
        runCatching { repositorioOfertas.sincronizar() }
        val carrito = repositorioCarrito.carrito.value
        carrito.items.forEach { item ->
            val oferta = repositorioOfertas.obtenerPorId(item.oferta.id).first()
            val disponible = oferta?.cantidadDisponible ?: 0
            if (item.cantidad > disponible) {
                return ResultadoOperacion.Error(
                    "No hay stock suficiente de ${item.oferta.nombre} (quedan $disponible)."
                )
            }
        }
        carrito.items.forEach { item ->
            repeat(item.cantidad) { reservadas ->
                val resultado = repositorioPedidos.reservar(item.oferta.id)
                if (resultado is ResultadoOperacion.Error) {
                    repositorioCarrito.cambiarCantidad(item.oferta, item.cantidad - reservadas)
                    runCatching { repositorioOfertas.sincronizar() }
                    return resultado
                }
            }
            repositorioCarrito.eliminar(item.oferta.id)
        }
        runCatching { repositorioOfertas.sincronizar() }
        return ResultadoOperacion.Exito(carrito.unidades)
    }
}
