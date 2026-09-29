package com.example.calculotuberias.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.calculotuberias.R

/** Poppins ya venía empaquetada en el proyecto; aquí pasa a ser la fuente del sistema. */
val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
)

private fun estilo(
    tamano: Int,
    alturaLinea: Int,
    peso: FontWeight = FontWeight.Normal,
    espaciado: Double = 0.0,
) = TextStyle(
    fontFamily = Poppins,
    fontWeight = peso,
    fontSize = tamano.sp,
    lineHeight = alturaLinea.sp,
    letterSpacing = espaciado.sp,
)

val Tipografia = Typography(
    displaySmall = estilo(36, 44, FontWeight.SemiBold),
    headlineLarge = estilo(32, 40, FontWeight.SemiBold),
    headlineMedium = estilo(28, 36, FontWeight.SemiBold),
    headlineSmall = estilo(24, 32, FontWeight.SemiBold),
    titleLarge = estilo(22, 28, FontWeight.SemiBold),
    titleMedium = estilo(16, 24, FontWeight.SemiBold, 0.15),
    titleSmall = estilo(14, 20, FontWeight.Medium, 0.1),
    bodyLarge = estilo(16, 24, espaciado = 0.5),
    bodyMedium = estilo(14, 20, espaciado = 0.25),
    bodySmall = estilo(12, 16, espaciado = 0.4),
    labelLarge = estilo(14, 20, FontWeight.Medium, 0.1),
    labelMedium = estilo(12, 16, FontWeight.Medium, 0.5),
    labelSmall = estilo(11, 16, FontWeight.Medium, 0.5),
)
