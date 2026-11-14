package com.rescatapp.features.registro.domain

import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.model.ResultadoOperacion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistrarOfertaUseCaseTest {
    private val repositorioOfertas = crearRepositorioOfertas()
    private val registrarOferta = RegistrarOfertaUseCase(repositorioOfertas)

    @Test
    fun registraLaOfertaConvertidaAlInicioDelListado() = runTest {
        val resultado = registrarOferta(camposValidos)

        assertTrue(resultado is ResultadoOperacion.Exito)
        val ofertaNueva = repositorioOfertas.ofertas.first().first()
        assertEquals(15, ofertaNueva.id)
        assertEquals("Pack Salteñas", ofertaNueva.nombre)
        assertEquals(1.5, ofertaNueva.pesoKg, 0.0)
        assertEquals(4, ofertaNueva.cantidadDisponible)
        assertEquals(50, ofertaNueva.porcentajeDescuento)
    }

    @Test
    fun noRegistraCamposQueNoSePuedenConvertir() = runTest {
        val resultado = registrarOferta(camposValidos.copy(pesoKg = "abc"))

        assertTrue(resultado is ResultadoOperacion.Error)
        assertEquals(14, repositorioOfertas.ofertas.first().size)
    }
}
