package com.rescatapp.features.pago.domain

import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.pago.model.ComprobantePago
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasarelaPagoSimuladaTest {

    private val pasarela = PasarelaPagoSimulada(esperaMs = 0L)
    private val oferta = ofertasDeEjemplo.first()

    @Test
    fun generarQrParaOfertaCreaDatosConReferenciaYPayloadCorrectos() {
        val datosQr = pasarela.generarQrParaOferta(oferta)

        assertTrue(datosQr.referenciaTransaccion.startsWith("QR-"))
        assertTrue(datosQr.codigoQrPayload.contains("SIMPLE-QR"))
        assertTrue(datosQr.codigoQrPayload.contains(oferta.comercio))
        assertEquals(oferta.precioRescate, datosQr.monto, 0.001)
        assertEquals(oferta.comercio, datosQr.comercio)
        assertEquals(300, datosQr.expiracionSegundos)
    }

    @Test
    fun generarQrConsecutivosProduceReferenciasDistintas() {
        val qr1 = pasarela.generarQrParaOferta(oferta)
        val qr2 = pasarela.generarQrParaOferta(oferta)

        assertNotEquals(qr1.referenciaTransaccion, qr2.referenciaTransaccion)
        assertNotEquals(qr1.codigoQrPayload, qr2.codigoQrPayload)
    }

    @Test
    fun procesarPagoQrAprobadoRetornaComprobante() = runTest {
        val datosQr = pasarela.generarQrParaOferta(oferta)

        val resultado = pasarela.procesarPagoQr(datosQr, aprobado = true)

        val comprobante = (resultado as ResultadoOperacion.Exito).valor as ComprobantePago
        assertEquals(datosQr.referenciaTransaccion, comprobante.referencia)
        assertEquals(datosQr.monto, comprobante.monto, 0.001)
        assertEquals(oferta.comercio, comprobante.comercio)
    }

    @Test
    fun procesarPagoQrRechazadoRetornaErrorConMotivo() = runTest {
        val datosQr = pasarela.generarQrParaOferta(oferta)

        val resultado = pasarela.procesarPagoQr(datosQr, aprobado = false)

        assertTrue((resultado as ResultadoOperacion.Error).mensaje.contains("rechazó"))
    }
}
