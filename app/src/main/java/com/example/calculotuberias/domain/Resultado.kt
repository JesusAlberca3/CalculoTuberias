package com.example.calculotuberias.domain

/**
 * Salida de cualquier rutina de cálculo: o un valor válido o un motivo de fallo.
 *
 * Obliga a que la capa de UI trate el caso de error de forma explícita, en lugar
 * de propagar `NaN` / `Infinity` hasta la pantalla como ocurría antes.
 */
sealed interface Resultado<out T> {

    data class Exito<T>(val valor: T, val advertencias: List<String> = emptyList()) : Resultado<T>

    data class Error(val mensaje: String) : Resultado<Nothing>
}

fun <T> Resultado<T>.valorOrNull(): T? = (this as? Resultado.Exito)?.valor

/** Errores de validación reutilizados por los distintos módulos. */
object Errores {
    const val DATOS_INCOMPLETOS = "Completa todos los campos con valores numéricos válidos."
    const val DIAMETRO_INVALIDO = "El diámetro debe ser mayor que cero."
    const val LONGITUD_INVALIDA = "La longitud debe ser mayor que cero."
    const val CAUDAL_INVALIDO = "El caudal debe ser mayor que cero."
    const val CARGA_INVALIDA = "La carga disponible debe ser mayor que cero."
    const val ESPESOR_INVALIDO = "El espesor de pared debe ser mayor que cero."
    const val COEFICIENTE_INVALIDO = "El coeficiente del material debe ser mayor que cero."
    const val TIRANTE_INVALIDO = "El tirante debe ser mayor que cero."
    const val PENDIENTE_INVALIDA = "La pendiente debe ser mayor que cero."
    const val MANNING_INVALIDO = "El coeficiente de Manning debe ser mayor que cero."
    const val SIN_CONVERGENCIA = "El método iterativo no convergió; revisa los datos de entrada."
}
