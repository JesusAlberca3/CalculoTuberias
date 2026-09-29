package com.example.calculotuberias.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculotuberias.domain.CatalogoMateriales
import com.example.calculotuberias.domain.FlujoGravedad
import com.example.calculotuberias.domain.Formato
import com.example.calculotuberias.domain.MaterialTuberia
import com.example.calculotuberias.domain.Resultado
import com.example.calculotuberias.domain.UnidadCaudal
import com.example.calculotuberias.ui.components.BarraAccion
import com.example.calculotuberias.ui.components.CampoLongitud
import com.example.calculotuberias.ui.components.CampoNumerico
import com.example.calculotuberias.ui.components.CuerpoDesplazable
import com.example.calculotuberias.ui.components.EntradaLongitud
import com.example.calculotuberias.ui.components.EtiquetaRegimenFlujo
import com.example.calculotuberias.ui.components.PanelResultados
import com.example.calculotuberias.ui.components.PantallaCalculo
import com.example.calculotuberias.ui.components.Renglon
import com.example.calculotuberias.ui.components.SeccionTarjeta
import com.example.calculotuberias.ui.components.SelectorOpcion
import com.example.calculotuberias.ui.components.descripcionMaterial

data class CaudalUiState(
    val diametro: EntradaLongitud = EntradaLongitud(unidad = com.example.calculotuberias.domain.UnidadLongitud.MILIMETRO),
    val longitud: EntradaLongitud = EntradaLongitud(),
    val material: MaterialTuberia = CatalogoMateriales.porDefecto,
    val carga: String = "",
    val sumaK: String = "0",
    val renglones: List<Renglon> = emptyList(),
    val error: String? = null,
    val advertencias: List<String> = emptyList(),
) {
    val datosCompletos: Boolean
        get() = diametro.valorMetros != null &&
            longitud.valorMetros != null &&
            carga.toDoubleOrNull() != null &&
            sumaK.toDoubleOrNull() != null
}

class CaudalViewModel : ViewModel() {

    var estado by mutableStateOf(CaudalUiState())
        private set

    fun alCambiarDiametro(valor: EntradaLongitud) { estado = estado.copy(diametro = valor) }
    fun alCambiarLongitud(valor: EntradaLongitud) { estado = estado.copy(longitud = valor) }
    fun alCambiarMaterial(valor: MaterialTuberia) { estado = estado.copy(material = valor) }
    fun alCambiarCarga(valor: String) { estado = estado.copy(carga = valor) }
    fun alCambiarSumaK(valor: String) { estado = estado.copy(sumaK = valor) }

    fun calcular() {
        val d = estado.diametro.valorMetros
        val l = estado.longitud.valorMetros
        val h = estado.carga.toDoubleOrNull()
        val k = estado.sumaK.toDoubleOrNull()

        if (d == null || l == null || h == null || k == null) {
            estado = estado.copy(
                renglones = emptyList(),
                error = com.example.calculotuberias.domain.Errores.DATOS_INCOMPLETOS,
                advertencias = emptyList(),
            )
            return
        }

        estado = when (val r = FlujoGravedad.caudal(d, l, estado.material.rugosidadMm, h, k)) {
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
                            "Caudal Q",
                            Formato.conUnidad(UnidadCaudal.LITRO_SEGUNDO.desdeM3s(v.caudalM3s), "L/s", 3),
                            destacado = true,
                            nota = "${Formato.auto(v.caudalM3s)} m³/s",
                        ),
                        Renglon("Velocidad media V", Formato.conUnidad(v.velocidad, "m/s")),
                        Renglon("Área hidráulica A", Formato.conUnidad(v.area, "m²")),
                        Renglon("Número de Reynolds", Formato.cientifico(v.reynolds)),
                        Renglon("Factor de fricción f", Formato.numero(v.factorFriccion, 5)),
                        Renglon("Régimen", EtiquetaRegimenFlujo(v.regimen)),
                        Renglon("Pérdida por fricción", Formato.conUnidad(v.perdidaFriccionM, "m")),
                        Renglon("Pérdidas locales", Formato.conUnidad(v.perdidaLocalM, "m")),
                        Renglon("Carga de velocidad V²/2g", Formato.conUnidad(v.cargaVelocidadM, "m")),
                        Renglon("Iteraciones", v.iteraciones.toString()),
                    ),
                )
            }
        }
    }
}

@Composable
fun CaudalScreen(onVolver: () -> Unit, vm: CaudalViewModel = viewModel()) {
    val estado = vm.estado

    PantallaCalculo(
        titulo = "Caudal",
        subtitulo = "Conducción por gravedad",
        onVolver = onVolver,
        accionInferior = {
            BarraAccion(
                texto = "Calcular caudal",
                icono = Icons.Default.Calculate,
                habilitado = estado.datosCompletos,
                onClick = vm::calcular,
            )
        },
    ) { innerPadding ->
        CuerpoDesplazable(innerPadding) {

            SeccionTarjeta(
                titulo = "Geometría de la conducción",
                icono = Icons.Default.Tune,
            ) {
                CampoLongitud(
                    entrada = estado.diametro,
                    onEntradaChange = vm::alCambiarDiametro,
                    etiqueta = "Diámetro interior",
                )
                CampoLongitud(
                    entrada = estado.longitud,
                    onEntradaChange = vm::alCambiarLongitud,
                    etiqueta = "Longitud de la tubería",
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

            SeccionTarjeta(
                titulo = "Condiciones hidráulicas",
                icono = Icons.Default.Science,
                descripcion = "H = (1 + f·L/D + ΣK) · V²/2g",
            ) {
                CampoNumerico(
                    valor = estado.carga,
                    onValorChange = vm::alCambiarCarga,
                    etiqueta = "Carga total disponible H",
                    sufijo = "m",
                )
                CampoNumerico(
                    valor = estado.sumaK,
                    onValorChange = vm::alCambiarSumaK,
                    etiqueta = "Suma de coeficientes locales ΣK",
                    ayuda = "Usa el módulo de pérdidas locales para obtenerlo",
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
