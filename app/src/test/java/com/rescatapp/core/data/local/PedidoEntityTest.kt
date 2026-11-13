package com.rescatapp.core.data.local

import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PedidoEntityTest {

    @Test
    fun mapeaTodosLosCamposEntreEntidadYModeloDeDominio() {
        val original = pedidosDeEjemplo.first()
        val entidad = original.toEntity()

        assertEquals(original.id, entidad.id)
        assertEquals(original.ofertaId, entidad.ofertaId)
        assertEquals(original.nombreOferta, entidad.nombreOferta)
        assertEquals(original.comercio, entidad.comercio)
        assertEquals(original.categoria, entidad.categoria)
        assertEquals(original.precioPagado, entidad.precioPagado, 0.001)
        assertEquals(original.ahorro, entidad.ahorro, 0.001)
        assertEquals(original.pesoKg, entidad.pesoKg, 0.001)
        assertEquals(original.horaRetiroDesde, entidad.horaRetiroDesde)
        assertEquals(original.horaRetiroHasta, entidad.horaRetiroHasta)
        assertEquals(original.estado, entidad.estado)
        assertEquals(original.imagenUri, entidad.imagenUri)

        val reconstruido = entidad.toDomain()
        assertEquals(original, reconstruido)
    }

    @Test
    fun entidadConValoresPorDefectoGeneraIdCeroEImagenNula() {
        val entidad = PedidoEntity(
            ofertaId = 10,
            nombreOferta = "Canasta",
            comercio = "Panadería",
            categoria = Categoria.PANADERIA,
            precioPagado = 15.0,
            ahorro = 10.0,
            pesoKg = 0.5,
            horaRetiroDesde = "18:00",
            horaRetiroHasta = "19:00",
            estado = EstadoPedido.RESERVADO
        )

        assertEquals(0, entidad.id)
        assertNull(entidad.imagenUri)
    }
}
