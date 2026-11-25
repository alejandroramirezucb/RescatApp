package com.rescatapp.core.designsystem.components

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.rescatapp.core.util.aHoraOrNull
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SelectorHoraOferta(
    etiqueta: String,
    valor: String,
    onValorCambiado: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    val contexto = LocalContext.current
    Column(modifier = modifier) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(
            onClick = {
                val hora = valor.aHoraOrNull() ?: LocalTime.of(18, 0)
                TimePickerDialog(
                    contexto,
                    { _, horas, minutos ->
                        onValorCambiado(
                            LocalTime.of(horas, minutos).format(
                                DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)
                            )
                        )
                    },
                    hora.hour,
                    hora.minute,
                    true
                ).apply { setTitle(etiqueta) }.show()
            },
            modifier = Modifier.fillMaxWidth().semantics {
                contentDescription = "$etiqueta: ${valor.ifBlank { "Seleccionar hora" }}"
            }
        ) {
            Text(valor.ifBlank { "Seleccionar hora" })
        }
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
