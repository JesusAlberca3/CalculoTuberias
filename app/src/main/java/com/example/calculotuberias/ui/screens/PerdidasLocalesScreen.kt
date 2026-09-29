package com.example.calculotuberias.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculotuberias.domain.Accesorio
import com.example.calculotuberias.domain.AccesorioSeleccionado
import com.example.calculotuberias.domain.CatalogoAccesorios
import com.example.calculotuberias.domain.CategoriaAccesorio
import com.example.calculotuberias.domain.Errores
import com.example.calculotuberias.domain.Formato
import com.example.calculotuberias.domain.PerdidasLocales
import com.example.calculotuberias.domain.Resultado
import com.example.calculotuberias.domain.UnidadLongitud
import com.example.calculotuberias.ui.components.BarraAccion
import com.example.calculotuberias.ui.components.CampoCaudal
import com.example.calculotuberias.ui.components.CampoLongitud
import com.example.calculotuberias.ui.components.CuerpoDesplazable
import com.example.calculotuberias.ui.components.EntradaCaudal
import com.example.calculotuberias.ui.components.EntradaLongitud
import com.example.calculotuberias.ui.components.PanelResultados
import com.example.calculotuberias.ui.components.PantallaCalculo
import com.example.calculotuberias.ui.components.Renglon
import com.example.calculotuberias.ui.components.SeccionTarjeta

data class PerdidasLocalesUiState(
    val caudal: EntradaCaudal = EntradaCaudal(),
    val diametro: EntradaLongitud = EntradaLongitud(unidad = UnidadLongitud.MILIMETRO),
    val seleccion: List<AccesorioSeleccionado> = CatalogoAccesorios.seleccionInicial,
    val renglones: List<Renglon> = emptyList(),
    val error: String? = null,
    val advertencias: List<String> = emptyList(),
) {
    val coeficienteKTotal: Double get() = seleccion.sumOf { it.aporteK }
    val piezasTotales: Int get() = seleccion.sumOf { it.cantidad }
    val datosCompletos: Boolean get() = caudal.valorM3s != null && diametro.valorMetros != null
}

class PerdidasLocalesViewModel : ViewModel() {

    var estado by mutableStateOf(PerdidasLocalesUiState())
        private set

    fun alCambiarCaudal(valor: EntradaCaudal) { estado = estado.copy(caudal = valor); recalcularSiProcede() }
    fun alCambiarDiametro(valor: EntradaLongitud) { estado = estado.copy(diametro = valor); recalcularSiProcede() }

    fun sumar(accesorio: Accesorio) = ajustar(accesorio, 1)

    fun restar(accesorio: Accesorio) = ajustar(accesorio, -1)

    fun limpiarSeleccion() {
        estado = estado.copy(seleccion = CatalogoAccesorios.seleccionInicial)
        recalcularSiProcede()
    }

    private fun ajustar(accesorio: Accesorio, delta: Int) {
        estado = estado.copy(
            seleccion = estado.seleccion.map {
                if (it.accesorio.nombre == accesorio.nombre) {
                    it.copy(cantidad = (it.cantidad + delta).coerceAtLeast(0))
                } else {
                    it
                }
            },
        )
        // A diferencia de la versión anterior, ΣK y la pérdida se actualizan al
        // instante: no hacía falta volver a pulsar «calcular» pero el resultado
        // mostrado quedaba obsoleto sin avisar.
        recalcularSiProcede()
    }

    private fun recalcularSiProcede() {
        if (estado.renglones.isNotEmpty() || estado.error != null) calcular()
    }

    fun calcular() {
        val q = estado.caudal.valorM3s
        val d = estado.diametro.valorMetros

        if (q == null || d == null) {
            estado = estado.copy(
                renglones = emptyList(),
                error = Errores.DATOS_INCOMPLETOS,
                advertencias = emptyList(),
            )
            return
        }

        estado = when (val r = PerdidasLocales.calcular(q, d, estado.coeficienteKTotal)) {
            is Resultado.Error -> estado.copy(
                renglones = emptyList(),
                error = r.mensaje,
                advertencias = emptyList(),
            )

            is Resultado.Exito -> {
                val v = r.valor
                estado.copy(
                    error = null,
                    advertencias = r.advertencias,
                    renglones = listOf(
                        Renglon(
                            "Pérdida local h_L",
                            Formato.conUnidad(v.perdidaM, "m"),
                            destacado = true,
                        ),
                        Renglon("Coeficiente total ΣK", Formato.numero(v.coeficienteKTotal, 2)),
                        Renglon("Velocidad media V", Formato.conUnidad(v.velocidad, "m/s")),
                        Renglon("Carga de velocidad V²/2g", Formato.conUnidad(v.cargaVelocidad, "m")),
                        Renglon("Área hidráulica A", Formato.conUnidad(v.area, "m²")),
                        Renglon("Piezas seleccionadas", estado.piezasTotales.toString()),
                    ),
                )
            }
        }
    }
}

@Composable
fun PerdidasLocalesScreen(onVolver: () -> Unit, vm: PerdidasLocalesViewModel = viewModel()) {
    val estado = vm.estado

    PantallaCalculo(
        titulo = "Pérdidas locales",
        subtitulo = "ΣK = ${Formato.numero(estado.coeficienteKTotal, 2)}",
        onVolver = onVolver,
        accionInferior = {
            BarraAccion(
                texto = "Calcular pérdidas locales",
                icono = Icons.Default.Calculate,
                habilitado = estado.datosCompletos,
                onClick = vm::calcular,
            )
        },
    ) { innerPadding ->
        CuerpoDesplazable(innerPadding) {

            SeccionTarjeta(titulo = "Condiciones de flujo", icono = Icons.Default.Tune) {
                CampoCaudal(
                    entrada = estado.caudal,
                    onEntradaChange = vm::alCambiarCaudal,
                    etiqueta = "Gasto Q",
                )
                CampoLongitud(
                    entrada = estado.diametro,
                    onEntradaChange = vm::alCambiarDiametro,
                    etiqueta = "Diámetro interior D",
                )
            }

            SeccionTarjeta(
                titulo = "Accesorios",
                icono = Icons.Default.Plumbing,
                descripcion = "h_L = ΣK · V²/2g",
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text("ΣK = ${Formato.numero(estado.coeficienteKTotal, 2)}") },
                    )
                    AssistChip(
                        onClick = vm::limpiarSeleccion,
                        label = { Text("Limpiar") },
                    )
                }

                CategoriaAccesorio.entries.forEach { categoria ->
                    val deCategoria = estado.seleccion.filter { it.accesorio.categoria == categoria }
                    if (deCategoria.isEmpty()) return@forEach

                    Text(
                        categoria.etiqueta,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    HorizontalDivider()

                    deCategoria.forEach { seleccionado ->
                        FilaAccesorio(
                            seleccionado = seleccionado,
                            onSumar = { vm.sumar(seleccionado.accesorio) },
                            onRestar = { vm.restar(seleccionado.accesorio) },
                        )
                    }
                }
            }

            PanelResultados(
                renglones = estado.renglones,
                error = estado.error,
                advertencias = estado.advertencias,
            )
        }
    }
}

@Composable
private fun FilaAccesorio(
    seleccionado: AccesorioSeleccionado,
    onSumar: () -> Unit,
    onRestar: () -> Unit,
) {
    val activo = seleccionado.cantidad > 0

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    seleccionado.accesorio.nombre,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "K = ${Formato.numero(seleccionado.accesorio.coeficienteK, 2)}" +
                        if (activo) "   →   aporta ${Formato.numero(seleccionado.aporteK, 2)}" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (activo) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }

            IconButton(onClick = onRestar, enabled = activo) {
                Icon(Icons.Default.Remove, contentDescription = "Quitar una pieza")
            }

            Text(
                seleccionado.cantidad.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            FilledIconButton(onClick = onSumar, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Añadir una pieza")
            }
        }
    }
}
