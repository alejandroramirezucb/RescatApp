package com.rescatapp.core.designsystem.components

import android.app.DatePickerDialog
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
import com.rescatapp.core.util.aFechaRetiroValidaOrNull
import com.rescatapp.core.util.hoyEnBolivia
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SelectorFechaOferta(
    valor: String,
    onValorCambiado: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    val contexto = LocalContext.current
    val texto = runCatching {
        LocalDate.parse(valor).format(DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ROOT))
    }.getOrDefault("Seleccionar fecha")
    Column(modifier = modifier) {
        Text("Fecha de retiro", style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(
            onClick = {
                val hoy = hoyEnBolivia()
                val inicial = valor.aFechaRetiroValidaOrNull(hoy) ?: hoy
                DatePickerDialog(
                    contexto,
                    { _, anio, mes, dia ->
                        onValorCambiado(LocalDate.of(anio, mes + 1, dia).toString())
                    },
                    inicial.year,
                    inicial.monthValue - 1,
                    inicial.dayOfMonth
                ).apply {
                    datePicker.minDate = hoy.atStartOfDay(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()
                }.show()
            },
            modifier = Modifier.fillMaxWidth().semantics {
                contentDescription = "Fecha de retiro: $texto"
            }
        ) {
            Text(texto)
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
