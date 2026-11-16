package com.rescatapp.features.pago.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.random.Random

private const val DIMENSION_QR = 25

@Composable
fun CodigoQrCanvas(payload: String) {
    val matriz = remember(payload) { generarMatrizQr(payload, DIMENSION_QR) }
    Canvas(
        modifier = Modifier
            .size(210.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        val lado = size.width / DIMENSION_QR
        for (fila in 0 until DIMENSION_QR) {
            for (columna in 0 until DIMENSION_QR) {
                if (matriz[fila][columna]) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(columna * lado, fila * lado),
                        size = Size(lado, lado)
                    )
                }
            }
        }
    }
}

internal fun generarMatrizQr(payload: String, dimension: Int): Array<BooleanArray> {
    val aleatorio = Random(payload.hashCode())
    val centro = dimension / 2
    return Array(dimension) { fila ->
        BooleanArray(dimension) { columna ->
            val filaEsquina = if (fila < 8) fila else dimension - 1 - fila
            val columnaEsquina = if (columna < 8) columna else dimension - 1 - columna
            when {
                filaEsquina < 8 && columnaEsquina < 8 && !(fila >= 8 && columna >= 8) ->
                    esModuloEsquina(filaEsquina, columnaEsquina)

                abs(fila - centro) <= 2 && abs(columna - centro) <= 2 -> false

                else -> aleatorio.nextBoolean()
            }
        }
    }
}

private fun esModuloEsquina(fila: Int, columna: Int): Boolean {
    if (fila == 7 || columna == 7) return false
    val borde = fila == 0 || fila == 6 || columna == 0 || columna == 6
    val centro = fila in 2..4 && columna in 2..4
    return borde || centro
}
