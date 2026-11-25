package com.rescatapp.core.data.remote

import com.google.gson.Gson
import com.rescatapp.core.data.aDto
import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.core.model.EstadoPagoSimulado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PedidoDtoTest {
    private val gson = Gson()
    private val json = gson.toJsonTree(pedidosDeEjemplo.first().aDto()).asJsonObject

    @Test
    fun servidorMapeaElPagoAnulado() {
        json.addProperty("estado", "CANCELADO")
        json.addProperty("estado_pago_simulado", "ANULADO")
        assertEquals(
            EstadoPagoSimulado.ANULADO,
            gson.fromJson(json, PedidoDto::class.java).toDomain().estadoPagoSimulado
        )
    }

    @Test
    fun servidorAnteriorSinEstadoDePagoConservaLaCompatibilidad() {
        json.remove("estado_pago_simulado")
        assertEquals(
            EstadoPagoSimulado.APROBADO,
            gson.fromJson(json, PedidoDto::class.java).toDomain().estadoPagoSimulado
        )
        json.addProperty("estado", "CANCELADO")
        assertEquals(
            EstadoPagoSimulado.ANULADO,
            gson.fromJson(json, PedidoDto::class.java).toDomain().estadoPagoSimulado
        )
    }

    @Test
    fun jsonDeSupabaseMapeaYNormalizaLosDatosDelCliente() {
        json.addProperty("cliente_nombre", " Ana Pérez ")
        json.addProperty("cliente_telefono", " +591 71234567 ")

        val pedido = gson.fromJson(json, PedidoDto::class.java).toDomain()

        assertEquals("Ana Pérez", pedido.clienteNombre)
        assertEquals("+591 71234567", pedido.clienteTelefono)
    }

    @Test
    fun servidorAnteriorSinTelefonoSigueMapeandoElNombre() {
        json.addProperty("cliente_nombre", "Ana Pérez")
        json.remove("cliente_telefono")

        val pedido = gson.fromJson(json, PedidoDto::class.java).toDomain()

        assertEquals("Ana Pérez", pedido.clienteNombre)
        assertNull(pedido.clienteTelefono)
    }

    @Test
    fun datosAusentesOVaciosSeConviertenEnNulos() {
        json.addProperty("cliente_nombre", "  ")
        json.addProperty("cliente_telefono", "")

        val pedido = gson.fromJson(json, PedidoDto::class.java).toDomain()

        assertNull(pedido.clienteNombre)
        assertNull(pedido.clienteTelefono)
    }
}
