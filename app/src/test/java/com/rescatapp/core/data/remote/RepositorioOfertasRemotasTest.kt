package com.rescatapp.core.data.remote

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RepositorioOfertasRemotasTest {
    private val servidor = MockWebServer()
    private lateinit var repositorio: RepositorioOfertasRemotas

    private val ofertaJson = """
        {"id":"1","nombre":"Pack Sorpresa","comercio":"Panadería La Central",
        "categoria":"PANADERIA","descripcion":"Panes del día","pesoKg":1.0,
        "precioNormal":45.0,"precioRescate":25.0,"cantidadDisponible":5,
        "horaRetiroDesde":"18:00","horaRetiroHasta":"20:00"}
    """.trimIndent()

    @Before
    fun iniciar() {
        servidor.start()
        val api = Retrofit.Builder()
            .baseUrl(servidor.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
        repositorio = RepositorioOfertasRemotas(api)
    }

    @After
    fun detener() {
        servidor.shutdown()
    }

    @Test
    fun obtenerOfertasRemotasConvierteLaRespuestaEnOfertas() = runTest {
        servidor.enqueue(MockResponse().setBody("[$ofertaJson]"))

        val oferta = repositorio.obtenerOfertasRemotas().first()

        assertEquals("GET", servidor.takeRequest().method)
        assertEquals(1, oferta.id)
        assertEquals(Categoria.PANADERIA, oferta.categoria)
    }

    @Test
    fun crearOfertaEnviaLaOfertaEnElCuerpo() = runTest {
        servidor.enqueue(MockResponse().setBody(ofertaJson))

        repositorio.crearOferta(
            Oferta(
                id = 0,
                nombre = "Pack Sorpresa",
                comercio = "Panadería La Central",
                categoria = Categoria.PANADERIA,
                descripcion = "Panes del día",
                pesoKg = 1.0,
                precioNormal = 45.0,
                precioRescate = 25.0,
                cantidadDisponible = 5,
                horaRetiroDesde = "18:00",
                horaRetiroHasta = "20:00"
            )
        )
        val solicitud = servidor.takeRequest()
        val cuerpo = solicitud.body.readUtf8()

        assertEquals("POST", solicitud.method)
        assertEquals("/ofertas", solicitud.path)
        assertTrue(cuerpo.contains("\"nombre\":\"Pack Sorpresa\""))
        assertTrue(cuerpo.contains("\"categoria\":\"PANADERIA\""))
    }

    @Test
    fun unErrorDelServidorLanzaHttpException() = runTest {
        servidor.enqueue(MockResponse().setResponseCode(500))

        val excepcion = runCatching { repositorio.obtenerOfertasRemotas() }.exceptionOrNull()

        assertEquals(500, (excepcion as HttpException).code())
    }

    @Test
    fun editarOfertaEnviaPutConElIdRemotoYDevuelveLaRespuestaDelServidor() = runTest {
        servidor.enqueue(MockResponse().setBody(ofertaJson.replace("25.0", "20.0")))
        val oferta = com.google.gson.Gson().fromJson(ofertaJson, OfertaDto::class.java).aOferta()

        val actualizada = repositorio.editarOferta(1, oferta.copy(precioRescate = 20.0))

        val solicitud = servidor.takeRequest()
        assertEquals("PUT", solicitud.method)
        assertEquals("/ofertas/1", solicitud.path)
        assertTrue(solicitud.body.readUtf8().contains("\"precioRescate\":20.0"))
        assertEquals(20.0, actualizada.precioRescate, 0.001)
    }

    @Test
    fun eliminarOfertaEnviaDeleteYAceptaUnaRespuestaSinCuerpo() = runTest {
        servidor.enqueue(MockResponse().setResponseCode(204))

        repositorio.eliminarOferta(23)

        val solicitud = servidor.takeRequest()
        assertEquals("DELETE", solicitud.method)
        assertEquals("/ofertas/23", solicitud.path)
    }

    @Test
    fun respuestasInvalidasNoSeConviertenEnOfertasConIdCero() = runTest {
        val respuestas = listOf(
            ofertaJson.replace("\"id\":\"1\"", "\"id\":\"invalido\""),
            ofertaJson.replace("\"id\":\"1\"", "\"id\":null"),
            ofertaJson.replace("PANADERIA", "DESCONOCIDA"),
            "{json inválido"
        )
        for (respuesta in respuestas) {
            servidor.enqueue(MockResponse().setBody("[$respuesta]"))

            val resultado = runCatching { repositorio.obtenerOfertasRemotas() }

            assertTrue(resultado.isFailure)
            assertNull(resultado.getOrNull())
        }
    }

    @Test
    fun erroresHttpDeLasMutacionesSePropaganAlConsumidor() = runTest {
        val oferta = com.google.gson.Gson().fromJson(ofertaJson, OfertaDto::class.java).aOferta()
        val operaciones: List<suspend () -> Unit> = listOf(
            {
                repositorio.crearOferta(oferta)
                Unit
            },
            {
                repositorio.editarOferta(1, oferta)
                Unit
            },
            { repositorio.eliminarOferta(1) }
        )
        for (operacion in operaciones) {
            servidor.enqueue(MockResponse().setResponseCode(503))
            val error = runCatching { operacion() }.exceptionOrNull()
            assertEquals(503, (error as HttpException).code())
        }
    }
}
