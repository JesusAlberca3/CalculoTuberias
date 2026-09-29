package com.example.calculotuberias.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculotuberias.domain.CatalogoMateriales
import com.example.calculotuberias.domain.Errores
import com.example.calculotuberias.domain.Formato
import com.example.calculotuberias.domain.MaterialTuberia
import com.example.calculotuberias.domain.MetodoFriccion
import com.example.calculotuberias.domain.PerdidaFriccion
import com.example.calculotuberias.domain.PerdidasFriccion
import com.example.calculotuberias.domain.Resultado
import com.example.calculotuberias.ui.components.BarraAccion
import com.example.calculotuberias.ui.components.CampoCaudal
import com.example.calculotuberias.ui.components.CampoLongitud
import com.example.calculotuberias.ui.components.CuerpoDesplazable
import com.example.calculotuberias.ui.components.EntradaCaudal
import com.example.calculotuberias.ui.components.EntradaLongitud
import com.example.calculotuberias.ui.components.EtiquetaRegimenFlujo
import com.example.calculotuberias.ui.components.PanelResultados
import com.example.calculotuberias.ui.components.PantallaCalculo
import com.example.calculotuberias.ui.components.Renglon
import com.example.calculotuberias.ui.components.SeccionTarjeta
import com.example.calculotuberias.ui.components.SelectorOpcion
import com.example.calculotuberias.ui.components.descripcionMaterial
import com.example.calculotuberias.domain.UnidadLongitud

data class PerdidasFriccionUiState(
    val longitud: EntradaLongitud = EntradaLongitud(),
    val diametro: EntradaLongitud = EntradaLongitud(unidad = UnidadLongitud.MILIMETRO),
    val caudal: EntradaCaudal = EntradaCaudal(),
    val material: MaterialTuberia = CatalogoMateriales.porDefecto,
    val metodo: MetodoFriccion = MetodoFriccion.DARCY_WEISBACH,
    val renglones: List<Renglon> = emptyList(),
    val comparativa: List<Pair<MetodoFriccion, Double>> = emptyList(),
    val error: String? = null,
    val advertencias: List<String> = emptyList(),
) {
    val datosCompletos: Boolean
        get() = longitud.valorMetros != null &&
            diametro.valorMetros != null &&
            caudal.valorM3s != null
}

class PerdidasFriccionViewModel : ViewModel() {

    var estado by mutableStateOf(PerdidasFriccionUiState())
        private set

    fun alCambiarLongitud(valor: EntradaLongitud) { estado = estado.copy(longitud = valor) }
    fun alCambiarDiametro(valor: EntradaLongitud) { estado = estado.copy(diametro = valor) }
    fun alCambiarCaudal(valor: EntradaCaudal) { estado = estado.copy(caudal = valor) }
    fun alCambiarMaterial(valor: MaterialTuberia) { estado = estado.copy(material = valor) }

    fun alCambiarMetodo(valor: MetodoFriccion) {
        estado = estado.copy(metodo = valor)
        if (estado.renglones.isNotEmpty() || estado.error != null) calcular()
    }

    fun calcular() {
        val l = estado.longitud.valorMetros
        val d = estado.diametro.valorMetros
        val q = estado.caudal.valorM3s
        val material = estado.material

        if (l == null || d == null || q == null) {
            estado = estado.copy(
                renglones = emptyList(),
                comparativa = emptyList(),
                error = Errores.DATOS_INCOMPLETOS,
                advertencias = emptyList(),
            )
            return
        }

        fun evaluar(metodo: MetodoFriccion): Resultado<PerdidaFriccion> = when (metodo) {
            MetodoFriccion.DARCY_WEISBACH ->
                PerdidasFriccion.darcyWeisbach(l, d, material.rugosidadMm, q)

            MetodoFriccion.HAZEN_WILLIAMS ->
                PerdidasFriccion.hazenWilliams(l, d, material.coeficienteHazenWilliams, q)

            MetodoFriccion.CHEZY_MANNING ->
                PerdidasFriccion.chezyManning(l, d, material.coeficienteManning, q)
        }

        // Se resuelven los tres métodos siempre: el elegido se detalla y los otros
        // dos sirven de contraste, que es justo lo que se pide en clase.
        val comparativa = MetodoFriccion.entries.mapNotNull { metodo ->
            (evaluar(metodo) as? Resultado.Exito)?.valor?.let { metodo to it.perdidaM }
        }

        estado = when (val r = evaluar(estado.metodo)) {
            is Resultado.Error -> estado.copy(
                renglones = emptyList(),
                comparativa = emptyList(),
                error = r.mensaje,
                advertencias = emptyList(),
            )

            is Resultado.Exito -> {
                val v = r.valor
                estado.copy(
                    error = null,
                    advertencias = r.advertencias,
                    comparativa = comparativa,
                    renglones = buildList {
                        add(
                            Renglon(
                                "Pérdida por fricción h_f",
                                Formato.conUnidad(v.perdidaM, "m", 4),
                                destacado = true,
                            )
                        )
                        add(Renglon("Gradiente hidráulico S", Formato.conUnidad(v.gradienteHidraulico, "m/m", 6)))
                        add(Renglon("Velocidad media V", Formato.conUnidad(v.velocidad, "m/s")))
                        add(Renglon("Área hidráulica A", Formato.conUnidad(v.area, "m²")))
                        add(Renglon("Carga de velocidad V²/2g", Formato.conUnidad(v.cargaVelocidad, "m")))
                        v.reynolds?.let { add(Renglon("Número de Reynolds", Formato.cientifico(it))) }
                        v.factorFriccion?.let { add(Renglon("Factor de fricción f", Formato.numero(it, 5))) }
                        v.regimen?.let { add(Renglon("Régimen", EtiquetaRegimenFlujo(it))) }
                    },
                )
            }
        }
    }
}

@Composable
fun PerdidasFriccionScreen(onVolver: () -> Unit, vm: PerdidasFriccionViewModel = viewModel()) {
    val estado = vm.estado

    PantallaCalculo(
        titulo = "Pérdidas por fricción",
        subtitulo = estado.metodo.etiqueta,
        onVolver = onVolver,
        accionInferior = {
            BarraAccion(
                texto = "Calcular pérdidas",
                icono = Icons.Default.Calculate,
                habilitado = estado.datosCompletos,
                onClick = vm::calcular,
            )
        },
    ) { innerPadding ->
        CuerpoDesplazable(innerPadding) {

            SeccionTarjeta(
                titulo = "Método de cálculo",
                icono = Icons.AutoMirrored.Filled.CompareArrows,
                descripcion = estado.metodo.descripcion,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MetodoFriccion.entries.forEach { metodo ->
                        FilterChip(
                            selected = estado.metodo == metodo,
                            onClick = { vm.alCambiarMetodo(metodo) },
                            label = {
                                Text(
                                    when (metodo) {
                                        MetodoFriccion.DARCY_WEISBACH -> "Darcy"
                                        MetodoFriccion.HAZEN_WILLIAMS -> "Hazen"
                                        MetodoFriccion.CHEZY_MANNING -> "Manning"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            SeccionTarjeta(titulo = "Datos de la conducción", icono = Icons.Default.Tune) {
                CampoLongitud(
                    entrada = estado.longitud,
                    onEntradaChange = vm::alCambiarLongitud,
                    etiqueta = "Longitud L",
                )
                CampoLongitud(
                    entrada = estado.diametro,
                    onEntradaChange = vm::alCambiarDiametro,
                    etiqueta = "Diámetro interior D",
                )
                CampoCaudal(
                    entrada = estado.caudal,
                    onEntradaChange = vm::alCambiarCaudal,
                    etiqueta = "Gasto Q",
                )
                SelectorOpcion(
                    etiqueta = "Material",
                    opciones = CatalogoMateriales.todos,
                    seleccionada = estado.material,
                    textoDe = { it.nombre },
                    detalleDe = ::descripcionMaterial,
                    onSeleccion = vm::alCambiarMaterial,
                )
            }

            PanelResultados(
                renglones = estado.renglones,
                error = estado.error,
                advertencias = estado.advertencias,
            )

            if (estado.comparativa.size > 1) {
                TarjetaComparativa(estado.comparativa)
            }
        }
    }
}

@Composable
private fun TarjetaComparativa(comparativa: List<Pair<MetodoFriccion, Double>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Contraste entre métodos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Text(
                "Mismos datos resueltos por las tres formulaciones.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f),
            )

            val referencia = comparativa.firstOrNull {
                it.first == MetodoFriccion.DARCY_WEISBACH
            }?.second

            comparativa.forEach { (metodo, perdida) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        metodo.etiqueta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        Formato.conUnidad(perdida, "m"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        textAlign = TextAlign.End,
                    )
                    if (referencia != null && referencia > 0.0 && metodo != MetodoFriccion.DARCY_WEISBACH) {
                        Text(
                            "  (${Formato.numero((perdida / referencia - 1.0) * 100, 1)} %)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                        )
                    }
                }
            }
        }
    }
}
