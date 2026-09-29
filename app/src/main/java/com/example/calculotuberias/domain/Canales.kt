package com.example.calculotuberias.domain

import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class GeometriaCanal(val etiqueta: String) {
    RECTANGULAR("Rectangular"),
    TRIANGULAR("Triangular"),
    TRAPEZOIDAL("Trapezoidal"),
    CIRCULAR("Circular"),
}

enum class RegimenCanal(val etiqueta: String) {
    SUBCRITICO("Subcrítico"),
    CRITICO("Crítico"),
    SUPERCRITICO("Supercrítico"),
}

/** Propiedades geométricas de la sección mojada para un tirante dado. */
data class SeccionMojada(
    val area: Double,
    val perimetroMojado: Double,
    val espejoAgua: Double,
) {
    val radioHidraulico: Double get() = area / perimetroMojado

    /** Profundidad hidráulica D = A/T, la que gobierna el número de Froude. */
    val profundidadHidraulica: Double? get() =
        if (espejoAgua > 1e-9) area / espejoAgua else null
}

/** Datos de entrada de un canal. Sólo se usan los campos que aplican a la geometría. */
data class DatosCanal(
    val geometria: GeometriaCanal,
    val pendiente: Double,
    val manning: Double,
    val tirante: Double,
    val anchoPlantilla: Double = 0.0,
    val talud: Double = 0.0,
    val diametro: Double = 0.0,
)

data class ResultadoCanal(
    val area: Double,
    val perimetroMojado: Double,
    val radioHidraulico: Double,
    val espejoAgua: Double,
    val profundidadHidraulica: Double?,
    val caudal: Double,
    val velocidad: Double,
    val energiaEspecifica: Double,
    val numeroFroude: Double?,
    val regimen: RegimenCanal?,
    val tiranteCritico: Double?,
    val relacionLlenado: Double?,
)

/**
 * Flujo uniforme en canales por la ecuación de Manning:
 *
 *     Q = (1/n) · A · R^(2/3) · √S
 *
 * Además del gasto se resuelve el tirante crítico por bisección sobre la
 * condición de energía mínima Q²·T / (g·A³) = 1, válida para cualquier sección.
 */
object Canales {

    private const val MAX_RELACION_LLENADO = 0.999

    fun calcular(datos: DatosCanal): Resultado<ResultadoCanal> {
        validar(datos)?.let { return it }

        val seccion = seccionMojada(datos) ?: return Resultado.Error(Errores.DATOS_INCOMPLETOS)
        if (seccion.area <= 0.0 || seccion.perimetroMojado <= 0.0) {
            return Resultado.Error("La geometría introducida no define una sección de flujo válida.")
        }

        val radio = seccion.radioHidraulico
        val caudal = (1.0 / datos.manning) * seccion.area * radio.pow(2.0 / 3.0) * sqrt(datos.pendiente)
        val velocidad = caudal / seccion.area
        val energia = datos.tirante + velocidad * velocidad / (2.0 * Constantes.GRAVEDAD)

        val profundidadHidraulica = seccion.profundidadHidraulica
        val froude = profundidadHidraulica
            ?.let { velocidad / sqrt(Constantes.GRAVEDAD * it) }

        val tiranteCritico = tiranteCritico(datos, caudal)

        val advertencias = buildList {
            if (profundidadHidraulica == null) {
                add("Con y ≈ D el espejo de agua tiende a cero: la sección trabaja llena y el " +
                    "número de Froude deja de estar definido. Usa el módulo de conductos a presión.")
            }
            if (datos.geometria == GeometriaCanal.CIRCULAR) {
                val llenado = datos.tirante / datos.diametro
                if (llenado > 0.94) {
                    add("Por encima de y/D ≈ 0.938 el gasto disminuye al aumentar el tirante " +
                        "(zona inestable del conducto parcialmente lleno).")
                }
            }
            if (froude != null && kotlin.math.abs(froude - 1.0) < 0.05) {
                add("Fr ≈ 1: el flujo está próximo al estado crítico y la superficie libre es inestable.")
            }
        }

        return Resultado.Exito(
            ResultadoCanal(
                area = seccion.area,
                perimetroMojado = seccion.perimetroMojado,
                radioHidraulico = radio,
                espejoAgua = seccion.espejoAgua,
                profundidadHidraulica = profundidadHidraulica,
                caudal = caudal,
                velocidad = velocidad,
                energiaEspecifica = energia,
                numeroFroude = froude,
                regimen = froude?.let(::clasificarRegimen),
                tiranteCritico = tiranteCritico,
                relacionLlenado = if (datos.geometria == GeometriaCanal.CIRCULAR) {
                    datos.tirante / datos.diametro
                } else null,
            ),
            advertencias,
        )
    }

    fun clasificarRegimen(froude: Double): RegimenCanal = when {
        froude < 0.995 -> RegimenCanal.SUBCRITICO
        froude > 1.005 -> RegimenCanal.SUPERCRITICO
        else -> RegimenCanal.CRITICO
    }

    /** Geometría de la sección mojada para el tirante indicado. */
    fun seccionMojada(datos: DatosCanal): SeccionMojada? {
        val y = datos.tirante
        return when (datos.geometria) {
            GeometriaCanal.RECTANGULAR -> {
                val b = datos.anchoPlantilla
                SeccionMojada(area = b * y, perimetroMojado = b + 2.0 * y, espejoAgua = b)
            }

            GeometriaCanal.TRIANGULAR -> {
                val z = datos.talud
                SeccionMojada(
                    area = z * y * y,
                    perimetroMojado = 2.0 * y * sqrt(1.0 + z * z),
                    espejoAgua = 2.0 * z * y,
                )
            }

            GeometriaCanal.TRAPEZOIDAL -> {
                val b = datos.anchoPlantilla
                val z = datos.talud
                SeccionMojada(
                    area = y * (b + z * y),
                    perimetroMojado = b + 2.0 * y * sqrt(1.0 + z * z),
                    espejoAgua = b + 2.0 * z * y,
                )
            }

            GeometriaCanal.CIRCULAR -> {
                val d = datos.diametro
                if (d <= 0.0 || y > d) return null
                val theta = 2.0 * acos((1.0 - 2.0 * y / d).coerceIn(-1.0, 1.0))
                SeccionMojada(
                    area = (d * d / 8.0) * (theta - sin(theta)),
                    perimetroMojado = (d / 2.0) * theta,
                    espejoAgua = d * sin(theta / 2.0),
                )
            }
        }
    }

    /**
     * Tirante crítico por bisección sobre f(y) = Q²·T/(g·A³) − 1.
     *
     * Funciona para cualquier geometría porque sólo necesita A(y) y T(y).
     */
    fun tiranteCritico(datos: DatosCanal, caudal: Double): Double? {
        if (caudal <= 0.0) return null

        val limiteSuperior = when (datos.geometria) {
            GeometriaCanal.CIRCULAR -> datos.diametro * MAX_RELACION_LLENADO
            else -> maxOf(datos.tirante * 20.0, 100.0)
        }

        fun f(y: Double): Double? {
            val seccion = seccionMojada(datos.copy(tirante = y)) ?: return null
            if (seccion.area <= 1e-12 || seccion.espejoAgua <= 1e-12) return null
            return caudal * caudal * seccion.espejoAgua /
                (Constantes.GRAVEDAD * seccion.area.pow(3)) - 1.0
        }

        var inferior = 1e-6
        var superior = limiteSuperior
        val fInferior = f(inferior) ?: return null
        val fSuperior = f(superior) ?: return null
        if (fInferior * fSuperior > 0.0) return null

        repeat(120) {
            val medio = 0.5 * (inferior + superior)
            val fMedio = f(medio) ?: return null
            if (fInferior * fMedio <= 0.0) superior = medio else inferior = medio
        }
        return 0.5 * (inferior + superior)
    }

    private fun validar(datos: DatosCanal): Resultado.Error? = when {
        datos.pendiente <= 0.0 -> Resultado.Error(Errores.PENDIENTE_INVALIDA)
        datos.manning <= 0.0 -> Resultado.Error(Errores.MANNING_INVALIDO)
        datos.tirante <= 0.0 -> Resultado.Error(Errores.TIRANTE_INVALIDO)

        datos.geometria == GeometriaCanal.RECTANGULAR && datos.anchoPlantilla <= 0.0 ->
            Resultado.Error("El ancho de plantilla debe ser mayor que cero.")

        datos.geometria == GeometriaCanal.TRIANGULAR && datos.talud <= 0.0 ->
            Resultado.Error("El talud z debe ser mayor que cero.")

        datos.geometria == GeometriaCanal.TRAPEZOIDAL &&
            (datos.anchoPlantilla < 0.0 || datos.talud <= 0.0) ->
            Resultado.Error("Revisa el ancho de plantilla (≥ 0) y el talud z (> 0).")

        datos.geometria == GeometriaCanal.CIRCULAR && datos.diametro <= 0.0 ->
            Resultado.Error(Errores.DIAMETRO_INVALIDO)

        datos.geometria == GeometriaCanal.CIRCULAR && datos.tirante > datos.diametro ->
            Resultado.Error("En sección circular el tirante debe cumplir 0 < y ≤ D.")

        else -> null
    }
}
