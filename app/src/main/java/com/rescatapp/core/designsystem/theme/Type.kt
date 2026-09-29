package com.rescatapp.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

private val tipografiaBase = Typography()

val TipografiaRescatApp = tipografiaBase.copy(
    headlineSmall = tipografiaBase.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
    titleLarge = tipografiaBase.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = tipografiaBase.titleMedium.copy(fontWeight = FontWeight.Bold),
    titleSmall = tipografiaBase.titleSmall.copy(fontWeight = FontWeight.Bold),
    labelMedium = tipografiaBase.labelMedium.copy(fontWeight = FontWeight.Bold)
)
