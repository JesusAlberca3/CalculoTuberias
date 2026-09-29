package com.example.calculotuberias.domain

import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

enum class RegimenFlujo(val etiqueta: String) {
    LAMINAR("Laminar"),
    TRANSICION("Transición"),
    TURBULENTO("Turbulento"),
}

/**
 * Factor de fricción de Darcy-Weisbach.
 *
 * Además de Swamee-Jain (la aproximación explícita que usaba la versión
 * anterior) se resuelve el régimen laminar con f = 64/Re, porque Swamee-Jain
 * sólo es válida para Re ≥ 4000 y devolvía valores sin sentido físico por
 * debajo de ese umbral.
 */
object Friccion {

    const val REYNOLDS_LAMINAR_MAX = 2300.0
    const val REYNOLDS_TURBULENTO_MIN = 4000.0

    fun reynolds(
        velocidad: Double,
        diametroM: Double,
        viscosidad: Double = Constantes.VISCOSIDAD_CINEMATICA,
    ): Double = velocidad * diametroM / viscosidad

    fun regimen(reynolds: Double): RegimenFlujo = when {
        reynolds < REYNOLDS_LAMINAR_MAX -> RegimenFlujo.LAMINAR
        reynolds < REYNOLDS_TURBULENTO_MIN -> RegimenFlujo.TRANSICION
        else -> RegimenFlujo.TURBULENTO
    }

    /** Aproximación explícita de Swamee-Jain (válida para 5000 ≤ Re ≤ 1e8). */
    fun swameeJain(rugosidadM: Double, diametroM: Double, reynolds: Double): Double =
        0.25 / log10(rugosidadM / (3.7 * diametroM) + 5.74 / reynolds.pow(0.9)).pow(2)

    /** Solución iterativa de Colebrook-White; se usa como referencia en las pruebas. */
    fun colebrookWhite(
        rugosidadM: Double,
        diametroM: Double,
        reynolds: Double,
        iteraciones: Int = 50,
    ): Double {
        var f = swameeJain(rugosidadM, diametroM, reynolds)
        repeat(iteraciones) {
            val raiz = -2.0 * log10(rugosidadM / (3.7 * diametroM) + 2.51 / (reynolds * sqrt(f)))
            f = 1.0 / (raiz * raiz)
        }
        return f
    }

    /** Factor de fricción cubriendo los tres regímenes. */
    fun factorFriccion(rugosidadM: Double, diametroM: Double, reynolds: Double): Double = when {
        reynolds <= 0.0 -> 0.0
        reynolds < REYNOLDS_LAMINAR_MAX -> 64.0 / reynolds
        reynolds < REYNOLDS_TURBULENTO_MIN -> {
            // Interpolación lineal en la zona crítica, donde no existe ley única.
            val fLaminar = 64.0 / REYNOLDS_LAMINAR_MAX
            val fTurbulento = swameeJain(rugosidadM, diametroM, REYNOLDS_TURBULENTO_MIN)
            val t = (reynolds - REYNOLDS_LAMINAR_MAX) /
                (REYNOLDS_TURBULENTO_MIN - REYNOLDS_LAMINAR_MAX)
            fLaminar + t * (fTurbulento - fLaminar)
        }
        else -> swameeJain(rugosidadM, diametroM, reynolds)
    }
}
