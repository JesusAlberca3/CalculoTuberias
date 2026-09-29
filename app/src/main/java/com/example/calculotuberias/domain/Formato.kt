package com.example.calculotuberias.domain

import java.util.Locale
import kotlin.math.abs

/**
 * Formateo numérico con locale fijo.
 *
 * Se usa [Locale.US] a propósito: la entrada acepta punto decimal, así que la
 * salida debe usarlo también. Antes se llamaba a `String.format` con el locale
 * del dispositivo y en un teléfono en español los resultados salían con coma.
 */
object Formato {

    fun numero(valor: Double, decimales: Int = 4): String = when {
        valor.isNaN() -> "—"
        valor.isInfinite() -> "∞"
        else -> String.format(Locale.US, "%.${decimales}f", valor)
    }

    fun cientifico(valor: Double, decimales: Int = 3): String = when {
        valor.isNaN() -> "—"
        valor.isInfinite() -> "∞"
        else -> String.format(Locale.US, "%.${decimales}e", valor)
    }

    /** Elige notación fija o científica según la magnitud del valor. */
    fun auto(valor: Double, decimales: Int = 4): String {
        if (valor.isNaN() || valor.isInfinite()) return numero(valor)
        val magnitud = abs(valor)
        return if (magnitud != 0.0 && (magnitud < 1e-3 || magnitud >= 1e6)) {
            cientifico(valor)
        } else {
            numero(valor, decimales)
        }
    }

    fun conUnidad(valor: Double, unidad: String, decimales: Int = 4): String =
        "${auto(valor, decimales)} $unidad"
}
