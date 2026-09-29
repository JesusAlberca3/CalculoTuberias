package com.example.calculotuberias.domain

/**
 * Constantes físicas del agua a 20 °C expresadas en unidades del SI.
 *
 * Se centralizan aquí para que todos los módulos de cálculo usen exactamente
 * los mismos valores; en la versión anterior cada pantalla declaraba los suyos
 * (9.81 / 998 / 1e-6 / 1.004e-6) y los resultados no eran comparables entre sí.
 */
object Constantes {

    /** Aceleración de la gravedad [m/s²]. */
    const val GRAVEDAD = 9.81

    /** Densidad del agua a 20 °C [kg/m³]. */
    const val DENSIDAD_AGUA = 998.0

    /** Viscosidad cinemática del agua a 20 °C [m²/s]. */
    const val VISCOSIDAD_CINEMATICA = 1.004e-6

    /** Módulo de compresibilidad volumétrica del agua a 20 °C [Pa]. */
    const val MODULO_VOLUMETRICO_AGUA = 2.19e9
}
