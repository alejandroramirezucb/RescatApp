package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.designsystem.theme.SobreNaranjaSuave

@Composable
internal fun BannerOportunidades(onExplorar: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(rellenoLateral)
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.horizontalGradient(
                    listOf(SobreNaranjaSuave, Naranja)
                )
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.Eco,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.12f),
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(120.dp)
        )
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "APROVECHA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
            Text(
                text = "Últimas oportunidades",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = "Descubre packs disponibles y ahorra mientras rescatas.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
            OutlinedButton(
                onClick = onExplorar,
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.12f),
                    contentColor = Color.White
                )
            ) {
                Text("Explorar →", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview(name = "Banner naranja", showBackground = true, widthDp = 360)
@Preview(name = "Banner con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun BannerOportunidadesPreview() {
    RescatAppTheme { BannerOportunidades(onExplorar = {}) }
}
