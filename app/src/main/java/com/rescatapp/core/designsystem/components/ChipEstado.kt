package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.NaranjaSuave
import com.rescatapp.core.designsystem.theme.Rojo
import com.rescatapp.core.designsystem.theme.RojoSuave
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.designsystem.theme.VerdeSuave
import com.rescatapp.core.model.EstadoPedido

@Composable
fun ChipEstado(estado: EstadoPedido) {
    val (colorTexto, colorFondo) = when (estado) {
        EstadoPedido.RECOGIDO -> Verde to VerdeSuave
        EstadoPedido.CANCELADO -> Rojo to RojoSuave
        else -> Naranja to NaranjaSuave
    }
    Surface(color = colorFondo, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = estado.etiquetaHistorial,
            style = MaterialTheme.typography.labelMedium,
            color = colorTexto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
