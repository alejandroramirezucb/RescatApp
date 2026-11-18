package com.rescatapp.core.data.supabase

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.gson.JsonParser
import com.rescatapp.core.model.ResultadoOperacion
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SupabaseConexionTest {
    private val servidor = MockWebServer()
    private val almacen = MemoriaCredenciales()
    private val cache = MemoriaCache()
    private lateinit var conexion: SupabaseConexion

    @Before fun preparar() {
        servidor.start()
        conexion = SupabaseConexion(
            ApplicationProvider.getApplicationContext<Context>(),
            almacen,
            cache,
            SupabaseHttp(servidor.url("/").toString(), "sb_publishable_prueba")
        )
    }

    @After fun terminar() {
        servidor.shutdown()
    }

    @Test fun cambiarCuentaYLuegoSalirNoExponePedidosAnteriores() = runTest {
        conexion.inicializar()
        encolarLogin("cliente-a", pedidos = "[$PEDIDO]")
        assertTrue(conexion.login("a@example.com", "contrasena") is ResultadoOperacion.Exito)
        assertEquals(listOf(14), conexion.pedidos.first().map { it.id })
        encolarLogin("cliente-b")
        assertTrue(conexion.login("b@example.com", "contrasena") is ResultadoOperacion.Exito)
        assertTrue(conexion.pedidos.first().isEmpty())
        assertEquals("cliente-b", conexion.sesion.value?.usuarioId)
        encolar("{}")
        conexion.logout()
        assertNull(conexion.sesion.value)
        assertTrue(conexion.pedidos.first().isEmpty())
        assertNull(almacen.leer("sesion"))
    }

    @Test fun reservaReintentadaReutilizaSolicitudYUsaRpcSinModificarStockDesdeAndroid() = runTest {
        conexion.inicializar()
        encolarLogin("cliente-a")
        conexion.login("a@example.com", "contrasena")
        repeat(6) { servidor.takeRequest(1, TimeUnit.SECONDS) }
        servidor.enqueue(MockResponse().setResponseCode(503).setBody("{}"))
        assertTrue(conexion.reservar(5) is ResultadoOperacion.Error)
        val intento = servidor.takeRequest(1, TimeUnit.SECONDS)!!
        encolar("[$PEDIDO]")
        encolar("[]")
        encolar("[$PEDIDO]")
        encolar("[]")
        assertTrue(conexion.reservar(5) is ResultadoOperacion.Exito)
        val reintento = servidor.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("/rest/v1/rpc/reservar_oferta", intento.path)
        assertEquals("POST", intento.method)
        assertEquals(
            JsonParser.parseString(intento.body.readUtf8()),
            JsonParser.parseString(reintento.body.readUtf8())
        )
        assertEquals("sb_publishable_prueba", intento.getHeader("apikey"))
        assertEquals("Bearer jwt-cliente-a", intento.getHeader("Authorization"))
        assertEquals("#APV-14", conexion.pedidos.first().single().codigoRescate)
    }

    @Test fun eliminarOfertaRetiraElCatalogoYConservaElPedido() = runTest {
        conexion.inicializar()
        encolarLogin("cliente-a", ofertas = "[$OFERTA]", pedidos = "[$PEDIDO]")
        conexion.login("a@example.com", "contrasena")
        assertEquals(1, conexion.ofertas.first().size)
        repeat(6) { servidor.takeRequest(1, TimeUnit.SECONDS) }
        encolar("[]")
        encolar("[]")
        encolar("[$PEDIDO]")
        encolar("[]")
        conexion.eliminarOferta(5)
        assertEquals(
            "/rest/v1/rpc/eliminar_oferta",
            servidor.takeRequest(1, TimeUnit.SECONDS)!!.path
        )
        assertTrue(conexion.ofertas.first().isEmpty())
        assertEquals(14, conexion.pedidos.first().single().id)
    }

    @Test fun credencialesInvalidasNoAbrenSesion() = runTest {
        conexion.inicializar()
        servidor.enqueue(
            MockResponse().setResponseCode(400).setBody("{\"msg\":\"Invalid login credentials\"}")
        )
        assertTrue(conexion.login("a@example.com", "incorrecta") is ResultadoOperacion.Error)
        assertNull(conexion.sesion.value)
    }

    @Test fun unJwtRechazadoSeRenuevaYReintentaLaLectura() = runTest {
        conexion.inicializar()
        encolarLogin("cliente-a")
        conexion.login("a@example.com", "contrasena")
        repeat(6) { servidor.takeRequest(1, TimeUnit.SECONDS) }
        servidor.enqueue(MockResponse().setResponseCode(401).setBody("{}"))
        encolar(
            """{"access_token":"jwt-nuevo","refresh_token":"refresh-nuevo","expires_in":3600}"""
        )
        encolar("[]")
        encolar("[]")
        encolar("[]")
        conexion.sincronizar()
        servidor.takeRequest(1, TimeUnit.SECONDS)
        val renovacion = servidor.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("/auth/v1/token?grant_type=refresh_token", renovacion.path)
        val reintento = servidor.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("Bearer jwt-nuevo", reintento.getHeader("Authorization"))
        assertTrue(almacen.leer("sesion")!!.contains("refresh-nuevo"))
        assertNull(conexion.errorSincronizacion.value)
    }

    @Test fun renovacionRevocadaLimpiaSesionYDatosPrivados() = runTest {
        conexion.inicializar()
        encolarLogin("cliente-a", pedidos = "[$PEDIDO]")
        conexion.login("a@example.com", "contrasena")
        servidor.enqueue(MockResponse().setResponseCode(401).setBody("{}"))
        servidor.enqueue(MockResponse().setResponseCode(400).setBody("{}"))
        conexion.sincronizar()
        assertNull(conexion.sesion.value)
        assertTrue(conexion.pedidos.first().isEmpty())
        assertNull(almacen.leer("sesion"))
    }

    @Test fun registroConCorreoPendienteNoAbreSesionYEnviaMetadatosDeNegocio() = runTest {
        conexion.inicializar()
        encolar("""{"id":"nuevo","email":"nuevo@example.com"}""")
        val resultado = conexion.registrar(
            com.rescatapp.features.auth.domain.model.DatosRegistro(
                nombre = "Responsable",
                correo = "nuevo@example.com",
                contrasena = "Contrasena123!",
                rol = com.rescatapp.core.model.RolUsuario.NEGOCIO,
                nombreNegocio = "Mi negocio"
            )
        )
        assertTrue(resultado is ResultadoOperacion.Exito && resultado.valor is RegistroPendiente)
        assertNull(conexion.sesion.value)
        val registro = JsonParser.parseString(
            servidor.takeRequest(1, TimeUnit.SECONDS)!!.body.readUtf8()
        ).asJsonObject
        assertEquals("NEGOCIO", registro.getAsJsonObject("data").get("rol").asString)
        assertEquals("Mi negocio", registro.getAsJsonObject("data").get("nombreNegocio").asString)
        assertTrue(registro.get("code_challenge").asString.isNotBlank())
    }

    private fun encolarLogin(id: String, ofertas: String = "[]", pedidos: String = "[]") {
        encolar("""{"access_token":"jwt-$id","refresh_token":"refresh-$id","expires_in":3600}""")
        encolar("""{"id":"$id","email":"$id@example.com"}""")
        encolar("""[{"id":"$id","nombre":"Cliente","rol":"CLIENTE"}]""")
        encolar(ofertas)
        encolar(pedidos)
        encolar("[]")
    }

    private fun encolar(json: String) {
        servidor.enqueue(MockResponse().setHeader("Content-Type", "application/json").setBody(json))
    }

    private companion object {
        const val PEDIDO = """
{
  "id": 14,
  "oferta_id": 5,
  "nombre_oferta": "Pack",
  "comercio": "Negocio",
  "categoria": "PANADERIA",
  "precio_pagado": 20,
  "ahorro": 10,
  "peso_kg": 1,
  "fecha_retiro": "2027-01-02",
  "hora_retiro_desde": "18:00:00",
  "hora_retiro_hasta": "20:00:00",
  "estado": "RESERVADO",
  "codigo_rescate": "#APV-14",
  "creado_at": "2027-01-01T23:00:00+00:00"
}
"""
        const val OFERTA = """
{
  "id": 5,
  "nombre": "Pack",
  "categoria": "PANADERIA",
  "descripcion": "Pan",
  "peso_kg": 1,
  "precio_normal": 30,
  "precio_rescate": 20,
  "cantidad_disponible": 1,
  "fecha_retiro": "2027-01-02",
  "hora_retiro_desde": "18:00:00",
  "hora_retiro_hasta": "20:00:00",
  "estado": "ACTIVA",
  "negocios": {
    "nombre": "Negocio",
    "propietario_id": "negocio-a"
  }
}
"""
    }
}

private class MemoriaCredenciales : AlmacenCredenciales {
    private val valores = mutableMapOf<String, String>()
    override fun leer(nombre: String): String? = valores[nombre]
    override fun guardar(nombre: String, valor: String?) {
        if (valor ==
            null
        ) {
            valores.remove(nombre)
        } else {
            valores[nombre] = valor
        }
    }
}

private class MemoriaCache : CacheDeCuenta {
    private val valores = mutableMapOf<Pair<String, String>, String>()
    override suspend fun leer(cuenta: String, recurso: String): String? = valores[cuenta to recurso]
    override suspend fun guardar(cuenta: String, recurso: String, json: String) {
        valores[
            cuenta to
                recurso
        ] =
            json
    }
    override suspend fun borrar(cuenta: String) {
        valores.keys.removeAll { it.first == cuenta }
    }
}
