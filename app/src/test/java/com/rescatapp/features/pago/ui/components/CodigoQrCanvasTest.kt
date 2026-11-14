package com.rescatapp.features.pago.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CodigoQrCanvasTest {

    @Test
    fun generarMatrizQrProduceDimensionExactaYFindersCorrectos() {
        val dimension = 25
        val payload = "SIMPLE-QR|BOL|RESCATAPP|1234"
        val matriz = generarMatrizQr(payload, dimension)

        assertEquals(dimension, matriz.size)
        assertEquals(dimension, matriz[0].size)

        // Verificar finder pattern esquina superior izquierda (0,0)
        // Borde exterior 7x7 debe estar activo
        assertTrue(matriz[0][0])
        assertTrue(matriz[0][6])
        assertTrue(matriz[6][0])
        assertTrue(matriz[6][6])

        // Fila 1 y columna 1 (anillo blanco interior) debe ser falso
        assertFalse(matriz[1][1])
        assertFalse(matriz[1][5])

        // Centro 3x3 (filas 2..4, columnas 2..4) debe estar activo
        assertTrue(matriz[3][3])
        assertTrue(matriz[2][2])

        // Centro de la matriz (insignia central) debe estar despejado para el badge
        assertFalse(matriz[12][12])
    }

    @Test
    fun generarMatrizQrEsDeterministaParaElMismoPayload() {
        val payload = "SIMPLE-QR|TEST"
        val m1 = generarMatrizQr(payload, 25)
        val m2 = generarMatrizQr(payload, 25)

        for (r in 0 until 25) {
            for (c in 0 until 25) {
                assertEquals(m1[r][c], m2[r][c])
            }
        }
    }

    @Test
    fun generarMatrizQrProduceDiferenciasParaPayloadsDistintos() {
        val m1 = generarMatrizQr("PAYLOAD_UNO", 25)
        val m2 = generarMatrizQr("PAYLOAD_DOS", 25)

        var diferencias = 0
        for (r in 0 until 25) {
            for (c in 0 until 25) {
                if (m1[r][c] != m2[r][c]) {
                    diferencias++
                }
            }
        }
        assertTrue("Los payloads distintos deben generar patrones QR diferentes", diferencias > 0)
    }
}
