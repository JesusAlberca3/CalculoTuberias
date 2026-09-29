package com.example.calculotuberias.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow

class PerdidasFriccionTest {

    private fun exito(resultado: Resultado<PerdidaFriccion>): PerdidaFriccion {
        assertTrue("Se esperaba éxito y se obtuvo $resultado", resultado is Resultado.Exito)
        return (resultado as Resultado.Exito).valor
    }

    @Test
    fun `Darcy-Weisbach reproduce el calculo manual`() {
        val l = 100.0
        val d = 0.20
        val q = 0.050
        val epsMm = 0.0015

        val r = exito(PerdidasFriccion.darcyWeisbach(l, d, epsMm, q))

        val area = PI * d * d / 4.0
        val v = q / area
        val re = v * d / Constantes.VISCOSIDAD_CINEMATICA
        val f = Friccion.swameeJain(epsMm / 1000.0, d, re)
        val esperado = f * (l / d) * v * v / (2.0 * Constantes.GRAVEDAD)

        assertEquals(esperado, r.perdidaM, 1e-12)
        assertEquals(v, r.velocidad, 1e-12)
        assertEquals(re, r.reynolds!!, 1e-6)
        assertEquals(esperado / l, r.gradienteHidraulico, 1e-12)
    }

    @Test
    fun `al duplicar el caudal la perdida crece casi con el cuadrado`() {
        val base = exito(PerdidasFriccion.darcyWeisbach(100.0, 0.2, 0.0015, 0.05))
        val doble = exito(PerdidasFriccion.darcyWeisbach(100.0, 0.2, 0.0015, 0.10))
        val relacion = doble.perdidaM / base.perdidaM

        // Queda por debajo de 4 porque f disminuye al aumentar Reynolds en tubería lisa.
        assertTrue("relación = $relacion", relacion in 3.4..4.0)
        assertTrue("f debe bajar al subir Re", doble.factorFriccion!! < base.factorFriccion!!)
    }

    @Test
    fun `Hazen-Williams usa los exponentes estandar del SI`() {
        val l = 100.0
        val d = 0.20
        val c = 150.0
        val q = 0.050

        val r = exito(PerdidasFriccion.hazenWilliams(l, d, c, q))
        val esperado = 10.674 * l * (q / c).pow(1.852) / d.pow(4.871)

        assertEquals(esperado, r.perdidaM, 1e-12)
    }

    @Test
    fun `Hazen-Williams queda cerca de Darcy-Weisbach para PVC`() {
        val dw = exito(PerdidasFriccion.darcyWeisbach(100.0, 0.2, 0.0015, 0.05))
        val hw = exito(PerdidasFriccion.hazenWilliams(100.0, 0.2, 150.0, 0.05))
        val desviacion = abs(hw.perdidaM - dw.perdidaM) / dw.perdidaM
        assertTrue("desviación = ${desviacion * 100} %", desviacion < 0.15)
    }

    @Test
    fun `Chezy-Manning coincide con aplicar Manning directamente con R igual a D entre 4`() {
        val l = 100.0
        val d = 0.20
        val n = 0.009
        val q = 0.050

        val r = exito(PerdidasFriccion.chezyManning(l, d, n, q))

        val area = PI * d * d / 4.0
        val v = q / area
        val radioHidraulico = d / 4.0
        val pendiente = (n * v / radioHidraulico.pow(2.0 / 3.0)).pow(2)
        val esperado = pendiente * l

        assertEquals(esperado, r.perdidaM, esperado * 1e-4)
    }

    @Test
    fun `la constante de Manning es 4 elevado a 10 tercios entre pi cuadrado`() {
        val exacta = 4.0.pow(10.0 / 3.0) / (PI * PI)
        assertEquals(exacta, PerdidasFriccion.CONSTANTE_MANNING, 1e-3)
    }

    @Test
    fun `se rechazan entradas no fisicas en lugar de devolver NaN`() {
        assertTrue(PerdidasFriccion.darcyWeisbach(100.0, 0.0, 0.0015, 0.05) is Resultado.Error)
        assertTrue(PerdidasFriccion.darcyWeisbach(0.0, 0.2, 0.0015, 0.05) is Resultado.Error)
        assertTrue(PerdidasFriccion.darcyWeisbach(100.0, 0.2, 0.0015, 0.0) is Resultado.Error)
        assertTrue(PerdidasFriccion.hazenWilliams(100.0, 0.2, 0.0, 0.05) is Resultado.Error)
        assertTrue(PerdidasFriccion.chezyManning(100.0, 0.2, 0.0, 0.05) is Resultado.Error)
    }
}
