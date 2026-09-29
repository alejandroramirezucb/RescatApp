package com.rescatapp.features.registro.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.ResultadoOperacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistrarOfertaUseCaseTest {
    private val repositorioOfertas = RepositorioOfertas()
    private val registrarOferta = RegistrarOfertaUseCase(repositorioOfertas)

    @Test
    fun registraLaOfertaConvertidaAlInicioDelListado() {
        val resultado = registrarOferta(camposValidos)

        assertTrue(resultado is ResultadoOperacion.Exito)
        val ofertaNueva = repositorioOfertas.ofertas.value.first()
        assertEquals(15, ofertaNueva.id)
        assertEquals("Pack Salteñas", ofertaNueva.nombre)
        assertEquals(1.5, ofertaNueva.pesoKg, 0.0)
        assertEquals(4, ofertaNueva.cantidadDisponible)
        assertEquals(50, ofertaNueva.porcentajeDescuento)
    }

    @Test
    fun noRegistraCamposQueNoSePuedenConvertir() {
        val resultado = registrarOferta(camposValidos.copy(pesoKg = "abc"))

        assertTrue(resultado is ResultadoOperacion.Error)
        assertEquals(14, repositorioOfertas.ofertas.value.size)
    }
}
