package com.rescatapp.core.data.local

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RescatAppTypeConvertersTest {

    private lateinit var conversores: RescatAppTypeConverters

    @Before
    fun preparar() {
        conversores = RescatAppTypeConverters()
    }

    @Test
    fun convierteEstadoPedidoACadenaYViceversa() {
        EstadoPedido.entries.forEach { estado ->
            val serializado = conversores.fromEstadoPedido(estado)
            assertEquals(estado.name, serializado)
            val deserializado = conversores.toEstadoPedido(serializado)
            assertEquals(estado, deserializado)
        }
    }

    @Test
    fun cadenaDesconocidaParaEstadoPedidoUsaValorPorDefecto() {
        val estado = conversores.toEstadoPedido("VALOR_INEXISTENTE")
        assertEquals(EstadoPedido.RESERVADO, estado)
    }

    @Test
    fun convierteCategoriaACadenaYViceversa() {
        Categoria.entries.forEach { categoria ->
            val serializado = conversores.fromCategoria(categoria)
            assertEquals(categoria.name, serializado)
            val deserializado = conversores.toCategoria(serializado)
            assertEquals(categoria, deserializado)
        }
    }

    @Test
    fun cadenaDesconocidaParaCategoriaUsaValorPorDefecto() {
        val categoria = conversores.toCategoria("OTRA_COSA")
        assertEquals(Categoria.COMIDA, categoria)
    }
}
