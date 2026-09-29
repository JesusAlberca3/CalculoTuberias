package com.example.calculotuberias.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.cbrt
import kotlin.math.pow
import kotlin.math.sqrt

class CanalesTest {

    private fun exito(r: Resultado<ResultadoCanal>): ResultadoCanal {
        assertTrue("Se esperaba éxito y se obtuvo $r", r is Resultado.Exito)
        return (r as Resultado.Exito).valor
    }

    @Test
    fun `canal rectangular reproduce el calculo manual de Manning`() {
        val datos = DatosCanal(
            geometria = GeometriaCanal.RECTANGULAR,
            pendiente = 0.001,
            manning = 0.015,
            tirante = 1.0,
            anchoPlantilla = 3.0,
        )
        val r = exito(Canales.calcular(datos))

        assertEquals(3.0, r.area, 1e-12)
        assertEquals(5.0, r.perimetroMojado, 1e-12)
        assertEquals(0.6, r.radioHidraulico, 1e-12)
        assertEquals(3.0, r.espejoAgua, 1e-12)

        val qEsperado = (1.0 / 0.015) * 3.0 * 0.6.pow(2.0 / 3.0) * sqrt(0.001)
        assertEquals(qEsperado, r.caudal, 1e-10)
        assertEquals(qEsperado / 3.0, r.velocidad, 1e-10)
    }

    @Test
    fun `tirante critico rectangular coincide con la formula cerrada`() {
        val b = 3.0
        val datos = DatosCanal(
            geometria = GeometriaCanal.RECTANGULAR,
            pendiente = 0.001,
            manning = 0.015,
            tirante = 1.0,
            anchoPlantilla = b,
        )
        val r = exito(Canales.calcular(datos))

        val caudalUnitario = r.caudal / b
        val ycAnalitico = cbrt(caudalUnitario * caudalUnitario / Constantes.GRAVEDAD)

        assertNotNull(r.tiranteCritico)
        assertEquals(ycAnalitico, r.tiranteCritico!!, 1e-6)
    }

    @Test
    fun `el numero de Froude usa la profundidad hidraulica A entre T`() {
        val datos = DatosCanal(
            geometria = GeometriaCanal.TRIANGULAR,
            pendiente = 0.001,
            manning = 0.015,
            tirante = 1.0,
            talud = 1.5,
        )
        val r = exito(Canales.calcular(datos))

        // Para sección triangular la profundidad hidráulica vale exactamente y/2.
        assertEquals(0.5, r.profundidadHidraulica!!, 1e-12)
        assertEquals(r.velocidad / sqrt(Constantes.GRAVEDAD * 0.5), r.numeroFroude!!, 1e-12)
    }

    @Test
    fun `canal trapezoidal reproduce area perimetro y espejo`() {
        val b = 3.0
        val z = 1.5
        val y = 1.0
        val datos = DatosCanal(
            geometria = GeometriaCanal.TRAPEZOIDAL,
            pendiente = 0.001,
            manning = 0.015,
            tirante = y,
            anchoPlantilla = b,
            talud = z,
        )
        val r = exito(Canales.calcular(datos))

        assertEquals(y * (b + z * y), r.area, 1e-12)
        assertEquals(b + 2.0 * y * sqrt(1.0 + z * z), r.perimetroMojado, 1e-12)
        assertEquals(b + 2.0 * z * y, r.espejoAgua, 1e-12)
    }

    @Test
    fun `seccion circular a media capacidad vale la mitad del circulo`() {
        val d = 1.0
        val datos = DatosCanal(
            geometria = GeometriaCanal.CIRCULAR,
            pendiente = 0.001,
            manning = 0.015,
            tirante = d / 2.0,
            diametro = d,
        )
        val r = exito(Canales.calcular(datos))

        assertEquals(PI * d * d / 8.0, r.area, 1e-10)
        assertEquals(PI * d / 2.0, r.perimetroMojado, 1e-10)
        assertEquals(d, r.espejoAgua, 1e-10)
        assertEquals(d / 4.0, r.radioHidraulico, 1e-10)
    }

    @Test
    fun `el gasto maximo del conducto circular ocurre cerca de y entre D igual a 0_938`() {
        fun q(relacion: Double): Double = exito(
            Canales.calcular(
                DatosCanal(
                    geometria = GeometriaCanal.CIRCULAR,
                    pendiente = 0.001,
                    manning = 0.013,
                    tirante = relacion,
                    diametro = 1.0,
                )
            )
        ).caudal

        assertTrue(q(0.938) > q(0.80))
        assertTrue(q(0.938) > q(0.999))
    }

    @Test
    fun `con la seccion llena el Froude queda indefinido en lugar de valer cero`() {
        val r = Canales.calcular(
            DatosCanal(
                geometria = GeometriaCanal.CIRCULAR,
                pendiente = 0.001,
                manning = 0.013,
                tirante = 1.0,
                diametro = 1.0,
            )
        )
        val valor = exito(r)
        assertNull("El espejo de agua es nulo: Fr no está definido", valor.numeroFroude)
        assertTrue((r as Resultado.Exito).advertencias.isNotEmpty())
    }

    @Test
    fun `clasificacion del regimen segun Froude`() {
        assertEquals(RegimenCanal.SUBCRITICO, Canales.clasificarRegimen(0.5))
        assertEquals(RegimenCanal.CRITICO, Canales.clasificarRegimen(1.0))
        assertEquals(RegimenCanal.SUPERCRITICO, Canales.clasificarRegimen(2.0))
    }

    @Test
    fun `se rechazan datos no fisicos`() {
        val base = DatosCanal(GeometriaCanal.RECTANGULAR, 0.001, 0.015, 1.0, anchoPlantilla = 3.0)
        assertTrue(Canales.calcular(base.copy(pendiente = 0.0)) is Resultado.Error)
        assertTrue(Canales.calcular(base.copy(manning = 0.0)) is Resultado.Error)
        assertTrue(Canales.calcular(base.copy(tirante = 0.0)) is Resultado.Error)
        assertTrue(Canales.calcular(base.copy(anchoPlantilla = 0.0)) is Resultado.Error)
        assertTrue(
            Canales.calcular(
                DatosCanal(GeometriaCanal.CIRCULAR, 0.001, 0.013, tirante = 1.5, diametro = 1.0)
            ) is Resultado.Error
        )
    }

    @Test
    fun `la energia especifica es el tirante mas la carga de velocidad`() {
        val r = exito(
            Canales.calcular(
                DatosCanal(GeometriaCanal.RECTANGULAR, 0.001, 0.015, 1.0, anchoPlantilla = 3.0)
            )
        )
        val esperado = 1.0 + r.velocidad * r.velocidad / (2.0 * Constantes.GRAVEDAD)
        assertEquals(esperado, r.energiaEspecifica, 1e-12)
    }
}
