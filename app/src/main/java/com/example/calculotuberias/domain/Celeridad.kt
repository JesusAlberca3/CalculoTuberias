package com.example.calculotuberias.domain

import kotlin.math.sqrt

data class ResultadoCeleridad(
    /** Celeridad de la onda de presión a [m/s]. */
    val celeridad: Double,
    /** Celeridad teórica en medio indefinido √(K/ρ) [m/s]. */
    val celeridadAguaLibre: Double,
    /** Sobrepresión de Joukowsky por cada 1 m/s de cierre brusco [m de columna de agua]. */
    val sobrepresionPorMetroSegundo: Double,
    /** Tiempo crítico o fase de la conducción 2L/a [s]; requiere la longitud. */
    val tiempoCriticoS: Double?,
    /** Sobrepresión para la velocidad de régimen indicada [m]; requiere esa velocidad. */
    val sobrepresionJoukowskyM: Double?,
)

/**
 * Celeridad de la onda de presión (golpe de ariete), fórmula de Allievi-Korteweg:
 *
 *     a = 1 / √( ρ · ( 1/K + D/(E·e) ) )
 *
 * equivalente a  a = √(K/ρ) / √(1 + K·D/(E·e)).
 */
object Celeridad {

    fun calcular(
        diametroInteriorM: Double,
        espesorM: Double,
        moduloYoungPa: Double,
        longitudConduccionM: Double? = null,
        velocidadRegimen: Double? = null,
    ): Resultado<ResultadoCeleridad> {
        when {
            diametroInteriorM <= 0.0 -> return Resultado.Error(Errores.DIAMETRO_INVALIDO)
            espesorM <= 0.0 -> return Resultado.Error(Errores.ESPESOR_INVALIDO)
            moduloYoungPa <= 0.0 -> return Resultado.Error("Selecciona un material de tubería válido.")
        }

        val rho = Constantes.DENSIDAD_AGUA
        val k = Constantes.MODULO_VOLUMETRICO_AGUA

        val celeridad = 1.0 / sqrt(rho * (1.0 / k + diametroInteriorM / (moduloYoungPa * espesorM)))
        val celeridadLibre = sqrt(k / rho)

        val advertencias = buildList {
            val relacion = diametroInteriorM / espesorM
            if (relacion > 200) {
                add("La relación D/e = ${Formato.numero(relacion, 1)} es muy alta; " +
                    "verifica el espesor introducido.")
            }
            if (relacion < 5) {
                add("La relación D/e = ${Formato.numero(relacion, 1)} corresponde a pared gruesa; " +
                    "la hipótesis de pared delgada de Allievi pierde precisión.")
            }
        }

        return Resultado.Exito(
            ResultadoCeleridad(
                celeridad = celeridad,
                celeridadAguaLibre = celeridadLibre,
                sobrepresionPorMetroSegundo = celeridad / Constantes.GRAVEDAD,
                tiempoCriticoS = longitudConduccionM
                    ?.takeIf { it > 0.0 }
                    ?.let { 2.0 * it / celeridad },
                sobrepresionJoukowskyM = velocidadRegimen
                    ?.takeIf { it > 0.0 }
                    ?.let { celeridad * it / Constantes.GRAVEDAD },
            ),
            advertencias,
        )
    }
}
