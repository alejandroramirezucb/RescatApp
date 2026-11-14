package com.rescatapp.features.pago.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val QR_DIMENSION = 25
private val ColorQrModulo = Color(0xFF111827)
private val ColorQrFondo = Color.White

@Composable
fun CodigoQrCanvas(payload: String, modifier: Modifier = Modifier, tamano: Dp = 220.dp) {
    val matriz = remember(payload) { generarMatrizQr(payload, QR_DIMENSION) }

    Box(
        modifier = modifier
            .size(tamano)
            .clip(RoundedCornerShape(16.dp))
            .background(ColorQrFondo)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .semantics {
                contentDescription = "Código QR de pago para transferencia bancaria"
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(tamano - 28.dp)) {
            val tamanoModulo = size.width / QR_DIMENSION

            for (fila in 0 until QR_DIMENSION) {
                for (col in 0 until QR_DIMENSION) {
                    if (matriz[fila][col]) {
                        drawRect(
                            color = ColorQrModulo,
                            topLeft = Offset(col * tamanoModulo, fila * tamanoModulo),
                            size = Size(tamanoModulo, tamanoModulo)
                        )
                    }
                }
            }

            // Insignia central decorativa
            val badgeSize = tamanoModulo * 5f
            val badgeOffset = Offset((size.width - badgeSize) / 2f, (size.height - badgeSize) / 2f)
            drawRoundRect(
                color = ColorQrFondo,
                topLeft = badgeOffset,
                size = Size(badgeSize, badgeSize),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF16A34A),
                topLeft = badgeOffset,
                size = Size(badgeSize, badgeSize),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
        }

        Text(
            text = "QR",
            color = Color(0xFF16A34A),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

internal fun generarMatrizQr(payload: String, dimension: Int): Array<BooleanArray> {
    val matriz = Array(dimension) { BooleanArray(dimension) }
    val reservado = Array(dimension) { BooleanArray(dimension) }

    fun marcarFinder(inicioFila: Int, inicioCol: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                val rf = inicioFila + r
                val cf = inicioCol + c
                reservado[rf][cf] = true
                val esBorde = r == 0 || r == 6 || c == 0 || c == 6
                val esCentro = r in 2..4 && c in 2..4
                matriz[rf][cf] = esBorde || esCentro
            }
        }
        for (r in -1..7) {
            for (c in -1..7) {
                val rf = inicioFila + r
                val cf = inicioCol + c
                if (rf in 0 until dimension && cf in 0 until dimension) {
                    reservado[rf][cf] = true
                }
            }
        }
    }

    marcarFinder(0, 0)
    marcarFinder(0, dimension - 7)
    marcarFinder(dimension - 7, 0)

    for (i in 8 until dimension - 8) {
        reservado[6][i] = true
        matriz[6][i] = (i % 2 == 0)
        reservado[i][6] = true
        matriz[i][6] = (i % 2 == 0)
    }

    val centro = dimension / 2
    for (r in (centro - 2)..(centro + 2)) {
        for (c in (centro - 2)..(centro + 2)) {
            reservado[r][c] = true
            matriz[r][c] = false
        }
    }

    val digest = java.security.MessageDigest.getInstance("SHA-256")
        .digest(payload.toByteArray(Charsets.UTF_8))
    for (r in 0 until dimension) {
        for (c in 0 until dimension) {
            if (!reservado[r][c]) {
                val bitIndex = (r * dimension + c) % 256
                val bytePos = bitIndex / 8
                val bitPos = bitIndex % 8
                matriz[r][c] = ((digest[bytePos].toInt() ushr bitPos) and 1) == 1
            }
        }
    }

    return matriz
}
