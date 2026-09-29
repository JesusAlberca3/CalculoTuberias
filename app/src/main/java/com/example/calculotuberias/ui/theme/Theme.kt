package com.example.calculotuberias.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = AzulSobrePrimario,
    primaryContainer = AzulContenedor,
    onPrimaryContainer = AzulSobreContenedor,
    secondary = CianSecundario,
    onSecondary = CianSobreSecundario,
    secondaryContainer = CianContenedor,
    onSecondaryContainer = CianSobreContenedor,
    tertiary = VerdeTerciario,
    onTertiary = VerdeSobreTerciario,
    tertiaryContainer = VerdeContenedor,
    onTertiaryContainer = VerdeSobreContenedor,
    background = FondoClaro,
    onBackground = SobreFondoClaro,
    surface = SuperficieClara,
    onSurface = SobreSuperficieClara,
    surfaceVariant = SuperficieVarianteClara,
    onSurfaceVariant = SobreSuperficieVarianteClara,
    outline = ContornoClaro,
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulPrimarioOscuro,
    onPrimary = AzulSobrePrimarioOscuro,
    primaryContainer = AzulContenedorOscuro,
    onPrimaryContainer = AzulSobreContenedorOscuro,
    secondary = CianSecundarioOscuro,
    onSecondary = CianSobreSecundarioOscuro,
    secondaryContainer = CianContenedorOscuro,
    onSecondaryContainer = CianSobreContenedorOscuro,
    tertiary = VerdeTerciarioOscuro,
    onTertiary = VerdeSobreTerciarioOscuro,
    tertiaryContainer = VerdeContenedorOscuro,
    onTertiaryContainer = VerdeSobreContenedorOscuro,
    background = FondoOscuro,
    onBackground = SobreFondoOscuro,
    surface = SuperficieOscura,
    onSurface = SobreSuperficieOscura,
    surfaceVariant = SuperficieVarianteOscura,
    onSurfaceVariant = SobreSuperficieVarianteOscura,
    outline = ContornoOscuro,
)

/**
 * Tema de la aplicación.
 *
 * El color dinámico de Android 12+ queda desactivado por defecto para conservar
 * la identidad azul institucional; se puede activar con [colorDinamico].
 */
@Composable
fun HidroTheme(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    colorDinamico: Boolean = false,
    content: @Composable () -> Unit,
) {
    val esquema = when {
        colorDinamico && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val contexto = LocalContext.current
            if (temaOscuro) dynamicDarkColorScheme(contexto) else dynamicLightColorScheme(contexto)
        }

        temaOscuro -> EsquemaOscuro
        else -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = esquema,
        typography = Tipografia,
        content = content,
    )
}
