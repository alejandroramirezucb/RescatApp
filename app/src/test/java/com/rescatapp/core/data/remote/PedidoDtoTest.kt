package com.rescatapp.core.data.remote

import com.google.gson.Gson
import com.rescatapp.core.data.aDto
import com.rescatapp.core.data.pedidosDeEjemplo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PedidoDtoTest {
    private val gson = Gson()
    private val json = gson.toJsonTree(pedidosDeEjemplo.first().aDto()).asJsonObject

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
