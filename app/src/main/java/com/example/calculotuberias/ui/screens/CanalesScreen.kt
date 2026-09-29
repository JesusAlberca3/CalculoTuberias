package com.example.calculotuberias.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculotuberias.domain.Canales
import com.example.calculotuberias.domain.DatosCanal
import com.example.calculotuberias.domain.Errores
import com.example.calculotuberias.domain.Formato
import com.example.calculotuberias.domain.GeometriaCanal
import com.example.calculotuberias.domain.RegimenCanal
import com.example.calculotuberias.domain.Resultado
import com.example.calculotuberias.domain.UnidadCaudal
import com.example.calculotuberias.ui.components.BarraAccion
import com.example.calculotuberias.ui.components.CampoLongitud
import com.example.calculotuberias.ui.components.CampoNumerico
import com.example.calculotuberias.ui.components.CuerpoDesplazable
import com.example.calculotuberias.ui.components.EntradaLongitud
import com.example.calculotuberias.ui.components.PanelResultados
import com.example.calculotuberias.ui.components.PantallaCalculo
import com.example.calculotuberias.ui.components.Renglon
import com.example.calculotuberias.ui.components.SeccionTarjeta

data class CanalesUiState(
    val geometria: GeometriaCanal = GeometriaCanal.RECTANGULAR,
    val pendiente: String = "",
    val manning: String = "",
    val tirante: EntradaLongitud = EntradaLongitud(),
    val ancho: EntradaLongitud = EntradaLongitud(),
    val talud: String = "",
    val diametro: EntradaLongitud = EntradaLongitud(),
    val renglones: List<Renglon> = emptyList(),
    val error: String? = null,
    val advertencias: List<String> = emptyList(),
) {
    val datosCompletos: Boolean
        get() {
            val comunes = pendiente.toDoubleOrNull() != null &&
                manning.toDoubleOrNull() != null &&
                tirante.valorMetros != null
            return comunes && when (geometria) {
                GeometriaCanal.RECTANGULAR -> ancho.valorMetros != null
                GeometriaCanal.TRIANGULAR -> talud.toDoubleOrNull() != null
                GeometriaCanal.TRAPEZOIDAL ->
                    ancho.valorMetros != null && talud.toDoubleOrNull() != null
                GeometriaCanal.CIRCULAR -> diametro.valorMetros != null
            }
        }
}

class CanalesViewModel : ViewModel() {

    var estado by mutableStateOf(CanalesUiState())
        private set

    fun alCambiarGeometria(valor: GeometriaCanal) {
        estado = estado.copy(geometria = valor, renglones = emptyList(), error = null, advertencias = emptyList())
    }

    fun alCambiarPendiente(valor: String) { estado = estado.copy(pendiente = valor) }
    fun alCambiarManning(valor: String) { estado = estado.copy(manning = valor) }
    fun alCambiarTirante(valor: EntradaLongitud) { estado = estado.copy(tirante = valor) }
    fun alCambiarAncho(valor: EntradaLongitud) { estado = estado.copy(ancho = valor) }
    fun alCambiarTalud(valor: String) { estado = estado.copy(talud = valor) }
    fun alCambiarDiametro(valor: EntradaLongitud) { estado = estado.copy(diametro = valor) }

    fun calcular() {
        val s = estado.pendiente.toDoubleOrNull()
        val n = estado.manning.toDoubleOrNull()
        val y = estado.tirante.valorMetros

        if (s == null || n == null || y == null) {
            estado = estado.copy(
                renglones = emptyList(),
                error = Errores.DATOS_INCOMPLETOS,
                advertencias = emptyList(),
            )
            return
        }

        val datos = DatosCanal(
            geometria = estado.geometria,
            pendiente = s,
            manning = n,
            tirante = y,
            anchoPlantilla = estado.ancho.valorMetros ?: 0.0,
            talud = estado.talud.toDoubleOrNull() ?: 0.0,
            diametro = estado.diametro.valorMetros ?: 0.0,
        )

        estado = when (val r = Canales.calcular(datos)) {
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
                    renglones = buildList {
                        add(
                            Renglon(
                                "Caudal Q",
                                Formato.conUnidad(v.caudal, "m³/s"),
                                destacado = true,
                                nota = "${Formato.numero(UnidadCaudal.LITRO_SEGUNDO.desdeM3s(v.caudal), 1)} L/s",
                            )
                        )
                        add(Renglon("Velocidad media V", Formato.conUnidad(v.velocidad, "m/s")))
                        add(Renglon("Área hidráulica A", Formato.conUnidad(v.area, "m²")))
                        add(Renglon("Perímetro mojado P", Formato.conUnidad(v.perimetroMojado, "m")))
                        add(Renglon("Radio hidráulico R", Formato.conUnidad(v.radioHidraulico, "m")))
                        add(Renglon("Espejo de agua T", Formato.conUnidad(v.espejoAgua, "m")))
                        v.profundidadHidraulica?.let {
                            add(Renglon("Profundidad hidráulica A/T", Formato.conUnidad(it, "m")))
                        }
                        add(Renglon("Energía específica E", Formato.conUnidad(v.energiaEspecifica, "m")))

                        if (v.numeroFroude != null) {
                            add(Renglon("Número de Froude Fr", Formato.numero(v.numeroFroude, 4)))
                            add(
                                Renglon(
                                    "Régimen",
                                    v.regimen?.etiqueta ?: "—",
                                    nota = descripcionRegimen(v.regimen),
                                )
                            )
                        } else {
                            add(Renglon("Número de Froude Fr", "no definido", nota = "T → 0, sección llena"))
                        }

                        v.tiranteCritico?.let {
                            add(
                                Renglon(
                                    "Tirante crítico y_c",
                                    Formato.conUnidad(it, "m"),
                                    nota = "Resuelto por bisección sobre Q²T/(gA³) = 1",
                                )
                            )
                        }
                        v.relacionLlenado?.let {
                            add(Renglon("Relación de llenado y/D", Formato.numero(it, 3)))
                        }
                    },
                )
            }
        }
    }

    private fun descripcionRegimen(regimen: RegimenCanal?): String? = when (regimen) {
        RegimenCanal.SUBCRITICO -> "Fr < 1: el control está aguas abajo"
        RegimenCanal.SUPERCRITICO -> "Fr > 1: el control está aguas arriba"
        RegimenCanal.CRITICO -> "Fr ≈ 1: energía específica mínima"
        null -> null
    }
}

@Composable
fun CanalesScreen(onVolver: () -> Unit, vm: CanalesViewModel = viewModel()) {
    val estado = vm.estado

    PantallaCalculo(
        titulo = "Canales",
        subtitulo = "Flujo uniforme · ${estado.geometria.etiqueta}",
        onVolver = onVolver,
        accionInferior = {
            BarraAccion(
                texto = "Calcular",
                icono = Icons.Default.Calculate,
                habilitado = estado.datosCompletos,
                onClick = vm::calcular,
            )
        },
    ) { innerPadding ->
        CuerpoDesplazable(innerPadding) {

            SeccionTarjeta(titulo = "Geometría de la sección", icono = Icons.Default.Category) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    GeometriaCanal.entries.forEach { geometria ->
                        FilterChip(
                            selected = estado.geometria == geometria,
                            onClick = { vm.alCambiarGeometria(geometria) },
                            label = {
                                Text(
                                    geometria.etiqueta.take(5),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                when (estado.geometria) {
                    GeometriaCanal.RECTANGULAR -> {
                        CampoLongitud(
                            entrada = estado.ancho,
                            onEntradaChange = vm::alCambiarAncho,
                            etiqueta = "Ancho de plantilla b",
                        )
                    }

                    GeometriaCanal.TRIANGULAR -> {
                        CampoNumerico(
                            valor = estado.talud,
                            onValorChange = vm::alCambiarTalud,
                            etiqueta = "Talud z (H:V)",
                        )
                    }

                    GeometriaCanal.TRAPEZOIDAL -> {
                        CampoLongitud(
                            entrada = estado.ancho,
                            onEntradaChange = vm::alCambiarAncho,
                            etiqueta = "Ancho de plantilla b",
                        )
                        CampoNumerico(
                            valor = estado.talud,
                            onValorChange = vm::alCambiarTalud,
                            etiqueta = "Talud z (H:V)",
                        )
                    }

                    GeometriaCanal.CIRCULAR -> {
                        CampoLongitud(
                            entrada = estado.diametro,
                            onEntradaChange = vm::alCambiarDiametro,
                            etiqueta = "Diámetro D",
                        )
                    }
                }

                CampoLongitud(
                    entrada = estado.tirante,
                    onEntradaChange = vm::alCambiarTirante,
                    etiqueta = "Tirante y",
                )
            }

            SeccionTarjeta(
                titulo = "Resistencia y pendiente",
                icono = Icons.Default.Tune,
                descripcion = "Q = (1/n) · A · R^(2/3) · √S",
            ) {
                CampoNumerico(
                    valor = estado.pendiente,
                    onValorChange = vm::alCambiarPendiente,
                    etiqueta = "Pendiente hidráulica S",
                    sufijo = "m/m",
                )
                CampoNumerico(
                    valor = estado.manning,
                    onValorChange = vm::alCambiarManning,
                    etiqueta = "Coeficiente de Manning n",
                    ayuda = "Tierra 0.025 · Concreto 0.014 · PVC 0.009",
                    ultimoDelFormulario = true,
                )
            }

            PanelResultados(
                renglones = estado.renglones,
                error = estado.error,
                advertencias = estado.advertencias,
            )
        }
    }
}
