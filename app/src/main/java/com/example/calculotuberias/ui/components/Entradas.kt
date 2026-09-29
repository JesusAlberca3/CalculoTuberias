package com.example.calculotuberias.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.calculotuberias.domain.UnidadCaudal
import com.example.calculotuberias.domain.UnidadLongitud

private val PATRON_NUMERO = Regex("^\\d*\\.?\\d*$")

/** Normaliza la coma decimal y descarta pulsaciones que romperían el número. */
private fun sanear(entrada: String): String? {
    val texto = entrada.replace(',', '.')
    return texto.takeIf { it.matches(PATRON_NUMERO) }
}

/**
 * Longitud introducida por el usuario junto con la unidad elegida.
 *
 * Mantener texto y unidad en un único objeto elevado al llamador evita el
 * defecto de la versión anterior, donde el campo guardaba su propio estado
 * interno y el valor convertido que recibía la pantalla podía quedar desfasado.
 */
data class EntradaLongitud(
    val texto: String = "",
    val unidad: UnidadLongitud = UnidadLongitud.METRO,
) {
    val valorMetros: Double? get() = texto.toDoubleOrNull()?.let { unidad.aMetros(it) }
    val estaVacia: Boolean get() = texto.isBlank()
}

/** Caudal introducido por el usuario junto con su unidad. */
data class EntradaCaudal(
    val texto: String = "",
    val unidad: UnidadCaudal = UnidadCaudal.LITRO_SEGUNDO,
) {
    val valorM3s: Double? get() = texto.toDoubleOrNull()?.let { unidad.aM3s(it) }
    val estaVacia: Boolean get() = texto.isBlank()
}

/** Campo numérico simple, sin unidades. */
@Composable
fun CampoNumerico(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    sufijo: String? = null,
    ayuda: String? = null,
    esError: Boolean = false,
    ultimoDelFormulario: Boolean = false,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = { sanear(it)?.let(onValorChange) },
        label = { Text(etiqueta) },
        suffix = sufijo?.let { { Text(it) } },
        supportingText = ayuda?.let { { Text(it) } },
        isError = esError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = if (ultimoDelFormulario) ImeAction.Done else ImeAction.Next,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

/** Campo numérico con selector de unidad de longitud. */
@Composable
fun CampoLongitud(
    entrada: EntradaLongitud,
    onEntradaChange: (EntradaLongitud) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    ayuda: String? = null,
    esError: Boolean = false,
) {
    CampoConSelector(
        texto = entrada.texto,
        onTextoChange = { onEntradaChange(entrada.copy(texto = it)) },
        etiqueta = etiqueta,
        unidadActual = entrada.unidad.etiqueta,
        unidades = UnidadLongitud.entries.map { it.etiqueta },
        onUnidadSeleccionada = { indice ->
            onEntradaChange(entrada.copy(unidad = UnidadLongitud.entries[indice]))
        },
        ayuda = ayuda ?: entrada.valorMetros?.let { "= ${"%.6g".format(it)} m" },
        esError = esError,
        modifier = modifier,
    )
}

/** Campo numérico con selector de unidad de caudal. */
@Composable
fun CampoCaudal(
    entrada: EntradaCaudal,
    onEntradaChange: (EntradaCaudal) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    esError: Boolean = false,
) {
    CampoConSelector(
        texto = entrada.texto,
        onTextoChange = { onEntradaChange(entrada.copy(texto = it)) },
        etiqueta = etiqueta,
        unidadActual = entrada.unidad.etiqueta,
        unidades = UnidadCaudal.entries.map { it.etiqueta },
        onUnidadSeleccionada = { indice ->
            onEntradaChange(entrada.copy(unidad = UnidadCaudal.entries[indice]))
        },
        ayuda = entrada.valorM3s?.let { "= ${"%.6g".format(it)} m³/s" },
        esError = esError,
        modifier = modifier,
    )
}

@Composable
private fun CampoConSelector(
    texto: String,
    onTextoChange: (String) -> Unit,
    etiqueta: String,
    unidadActual: String,
    unidades: List<String>,
    onUnidadSeleccionada: (Int) -> Unit,
    ayuda: String?,
    esError: Boolean,
    modifier: Modifier = Modifier,
) {
    var desplegado by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        OutlinedTextField(
            value = texto,
            onValueChange = { sanear(it)?.let(onTextoChange) },
            label = { Text(etiqueta) },
            supportingText = ayuda?.let { { Text(it, style = MaterialTheme.typography.bodySmall) } },
            isError = esError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.weight(1f),
        )

        Box(modifier = Modifier.padding(start = 8.dp, top = 6.dp)) {
            TextButton(
                onClick = { desplegado = true },
                modifier = Modifier.width(96.dp),
            ) {
                Text(unidadActual, style = MaterialTheme.typography.labelLarge)
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Cambiar unidad")
            }

            DropdownMenu(expanded = desplegado, onDismissRequest = { desplegado = false }) {
                unidades.forEachIndexed { indice, unidad ->
                    DropdownMenuItem(
                        text = { Text(unidad) },
                        onClick = {
                            onUnidadSeleccionada(indice)
                            desplegado = false
                        },
                    )
                }
            }
        }
    }
}

/**
 * Selector genérico para catálogos (materiales, geometrías, métodos…).
 *
 * Se implementa como tarjeta pulsable en lugar de un campo de texto de solo
 * lectura: toda la superficie responde al toque y el menú muestra además el
 * detalle de cada opción (por ejemplo ε, C y n de cada material).
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectorOpcion(
    etiqueta: String,
    opciones: List<T>,
    seleccionada: T,
    textoDe: (T) -> String,
    onSeleccion: (T) -> Unit,
    modifier: Modifier = Modifier,
    detalleDe: ((T) -> String?)? = null,
) {
    var desplegado by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedCard(onClick = { desplegado = true }, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        etiqueta,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(textoDe(seleccionada), style = MaterialTheme.typography.bodyLarge)
                    detalleDe?.invoke(seleccionada)?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar $etiqueta")
            }
        }

        DropdownMenu(
            expanded = desplegado,
            onDismissRequest = { desplegado = false },
            modifier = Modifier.fillMaxWidth(0.92f),
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(textoDe(opcion))
                            detalleDe?.invoke(opcion)?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    },
                    onClick = {
                        onSeleccion(opcion)
                        desplegado = false
                    },
                )
            }
        }
    }
}
