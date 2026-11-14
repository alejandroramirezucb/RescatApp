package com.rescatapp.features.pago.domain

import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.features.pago.model.EstadoPago
import com.rescatapp.features.pago.model.MetodoPago
import com.rescatapp.features.pago.model.ResultadoPago
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PasarelaPagoSimuladaTest {

    private lateinit var pasarela: PasarelaPagoSimulada
    private val oferta = ofertasDeEjemplo.first()

    @Before
    fun setUp() {
        pasarela = PasarelaPagoSimulada()
    }

    @Test
    fun generarQrParaOfertaCreaDatosConReferenciaYPayloadCorrectos() {
        val datosQr = pasarela.generarQrParaOferta(oferta)

        assertTrue(datosQr.referenciaTransaccion.startsWith("QR-"))
        assertTrue(datosQr.codigoQrPayload.contains("SIMPLE-QR"))
        assertTrue(datosQr.codigoQrPayload.contains(oferta.comercio))
        assertEquals(oferta.precioRescate, datosQr.monto, 0.001)
        assertEquals(oferta.comercio, datosQr.comercio)
        assertEquals(300, datosQr.expiracionSegundos)
        assertTrue(!datosQr.estaExpirado)
    }

    @Test
    fun generarQrConsecutivosProduceReferenciasDistintas() {
        val qr1 = pasarela.generarQrParaOferta(oferta)
        val qr2 = pasarela.generarQrParaOferta(oferta)

        assertNotEquals(qr1.referenciaTransaccion, qr2.referenciaTransaccion)
        assertNotEquals(qr1.codigoQrPayload, qr2.codigoQrPayload)
    }

    @Test
    fun procesarPagoQrExitosoRetornaComprobanteAprobado() = runTest {
        val datosQr = pasarela.generarQrParaOferta(oferta)
        val resultado = pasarela.procesarPagoQr(datosQr, simularExito = true, tiempoEsperaMs = 0L)

        assertTrue(resultado is ResultadoPago.Exito)
        val comprobante = (resultado as ResultadoPago.Exito).comprobante
        assertEquals(datosQr.referenciaTransaccion, comprobante.referencia)
        assertEquals(datosQr.monto, comprobante.monto, 0.001)
        assertEquals(MetodoPago.QR_SIMPLE, comprobante.metodoPago)
        assertEquals(EstadoPago.APROBADO, comprobante.estado)
        assertEquals(oferta.comercio, comprobante.comercio)
    }

    @Test
    fun procesarPagoQrRechazadoRetornaErrorConMotivo() = runTest {
        val datosQr = pasarela.generarQrParaOferta(oferta)
        val resultado = pasarela.procesarPagoQr(datosQr, simularExito = false, tiempoEsperaMs = 0L)

        assertTrue(resultado is ResultadoPago.Rechazado)
        val rechazo = resultado as ResultadoPago.Rechazado
        assertTrue(rechazo.motivo.contains("rechazó"))
    }

    @Test
    fun procesarPagoQrExpiradoRetornaErrorPorExpiracion() = runTest {
        val datosQr = pasarela.generarQrParaOferta(oferta, expiracionSegundos = 0)
        assertTrue(datosQr.estaExpirado)

        val resultado = pasarela.procesarPagoQr(datosQr, simularExito = true, tiempoEsperaMs = 0L)
        assertTrue(resultado is ResultadoPago.Rechazado)
        val rechazo = resultado as ResultadoPago.Rechazado
        assertTrue(rechazo.motivo.contains("expirado"))
    }

    @Test
    fun procesarPagoEfectivoGeneraComprobanteConMetodoEfectivo() = runTest {
        val resultado = pasarela.procesarPagoEfectivo(oferta, tiempoEsperaMs = 0L)

        assertTrue(resultado is ResultadoPago.Exito)
        val comprobante = (resultado as ResultadoPago.Exito).comprobante
        assertEquals(MetodoPago.EFECTIVO, comprobante.metodoPago)
        assertEquals(oferta.precioRescate, comprobante.monto, 0.001)
        assertEquals(EstadoPago.APROBADO, comprobante.estado)
    }
}
