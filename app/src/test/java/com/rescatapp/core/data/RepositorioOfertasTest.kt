package com.rescatapp.core.data

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.RolUsuario
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositorioOfertasTest {
    private val api = SupabaseApiFalsa()
    private val repositorio = crearRepositorioOfertas(api = api)

    @Test
    fun sincronizarTraeLasOfertasDelServidorYQuitaLasQueYaNoExisten() = runTest {
        api.ofertas.removeAll { it.id != 14 }

        repositorio.sincronizar()

        assertEquals(listOf(14), repositorio.ofertas.first().map { it.id })
    }

    @Test
    fun elCatalogoOcultaOfertasPausadasAgotadasOVencidas() = runTest {
        api.ofertas.replaceAll {
            when (it.id) {
                14 -> it.copy(estadoPublicacion = "PAUSADA")
                13 -> it.copy(cantidadDisponible = 0)
                12 -> it.copy(fechaRetiro = LocalDate.now(ZoneId.of("America/La_Paz")).minusDays(1))
                else -> it
            }
        }
        repositorio.sincronizar()

        val ids = repositorio.catalogo.first().map { it.id }

        assertTrue(ids.none { it in listOf(14, 13, 12) })
        assertTrue(11 in ids)
    }

    @Test
    fun propiasMuestraSoloLasOfertasDelUsuarioConSesion() = runTest {
        api.ofertas.replaceAll { if (it.id == 14) it.copy(propietarioId = USUARIO_PRUEBA) else it }
        repositorio.sincronizar()

        assertEquals(listOf(14), repositorio.propias.first().map { it.id })
    }

    @Test
    fun filtrarCombinaCategoriaYTexto() = runTest {
        val resultado = repositorio.filtrar(Categoria.COMIDA, "pizza").first()

        assertTrue(resultado.isNotEmpty())
        assertTrue(resultado.all { it.categoria == Categoria.COMIDA })
    }

    @Test
    fun agregarEnviaLaOfertaConElNegocioDelUsuario() = runTest {
        val negocio = crearRepositorioOfertas(
            api = api,
            autenticacion = crearAutenticacion(api, sesionDePrueba(RolUsuario.NEGOCIO))
        )

        negocio.agregar(ofertasDeEjemplo.first())

        assertEquals(1, api.ofertasCreadas.single()["negocio_id"])
        assertEquals("Pack Sorpresa", api.ofertasCreadas.single()["nombre"])
    }

    @Test
    fun cambiarEstadoPausaUnaOfertaActiva() = runTest {
        repositorio.cambiarEstado(ofertasDeEjemplo.first())

        assertEquals("PAUSADA", repositorio.ofertas.first().first { it.id == 14 }.estadoPublicacion)
    }

    @Test
    fun obtenerPorIdEmiteNuloCuandoLaOfertaNoExiste() = runTest {
        assertNull(repositorio.obtenerPorId(99).first())
    }
}
