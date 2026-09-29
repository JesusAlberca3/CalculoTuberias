package com.example.calculotuberias.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calculotuberias.BuildConfig
import com.example.calculotuberias.ui.components.PantallaCalculo

/**
 * Documentación dentro de la app.
 *
 * El texto sigue a docs/GUIA_DE_USO.md, recortado para leerse en el teléfono.
 * Las deducciones y las tablas completas viven en el repositorio.
 */
@Composable
fun DocumentacionScreen(onVolver: () -> Unit) {
    PantallaCalculo(titulo = "Documentación", onVolver = onVolver) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "HidroFES Flow ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                "Calculadora de conductos a presión y canales a superficie libre. " +
                    "Proyecto académico de la UNAM FES Aragón.",
                style = MaterialTheme.typography.bodyMedium,
            )

            secciones.forEach { seccion ->
                SeccionDoc(seccion)
            }

            Text(
                "Los cálculos se hacen en el SI. Agua a 20 °C:  g = 9.81 m/s²,  " +
                    "ρ = 998 kg/m³,  ν = 1.004×10⁻⁶ m²/s,  K = 2.19×10⁹ Pa.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }
    }
}

private data class SeccionDoc(
    val titulo: String,
    val parrafos: List<String>,
    val abiertaAlInicio: Boolean = false,
)

private val secciones = listOf(
    SeccionDoc(
        titulo = "Cómo introducir los datos",
        abiertaAlInicio = true,
        parrafos = listOf(
            "Longitud y caudal llevan su propia unidad. Debajo del campo aparece el valor ya convertido a metros o a m³/s, que es el que entra al cálculo.",
            "El separador decimal puede ser punto o coma. El botón de calcular se activa cuando todos los campos obligatorios tienen un número.",
            "El material trae juntos la rugosidad, el coeficiente C de Hazen-Williams y el n de Manning. PVC queda seleccionado al abrir la pantalla.",
            "Girar el teléfono conserva lo escrito y el último resultado.",
        ),
    ),
    SeccionDoc(
        titulo = "Caudal y diámetro",
        parrafos = listOf(
            "Caudal: con un diámetro y una carga disponible H, obtiene el gasto que circula.",
            "Diámetro: con un gasto y la misma carga, obtiene el diámetro necesario.",
            "Los dos usan H = (1 + f·L/D + ΣK) · V²/2g. Por eso, si pasas el caudal de un módulo al otro, con el resto de datos iguales, recuperas el diámetro de partida.",
            "ΣK resume las pérdidas en accesorios. Si la tubería no tiene piezas especiales, déjalo en 0. Puedes armarlo en Pérdidas locales.",
            "El resultado reparte H entre fricción, pérdidas locales y carga de velocidad. Esas tres cifras suman la carga que introdujiste.",
        ),
    ),
    SeccionDoc(
        titulo = "Pérdidas por fricción",
        parrafos = listOf(
            "Darcy-Weisbach usa la rugosidad del material y cubre régimen laminar y turbulento. Es la referencia general.",
            "Hazen-Williams usa el coeficiente C. Está pensado para agua, diámetros de al menos 50 mm y velocidades entre 0.6 y 3 m/s. Fuera de ese intervalo el cálculo sigue y aparece una advertencia.",
            "Chézy-Manning usa el coeficiente n, con el tubo trabajando lleno.",
            "La tarjeta verde de contraste resuelve los tres métodos con los mismos datos y muestra cuánto se apartan Hazen-Williams y Manning de Darcy-Weisbach.",
        ),
    ),
    SeccionDoc(
        titulo = "Pérdidas locales",
        parrafos = listOf(
            "Cada accesorio tiene un coeficiente K. La pérdida es h_L = ΣK · V²/2g.",
            "Suma las piezas con el botón de cada fila. El ΣK de la barra superior se actualiza al momento y es el valor que puedes llevar a Caudal o a Diámetro.",
            "Sin ninguna pieza seleccionada la pérdida es cero y la app lo indica.",
        ),
    ),
    SeccionDoc(
        titulo = "Celeridad y golpe de ariete",
        parrafos = listOf(
            "Calcula la velocidad de la onda de presión, a = 1 / √(ρ · (1/K + D/(E·e))). El material fija el módulo de Young.",
            "La longitud de la conducción es opcional y sirve para el tiempo crítico 2L/a: un cierre más lento que ese tiempo es una maniobra lenta.",
            "La velocidad de régimen es opcional y sirve para la sobrepresión de Joukowsky, Δh = a·ΔV/g, correspondiente a un cierre instantáneo.",
        ),
    ),
    SeccionDoc(
        titulo = "Canales",
        parrafos = listOf(
            "Flujo uniforme de Manning: Q = (1/n) · A · R^(2/3) · √S. La pendiente S va en m/m.",
            "Rectangular pide el ancho de plantilla. Triangular, el talud z (horizontal entre vertical). Trapezoidal, los dos. Circular, el diámetro, con un tirante menor o igual a ese diámetro.",
            "El número de Froude usa la profundidad hidráulica A/T. Menor que 1 es subcrítico y el control está aguas abajo; mayor que 1 es supercrítico y el control está aguas arriba.",
            "Con la sección circular llena el espejo de agua se anula y el Froude se reporta como no definido. Por encima de y/D ≈ 0.94 el gasto disminuye al subir el tirante.",
            "El tirante crítico se obtiene por bisección y sirve para comparar el tirante de trabajo con el estado de energía mínima.",
        ),
    ),
    SeccionDoc(
        titulo = "Cómo leer el resultado",
        parrafos = listOf(
            "La tarjeta gris indica que todavía no hay cálculo. La roja describe qué dato falta o qué valor no es válido.",
            "La tarjeta azul es el resultado. La magnitud principal va en tamaño grande y el icono de copiar la pasa al portapapeles.",
            "La tarjeta verde es una advertencia: el número es válido y hay una condición del método que conviene revisar.",
        ),
    ),
    SeccionDoc(
        titulo = "Alcance",
        parrafos = listOf(
            "Todo el cálculo supone agua a 20 °C. Para otro fluido o otra temperatura cambian la viscosidad, la densidad y el módulo de compresibilidad.",
            "Los coeficientes de los materiales son los de las tablas de este proyecto. El n del hierro dúctil (0.022) y el del acero remachado (0.010) se apartan de los valores más citados en los manuales.",
        ),
    ),
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun SeccionDoc(seccion: SeccionDoc) {
    var abierta by rememberSaveable(seccion.titulo) { mutableStateOf(seccion.abiertaAlInicio) }

    Card(
        onClick = { abierta = !abierta },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    seccion.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    if (abierta) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (abierta) "Contraer" else "Expandir",
                )
            }

            AnimatedVisibility(visible = abierta) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider()
                    seccion.parrafos.forEach { parrafo ->
                        Text(parrafo, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
