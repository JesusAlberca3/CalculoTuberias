package com.example.calculotuberias.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculotuberias.domain.CatalogoMaterialesElasticos
import com.example.calculotuberias.domain.Celeridad
import com.example.calculotuberias.domain.Errores
import com.example.calculotuberias.domain.Formato
import com.example.calculotuberias.domain.MaterialElastico
import com.example.calculotuberias.domain.Resultado
import com.example.calculotuberias.domain.UnidadLongitud
import com.example.calculotuberias.ui.components.BarraAccion
import com.example.calculotuberias.ui.components.CampoLongitud
import com.example.calculotuberias.ui.components.CampoNumerico
import com.example.calculotuberias.ui.components.CuerpoDesplazable
import com.example.calculotuberias.ui.components.EntradaLongitud
import com.example.calculotuberias.ui.components.PanelResultados
import com.example.calculotuberias.ui.components.PantallaCalculo
import com.example.calculotuberias.ui.components.Renglon
import com.example.calculotuberias.ui.components.SeccionTarjeta
import com.example.calculotuberias.ui.components.SelectorOpcion
import com.example.calculotuberias.ui.components.descripcionMaterialElastico

data class CeleridadUiState(
    val diametro: EntradaLongitud = EntradaLongitud(unidad = UnidadLongitud.MILIMETRO),
    val espesor: EntradaLongitud = EntradaLongitud(unidad = UnidadLongitud.MILIMETRO),
    val material: MaterialElastico = CatalogoMaterialesElasticos.porDefecto,
    val longitudConduccion: EntradaLongitud = EntradaLongitud(),
    val velocidadRegimen: String = "",
    val renglones: List<Renglon> = emptyList(),
    val error: String? = null,
    val advertencias: List<String> = emptyList(),
) {
    val datosCompletos: Boolean
        get() = diametro.valorMetros != null && espesor.valorMetros != null
}

class CeleridadViewModel : ViewModel() {

    var estado by mutableStateOf(CeleridadUiState())
        private set

    fun alCambiarDiametro(valor: EntradaLongitud) { estado = estado.copy(diametro = valor) }
    fun alCambiarEspesor(valor: EntradaLongitud) { estado = estado.copy(espesor = valor) }
    fun alCambiarMaterial(valor: MaterialElastico) { estado = estado.copy(material = valor) }
    fun alCambiarLongitud(valor: EntradaLongitud) { estado = estado.copy(longitudConduccion = valor) }
    fun alCambiarVelocidad(valor: String) { estado = estado.copy(velocidadRegimen = valor) }

    fun calcular() {
        val d = estado.diametro.valorMetros
        val e = estado.espesor.valorMetros

        if (d == null || e == null) {
            estado = estado.copy(
                renglones = emptyList(),
                error = Errores.DATOS_INCOMPLETOS,
                advertencias = emptyList(),
            )
            return
        }

        val resultado = Celeridad.calcular(
            diametroInteriorM = d,
            espesorM = e,
            // El material ya trae su módulo: en la versión anterior había que abrir
            // el desplegable aunque mostrase «PVC», o el botón devolvía error.
            moduloYoungPa = estado.material.moduloYoungPa,
            longitudConduccionM = estado.longitudConduccion.valorMetros,
            velocidadRegimen = estado.velocidadRegimen.toDoubleOrNull(),
        )

        estado = when (resultado) {
            is Resultado.Error -> estado.copy(
                renglones = emptyList(),
                error = resultado.mensaje,
                advertencias = emptyList(),
            )

            is Resultado.Exito -> {
                val v = resultado.valor
                estado.copy(
                    error = null,
                    advertencias = resultado.advertencias,
                    renglones = buildList {
                        add(
                            Renglon(
                                "Celeridad a",
                                Formato.conUnidad(v.celeridad, "m/s", 2),
                                destacado = true,
                            )
                        )
                        add(
                            Renglon(
                                "Celeridad en agua libre √(K/ρ)",
                                Formato.conUnidad(v.celeridadAguaLibre, "m/s", 2),
                                nota = "Límite superior teórico con pared indeformable",
                            )
                        )
                        add(
                            Renglon(
                                "Sobrepresión por 1 m/s (a/g)",
                                Formato.conUnidad(v.sobrepresionPorMetroSegundo, "m", 2),
                            )
                        )
                        v.tiempoCriticoS?.let {
                            add(
                                Renglon(
                                    "Tiempo crítico 2L/a",
                                    Formato.conUnidad(it, "s", 3),
                                    nota = "Cierre más lento que este tiempo ⇒ maniobra lenta",
                                )
                            )
                        }
                        v.sobrepresionJoukowskyM?.let {
                            add(
                                Renglon(
                                    "Sobrepresión de Joukowsky",
                                    Formato.conUnidad(it, "m", 2),
                                    nota = "Δh = a·ΔV/g para cierre instantáneo",
                                )
                            )
                        }
                        add(Renglon("Material de pared", estado.material.nombre))
                        add(
                            Renglon(
                                "Módulo de Young E",
                                "${Formato.numero(estado.material.moduloYoungGPa, 1)} GPa",
                            )
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun CeleridadScreen(onVolver: () -> Unit, vm: CeleridadViewModel = viewModel()) {
    val estado = vm.estado

    PantallaCalculo(
        titulo = "Celeridad",
        subtitulo = "Golpe de ariete · Allievi-Korteweg",
        onVolver = onVolver,
        accionInferior = {
            BarraAccion(
                texto = "Calcular celeridad",
                icono = Icons.Default.Calculate,
                habilitado = estado.datosCompletos,
                onClick = vm::calcular,
            )
        },
    ) { innerPadding ->
        CuerpoDesplazable(innerPadding) {

            SeccionTarjeta(
                titulo = "Tubería",
                icono = Icons.Default.Tune,
                descripcion = "a = 1 / √( ρ · ( 1/K + D/(E·e) ) )",
            ) {
                CampoLongitud(
                    entrada = estado.diametro,
                    onEntradaChange = vm::alCambiarDiametro,
                    etiqueta = "Diámetro interior D",
                )
                CampoLongitud(
                    entrada = estado.espesor,
                    onEntradaChange = vm::alCambiarEspesor,
                    etiqueta = "Espesor de pared e",
                )
                SelectorOpcion(
                    etiqueta = "Material de la tubería",
                    opciones = CatalogoMaterialesElasticos.todos,
                    seleccionada = estado.material,
                    textoDe = { it.nombre },
                    detalleDe = ::descripcionMaterialElastico,
                    onSeleccion = vm::alCambiarMaterial,
                )
            }

            SeccionTarjeta(
                titulo = "Datos opcionales del transitorio",
                icono = Icons.Default.Bolt,
                descripcion = "Permiten obtener el tiempo crítico y la sobrepresión de Joukowsky.",
            ) {
                CampoLongitud(
                    entrada = estado.longitudConduccion,
                    onEntradaChange = vm::alCambiarLongitud,
                    etiqueta = "Longitud de la conducción L",
                )
                CampoNumerico(
                    valor = estado.velocidadRegimen,
                    onValorChange = vm::alCambiarVelocidad,
                    etiqueta = "Velocidad de régimen V₀",
                    sufijo = "m/s",
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
