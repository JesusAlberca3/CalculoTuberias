package com.example.calculotuberias.domain

/**
 * Unidades de longitud admitidas en la entrada de datos.
 *
 * El dominio trabaja siempre en metros; la conversión ocurre en la frontera.
 */
enum class UnidadLongitud(val etiqueta: String, val factorAMetros: Double) {
    MILIMETRO("mm", 1e-3),
    CENTIMETRO("cm", 1e-2),
    METRO("m", 1.0),
    PULGADA("in", 0.0254),
    PIE("ft", 0.3048);

    fun aMetros(valor: Double): Double = valor * factorAMetros

    fun desdeMetros(metros: Double): Double = metros / factorAMetros
}

/** Unidades de caudal admitidas. El dominio trabaja en m³/s. */
enum class UnidadCaudal(val etiqueta: String, val factorAM3s: Double) {
    LITRO_SEGUNDO("L/s", 1e-3),
    METRO3_SEGUNDO("m³/s", 1.0),
    METRO3_HORA("m³/h", 1.0 / 3600.0),
    GALON_MINUTO("gal/min", 6.309_02e-5);

    fun aM3s(valor: Double): Double = valor * factorAM3s

    fun desdeM3s(m3s: Double): Double = m3s / factorAM3s
}
