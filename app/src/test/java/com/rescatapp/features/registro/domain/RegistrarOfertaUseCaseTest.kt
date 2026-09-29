package com.rescatapp.features.registro.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Categoria
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistrarOfertaUseCaseTest {

    @Test
    fun convierteCamposEInsertaOfertaConNuevoIdYDatosParseados() {
        val repositorio = RepositorioOfertas()
        val totalInicial = repositorio.ofertas.value.size
        val useCase = RegistrarOfertaUseCase(repositorio)

        val campos = CamposRegistro(
            nombre = "Pack Salteñas",
            comercio = "Panadería La Central",
            categoria = Categoria.PANADERIA,
            descripcion = "Salteñas del día",
            pesoKg = "1,5",
            precioNormal = "40",
            precioRescate = "20",
            cantidadDisponible = "4",
            horaRetiroDesde = "09:00",
            horaRetiroHasta = "11:00"
        )

        val ofertaCreada = useCase(campos)

        assertNotNull(ofertaCreada)
        assertEquals(totalInicial + 1, repositorio.ofertas.value.size)

        val ofertaEnRepo = repositorio.obtenerActualPorId(ofertaCreada!!.id)
        assertNotNull(ofertaEnRepo)

        assertEquals("Pack Salteñas", ofertaEnRepo!!.nombre)
        assertEquals("Panadería La Central", ofertaEnRepo.comercio)
        assertEquals(Categoria.PANADERIA, ofertaEnRepo.categoria)
        assertEquals("Salteñas del día", ofertaEnRepo.descripcion)
        assertEquals(1.5, ofertaEnRepo.pesoKg, 0.0)
        assertEquals(40.0, ofertaEnRepo.precioNormal, 0.0)
        assertEquals(20.0, ofertaEnRepo.precioRescate, 0.0)
        assertEquals(4, ofertaEnRepo.cantidadDisponible)
        assertEquals("09:00", ofertaEnRepo.horaRetiroDesde)
        assertEquals("11:00", ofertaEnRepo.horaRetiroHasta)
        assertTrue(ofertaEnRepo.id > 14)
        assertEquals(ofertaCreada, ofertaEnRepo)
    }

    @Test
    fun asignaIdNuevoSecuencialBasadoEnOfertasExistentes() {
        val repositorio = RepositorioOfertas()
        val idMaximoExistente = repositorio.ofertas.value.maxOf { it.id }
        val useCase = RegistrarOfertaUseCase(repositorio)

        val campos = CamposRegistro(
            nombre = "Pack Salteñas",
            comercio = "Panadería La Central",
            categoria = Categoria.PANADERIA,
            descripcion = "Salteñas del día",
            pesoKg = "1.5",
            precioNormal = "40",
            precioRescate = "20",
            cantidadDisponible = "4",
            horaRetiroDesde = "09:00",
            horaRetiroHasta = "11:00"
        )

        val nuevaOferta = useCase(campos)
        assertNotNull(nuevaOferta)
        assertEquals(idMaximoExistente + 1, nuevaOferta!!.id)
    }

    @Test
    fun siDatosSonInvalidos_noAgregaAlRepositorioYRetornaNull() {
        val repositorio = RepositorioOfertas()
        val totalInicial = repositorio.ofertas.value.size
        val useCase = RegistrarOfertaUseCase(repositorio)

        val camposInvalidos = CamposRegistro(
            nombre = "",
            comercio = "Panadería La Central",
            categoria = Categoria.PANADERIA
        )

        val resultado = useCase(camposInvalidos)

        assertNull(resultado)
        assertEquals(totalInicial, repositorio.ofertas.value.size)
    }
}
