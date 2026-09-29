package com.rescatapp.features.registro.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.ResultadoOperacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistrarOfertaUseCaseTest {
    private val campos = CamposRegistro(
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

    @Test
    fun registraUnaOfertaValida() {
        val repositorio = RepositorioOfertas()
        val registrar = RegistrarOfertaUseCase(repositorio, ValidarOfertaUseCase())

        val resultado = registrar(campos)

        assertTrue(resultado is ResultadoOperacion.Exito<*>)
        val oferta = resultado.valorExitoso
        assertEquals(15, oferta?.id)
        assertEquals(1.5, oferta?.pesoKg ?: 0.0, 0.0)
        assertEquals(4, oferta?.cantidadDisponible)
        assertEquals(oferta, repositorio.ofertas.value.first())
    }

    @Test
    fun rechazaDatosInvalidosSinGuardar() {
        val repositorio = RepositorioOfertas()
        val registrar = RegistrarOfertaUseCase(repositorio, ValidarOfertaUseCase())

        val resultado = registrar(campos.copy(nombre = ""))

        assertEquals(ResultadoOperacion.Error("Revisa los datos de la oferta"), resultado)
        assertEquals(14, repositorio.ofertas.value.size)
    }
}
