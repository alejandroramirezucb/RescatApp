package com.rescatapp.core.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.mock.pedidosDeEjemplo
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.detalle.domain.ReservarOfertaUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class CalcularImpactoUseCaseTest {
    private val calcular = CalcularImpactoUseCase()

    @Test
    fun listaVaciaRetornaCeroRescatesCeroAhorradoYCeroKg() {
        val impacto = calcular(emptyList())

        assertEquals(0, impacto.rescates)
        assertEquals(0.0, impacto.ahorrado, 0.0)
        assertEquals(0.0, impacto.kgAprovechados, 0.0)
    }

    @Test
    fun datosDeEjemploRetornaCuatroRescates102AhorradoY3Punto6Kg() {
        val impacto = calcular(pedidosDeEjemplo)

        assertEquals(4, impacto.rescates)
        assertEquals(102.0, impacto.ahorrado, 0.0)
        assertEquals(3.6, impacto.kgAprovechados, 0.001)
    }

    @Test
    fun pedidoSandwichComboCanceladoNoSeContabiliza() {
        val sandwichCombo = pedidosDeEjemplo.find { it.nombreOferta == "Sándwich Combo" }!!
        assertEquals(EstadoPedido.CANCELADO, sandwichCombo.estado)

        val impacto = calcular(pedidosDeEjemplo)

        assertEquals(4, impacto.rescates)
        assertEquals(102.0, impacto.ahorrado, 0.0)
        assertEquals(3.6, impacto.kgAprovechados, 0.001)
    }

    @Test
    fun reservaPackCroissantsAumentaImpactoA5Rescates117AhorradoY4Punto2Kg() {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val resultado = ReservarOfertaUseCase(ofertas, pedidos)(13)

        val impacto = calcular(pedidos.pedidos.value)

        assertEquals(5, impacto.rescates)
        assertEquals(117.0, impacto.ahorrado, 0.0)
        assertEquals(4.2, impacto.kgAprovechados, 0.001)
    }

    @Test
    fun cancelarPedidoVuelveAlImpactoAnterior() {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val resultado = ReservarOfertaUseCase(ofertas, pedidos)(13)
        val pedidoCreado = (resultado as ResultadoOperacion.Exito).valor

        assertEquals(5, calcular(pedidos.pedidos.value).rescates)

        pedidos.actualizar(pedidoCreado.copy(estado = EstadoPedido.CANCELADO))

        val impacto = calcular(pedidos.pedidos.value)
        assertEquals(4, impacto.rescates)
        assertEquals(102.0, impacto.ahorrado, 0.0)
        assertEquals(3.6, impacto.kgAprovechados, 0.001)
    }
}
