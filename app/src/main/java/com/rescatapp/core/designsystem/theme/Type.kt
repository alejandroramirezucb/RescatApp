package com.rescatapp.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val tipografiaBase = Typography()

val TipografiaRescatApp = tipografiaBase.copy(
    headlineSmall = tipografiaBase.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = tipografiaBase.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        lineHeight = 28.sp,
        letterSpacing = (-0.3).sp
    ),
    titleMedium = tipografiaBase.titleMedium.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = tipografiaBase.titleSmall.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = tipografiaBase.bodyLarge.copy(lineHeight = 24.sp, letterSpacing = 0.sp),
    bodyMedium = tipografiaBase.bodyMedium.copy(lineHeight = 21.sp, letterSpacing = 0.sp),
    bodySmall = tipografiaBase.bodySmall.copy(lineHeight = 18.sp, letterSpacing = 0.1.sp),
    labelLarge = tipografiaBase.labelLarge.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.1.sp
    ),
    labelMedium = tipografiaBase.labelMedium.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.1.sp
    )
)
