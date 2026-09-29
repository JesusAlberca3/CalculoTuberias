package com.example.calculotuberias.domain

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Conducción por gravedad entre dos depósitos o hacia descarga libre.
 *
 * Ecuación de la energía empleada, idéntica para los dos sentidos del problema:
 *
 *     H = (1 + f·L/D + ΣK) · V²/(2g)
 *
 * El término `1` es la carga de velocidad en la descarga. En la versión anterior
 * el cálculo de diámetro lo incluía y el de caudal no, de modo que encadenar
 * ambos módulos no cerraba el círculo. Ahora sí: calcular Q a partir de D y
 * volver a calcular D con ese Q devuelve el diámetro original.
 */
object FlujoGravedad {

    private const val TOLERANCIA_FRICCION = 1e-10
    private const val TOLERANCIA_DIAMETRO = 1e-10
    private const val MAX_ITERACIONES = 200
    private const val DIAMETRO_INICIAL = 0.5

    data class ResultadoCaudal(
        val caudalM3s: Double,
        val velocidad: Double,
        val area: Double,
        val factorFriccion: Double,
        val reynolds: Double,
        val regimen: RegimenFlujo,
        val perdidaFriccionM: Double,
        val perdidaLocalM: Double,
        val cargaVelocidadM: Double,
        val iteraciones: Int,
    )

    data class ResultadoDiametro(
        val diametroM: Double,
        val velocidad: Double,
        val area: Double,
        val factorFriccion: Double,
        val reynolds: Double,
        val regimen: RegimenFlujo,
        val perdidaFriccionM: Double,
        val perdidaLocalM: Double,
        val cargaVelocidadM: Double,
        val iteraciones: Int,
    )

    /** Caudal que circula por un diámetro conocido bajo una carga H disponible. */
    fun caudal(
        diametroM: Double,
        longitudM: Double,
        rugosidadMm: Double,
        cargaDisponibleM: Double,
        sumaK: Double,
    ): Resultado<ResultadoCaudal> {
        when {
            diametroM <= 0.0 -> return Resultado.Error(Errores.DIAMETRO_INVALIDO)
            longitudM <= 0.0 -> return Resultado.Error(Errores.LONGITUD_INVALIDA)
            cargaDisponibleM <= 0.0 -> return Resultado.Error(Errores.CARGA_INVALIDA)
            sumaK < 0.0 -> return Resultado.Error("El coeficiente de pérdidas locales no puede ser negativo.")
        }

        val rugosidadM = rugosidadMm / 1000.0
        var f = 0.02
        var velocidad = 0.0
        var reynolds = 0.0
        var iteraciones = 0
        var delta: Double

        do {
            velocidad = velocidadPorEnergia(cargaDisponibleM, f, longitudM, diametroM, sumaK)
            reynolds = Friccion.reynolds(velocidad, diametroM)
            val fNuevo = Friccion.factorFriccion(rugosidadM, diametroM, reynolds)
            delta = abs(fNuevo - f)
            f = fNuevo
            iteraciones++
        } while (delta > TOLERANCIA_FRICCION && iteraciones < MAX_ITERACIONES)

        if (!velocidad.isFinite() || velocidad <= 0.0) {
            return Resultado.Error(Errores.SIN_CONVERGENCIA)
        }

        velocidad = velocidadPorEnergia(cargaDisponibleM, f, longitudM, diametroM, sumaK)
        val area = PI * diametroM * diametroM / 4.0
        val cargaVelocidad = velocidad * velocidad / (2.0 * Constantes.GRAVEDAD)

        return Resultado.Exito(
            ResultadoCaudal(
                caudalM3s = area * velocidad,
                velocidad = velocidad,
                area = area,
                factorFriccion = f,
                reynolds = reynolds,
                regimen = Friccion.regimen(reynolds),
                perdidaFriccionM = f * (longitudM / diametroM) * cargaVelocidad,
                perdidaLocalM = sumaK * cargaVelocidad,
                cargaVelocidadM = cargaVelocidad,
                iteraciones = iteraciones,
            ),
            advertenciasVelocidad(velocidad),
        )
    }

    /** Diámetro necesario para conducir un caudal dado con la carga H disponible. */
    fun diametro(
        caudalM3s: Double,
        longitudM: Double,
        rugosidadMm: Double,
        cargaDisponibleM: Double,
        sumaK: Double,
    ): Resultado<ResultadoDiametro> {
        when {
            caudalM3s <= 0.0 -> return Resultado.Error(Errores.CAUDAL_INVALIDO)
            longitudM <= 0.0 -> return Resultado.Error(Errores.LONGITUD_INVALIDA)
            cargaDisponibleM <= 0.0 -> return Resultado.Error(Errores.CARGA_INVALIDA)
            sumaK < 0.0 -> return Resultado.Error("El coeficiente de pérdidas locales no puede ser negativo.")
        }

        val rugosidadM = rugosidadMm / 1000.0
        var diametro = DIAMETRO_INICIAL
        var f = 0.02
        var reynolds = 0.0
        var iteraciones = 0
        var error: Double

        do {
            val area = PI * diametro * diametro / 4.0
            val velocidad = caudalM3s / area
            reynolds = Friccion.reynolds(velocidad, diametro)
            f = Friccion.factorFriccion(rugosidadM, diametro, reynolds)

            val velocidadObjetivo =
                velocidadPorEnergia(cargaDisponibleM, f, longitudM, diametro, sumaK)
            val diametroNuevo = sqrt(4.0 * caudalM3s / (PI * velocidadObjetivo))

            // Criterio de paro por tolerancia absoluta: la versión anterior comparaba
            // con `!= 0.0` y por eso oscilaba hasta agotar las 1000 iteraciones.
            error = abs(diametroNuevo - diametro)
            diametro = diametroNuevo
            iteraciones++
        } while (error > TOLERANCIA_DIAMETRO && iteraciones < MAX_ITERACIONES)

        if (!diametro.isFinite() || diametro <= 0.0) {
            return Resultado.Error(Errores.SIN_CONVERGENCIA)
        }

        val area = PI * diametro * diametro / 4.0
        val velocidad = caudalM3s / area
        val cargaVelocidad = velocidad * velocidad / (2.0 * Constantes.GRAVEDAD)

        val advertencias = advertenciasVelocidad(velocidad) + buildList {
            if (iteraciones >= MAX_ITERACIONES) {
                add("Se alcanzó el máximo de $MAX_ITERACIONES iteraciones; el resultado puede no estar convergido.")
            }
        }

        return Resultado.Exito(
            ResultadoDiametro(
                diametroM = diametro,
                velocidad = velocidad,
                area = area,
                factorFriccion = f,
                reynolds = reynolds,
                regimen = Friccion.regimen(reynolds),
                perdidaFriccionM = f * (longitudM / diametro) * cargaVelocidad,
                perdidaLocalM = sumaK * cargaVelocidad,
                cargaVelocidadM = cargaVelocidad,
                iteraciones = iteraciones,
            ),
            advertencias,
        )
    }

    private fun velocidadPorEnergia(
        cargaH: Double,
        f: Double,
        longitudM: Double,
        diametroM: Double,
        sumaK: Double,
    ): Double = sqrt(
        (2.0 * Constantes.GRAVEDAD * cargaH) / (1.0 + f * longitudM / diametroM + sumaK)
    )

    private fun advertenciasVelocidad(velocidad: Double): List<String> = buildList {
        if (velocidad > 5.0) {
            add("V = ${Formato.numero(velocidad, 2)} m/s es alta; revisa erosión y golpe de ariete.")
        }
        if (velocidad < 0.3) {
            add("V = ${Formato.numero(velocidad, 2)} m/s es baja; puede favorecer sedimentación.")
        }
    }
}
