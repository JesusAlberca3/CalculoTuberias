package com.example.calculotuberias.domain

import kotlin.math.PI
import kotlin.math.pow

enum class MetodoFriccion(val etiqueta: String, val descripcion: String) {
    DARCY_WEISBACH(
        "Darcy-Weisbach",
        "Universal. Válido para cualquier fluido y régimen; requiere la rugosidad absoluta ε.",
    ),
    HAZEN_WILLIAMS(
        "Hazen-Williams",
        "Empírico para agua a temperatura ambiente, D ≥ 50 mm y V entre 0.6 y 3 m/s.",
    ),
    CHEZY_MANNING(
        "Chézy-Manning",
        "Deriva de flujo en canales; aplicado a tubo lleno con R = D/4.",
    ),
}

/** Resultado detallado de una pérdida por fricción en conducto a presión. */
data class PerdidaFriccion(
    val metodo: MetodoFriccion,
    /** Pérdida de carga h_f [m]. */
    val perdidaM: Double,
    /** Área hidráulica [m²]. */
    val area: Double,
    /** Velocidad media [m/s]. */
    val velocidad: Double,
    /** Gradiente hidráulico S = h_f / L [m/m]. */
    val gradienteHidraulico: Double,
    /** Carga de velocidad V²/2g [m]. */
    val cargaVelocidad: Double,
    val reynolds: Double? = null,
    val factorFriccion: Double? = null,
    val regimen: RegimenFlujo? = null,
)

object PerdidasFriccion {

    /**
     * Darcy-Weisbach: h_f = f · (L/D) · V²/(2g), con f por Swamee-Jain / laminar.
     */
    fun darcyWeisbach(
        longitudM: Double,
        diametroM: Double,
        rugosidadMm: Double,
        caudalM3s: Double,
    ): Resultado<PerdidaFriccion> {
        validarConducto(longitudM, diametroM, caudalM3s)?.let { return it }

        val area = areaCircular(diametroM)
        val velocidad = caudalM3s / area
        val reynolds = Friccion.reynolds(velocidad, diametroM)
        val f = Friccion.factorFriccion(rugosidadMm / 1000.0, diametroM, reynolds)
        val cargaVelocidad = velocidad * velocidad / (2.0 * Constantes.GRAVEDAD)
        val perdida = f * (longitudM / diametroM) * cargaVelocidad

        val regimen = Friccion.regimen(reynolds)
        val advertencias = buildList {
            if (regimen == RegimenFlujo.TRANSICION) {
                add("Re = ${Formato.cientifico(reynolds)} cae en la zona crítica (2300–4000): " +
                    "el factor de fricción es incierto.")
            }
        }

        return Resultado.Exito(
            PerdidaFriccion(
                metodo = MetodoFriccion.DARCY_WEISBACH,
                perdidaM = perdida,
                area = area,
                velocidad = velocidad,
                gradienteHidraulico = perdida / longitudM,
                cargaVelocidad = cargaVelocidad,
                reynolds = reynolds,
                factorFriccion = f,
                regimen = regimen,
            ),
            advertencias,
        )
    }

    /**
     * Hazen-Williams en unidades SI: h_f = 10.674 · L · (Q/C)^1.852 / D^4.871.
     *
     * La versión anterior usaba 10.68 con exponentes redondeados a 1.85 y 4.85,
     * lo que introducía del orden de 1.6 % de desviación frente a la forma estándar.
     */
    fun hazenWilliams(
        longitudM: Double,
        diametroM: Double,
        coeficienteC: Double,
        caudalM3s: Double,
    ): Resultado<PerdidaFriccion> {
        validarConducto(longitudM, diametroM, caudalM3s)?.let { return it }
        if (coeficienteC <= 0.0) return Resultado.Error(Errores.COEFICIENTE_INVALIDO)

        val area = areaCircular(diametroM)
        val velocidad = caudalM3s / area
        val perdida = 10.674 * longitudM * (caudalM3s / coeficienteC).pow(1.852) /
            diametroM.pow(4.871)

        val advertencias = buildList {
            if (diametroM < 0.05) add("Hazen-Williams pierde validez para diámetros menores a 50 mm.")
            if (velocidad < 0.6 || velocidad > 3.0) {
                add("V = ${Formato.numero(velocidad, 2)} m/s está fuera del rango recomendado " +
                    "(0.6 a 3.0 m/s) para Hazen-Williams.")
            }
        }

        return Resultado.Exito(
            PerdidaFriccion(
                metodo = MetodoFriccion.HAZEN_WILLIAMS,
                perdidaM = perdida,
                area = area,
                velocidad = velocidad,
                gradienteHidraulico = perdida / longitudM,
                cargaVelocidad = velocidad * velocidad / (2.0 * Constantes.GRAVEDAD),
            ),
            advertencias,
        )
    }

    /**
     * Chézy-Manning en tubo lleno: h_f = 10.294 · n² · L · Q² / D^(16/3).
     *
     * La constante exacta es 4^(10/3)/π² = 10.2937 (antes se usaba 10.29).
     */
    fun chezyManning(
        longitudM: Double,
        diametroM: Double,
        coeficienteN: Double,
        caudalM3s: Double,
    ): Resultado<PerdidaFriccion> {
        validarConducto(longitudM, diametroM, caudalM3s)?.let { return it }
        if (coeficienteN <= 0.0) return Resultado.Error(Errores.COEFICIENTE_INVALIDO)

        val area = areaCircular(diametroM)
        val velocidad = caudalM3s / area
        val perdida = CONSTANTE_MANNING * coeficienteN * coeficienteN * longitudM *
            caudalM3s * caudalM3s / diametroM.pow(16.0 / 3.0)

        return Resultado.Exito(
            PerdidaFriccion(
                metodo = MetodoFriccion.CHEZY_MANNING,
                perdidaM = perdida,
                area = area,
                velocidad = velocidad,
                gradienteHidraulico = perdida / longitudM,
                cargaVelocidad = velocidad * velocidad / (2.0 * Constantes.GRAVEDAD),
            ),
        )
    }

    /** 4^(10/3) / π² */
    const val CONSTANTE_MANNING = 10.2937

    fun areaCircular(diametroM: Double): Double = PI * diametroM * diametroM / 4.0

    private fun validarConducto(
        longitudM: Double,
        diametroM: Double,
        caudalM3s: Double,
    ): Resultado.Error? = when {
        longitudM <= 0.0 -> Resultado.Error(Errores.LONGITUD_INVALIDA)
        diametroM <= 0.0 -> Resultado.Error(Errores.DIAMETRO_INVALIDO)
        caudalM3s <= 0.0 -> Resultado.Error(Errores.CAUDAL_INVALIDO)
        else -> null
    }
}
