package com.example.calculotuberias.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calculotuberias.domain.RegimenCanal
import com.example.calculotuberias.domain.RegimenFlujo
import com.example.calculotuberias.ui.theme.RegimenCritico
import com.example.calculotuberias.ui.theme.RegimenSubcritico
import com.example.calculotuberias.ui.theme.RegimenSupercritico

/** Una línea de resultado: etiqueta, valor y una nota opcional. */
data class Renglon(
    val etiqueta: String,
    val valor: String,
    val destacado: Boolean = false,
    val nota: String? = null,
)

/**
 * Panel de resultados con tres estados explícitos: vacío, error y éxito.
 *
 * La versión anterior mostraba `0.000` o cadenas vacías cuando la entrada era
 * inválida, sin distinguir "no he calculado nada" de "los datos están mal".
 */
@Composable
fun PanelResultados(
    renglones: List<Renglon>,
    modifier: Modifier = Modifier,
    error: String? = null,
    advertencias: List<String> = emptyList(),
    mensajeVacio: String = "Introduce los datos y pulsa calcular.",
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when {
            error != null -> TarjetaMensaje(
                texto = error,
                icono = Icons.Default.ErrorOutline,
                contenedor = MaterialTheme.colorScheme.errorContainer,
                sobreContenedor = MaterialTheme.colorScheme.onErrorContainer,
            )

            renglones.isEmpty() -> TarjetaMensaje(
                texto = mensajeVacio,
                icono = Icons.Default.Info,
                contenedor = MaterialTheme.colorScheme.surfaceVariant,
                sobreContenedor = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            else -> TarjetaValores(renglones)
        }

        AnimatedVisibility(visible = advertencias.isNotEmpty() && error == null) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                advertencias.forEach {
                    TarjetaMensaje(
                        texto = it,
                        icono = Icons.Default.WarningAmber,
                        contenedor = MaterialTheme.colorScheme.tertiaryContainer,
                        sobreContenedor = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaValores(renglones: List<Renglon>) {
    val portapapeles = LocalClipboardManager.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Resultados",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = {
                        val texto = renglones.joinToString("\n") { "${it.etiqueta}: ${it.valor}" }
                        portapapeles.setText(AnnotatedString(texto))
                    },
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copiar resultados",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
            )

            renglones.forEach { FilaResultado(it) }
        }
    }
}

@Composable
private fun FilaResultado(renglon: Renglon) {
    val colorTexto = MaterialTheme.colorScheme.onPrimaryContainer
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                renglon.etiqueta,
                style = MaterialTheme.typography.bodyMedium,
                color = colorTexto.copy(alpha = 0.85f),
                modifier = Modifier.weight(1f),
            )
            Text(
                renglon.valor,
                style = if (renglon.destacado) {
                    MaterialTheme.typography.titleLarge
                } else {
                    MaterialTheme.typography.bodyLarge
                },
                color = colorTexto,
                textAlign = TextAlign.End,
            )
        }
        renglon.nota?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = colorTexto.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun TarjetaMensaje(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    contenedor: Color,
    sobreContenedor: Color,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = contenedor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(icono, contentDescription = null, tint = sobreContenedor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(12.dp))
            Text(texto, style = MaterialTheme.typography.bodyMedium, color = sobreContenedor)
        }
    }
}

/** Texto descriptivo del régimen de flujo en conductos. */
fun EtiquetaRegimenFlujo(regimen: RegimenFlujo): String = when (regimen) {
    RegimenFlujo.LAMINAR -> "Laminar (Re < 2300)"
    RegimenFlujo.TRANSICION -> "Transición (2300 < Re < 4000)"
    RegimenFlujo.TURBULENTO -> "Turbulento (Re > 4000)"
}

fun colorRegimenCanal(regimen: RegimenCanal): Color = when (regimen) {
    RegimenCanal.SUBCRITICO -> RegimenSubcritico
    RegimenCanal.CRITICO -> RegimenCritico
    RegimenCanal.SUPERCRITICO -> RegimenSupercritico
}
