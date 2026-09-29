package com.example.calculotuberias.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class FriccionTest {

    @Test
    fun `Swamee-Jain se aproxima a Colebrook-White dentro del 1 por ciento`() {
        val casos = listOf(
            Triple(1.5e-6, 0.20, 5e5),
            Triple(2.6e-4, 0.30, 1e6),
            Triple(0.0, 0.10, 1e5),
            Triple(9.0e-4, 0.50, 2e6),
        )
        casos.forEach { (eps, d, re) ->
            val sj = Friccion.swameeJain(eps, d, re)
            val cb = Friccion.colebrookWhite(eps, d, re)
            val errorRelativo = abs(sj - cb) / cb
            assertTrue(
                "eps=$eps D=$d Re=$re -> SJ=$sj Colebrook=$cb (error ${errorRelativo * 100} %)",
                errorRelativo < 0.01,
            )
        }
    }

    @Test
    fun `regimen laminar usa 64 entre Reynolds`() {
        val f = Friccion.factorFriccion(rugosidadM = 1.5e-6, diametroM = 0.05, reynolds = 1000.0)
        assertEquals(0.064, f, 1e-12)
    }

    @Test
    fun `clasificacion de regimen segun Reynolds`() {
        assertEquals(RegimenFlujo.LAMINAR, Friccion.regimen(1500.0))
        assertEquals(RegimenFlujo.TRANSICION, Friccion.regimen(3000.0))
        assertEquals(RegimenFlujo.TURBULENTO, Friccion.regimen(100_000.0))
    }

    @Test
    fun `el factor de friccion es continuo en las fronteras de regimen`() {
        val eps = 1.5e-6
        val d = 0.1
        val fLaminar = Friccion.factorFriccion(eps, d, Friccion.REYNOLDS_LAMINAR_MAX - 0.001)
        val fTransicionInicio = Friccion.factorFriccion(eps, d, Friccion.REYNOLDS_LAMINAR_MAX + 0.001)
        assertEquals(fLaminar, fTransicionInicio, 1e-6)

        val fTransicionFin = Friccion.factorFriccion(eps, d, Friccion.REYNOLDS_TURBULENTO_MIN - 0.001)
        val fTurbulento = Friccion.factorFriccion(eps, d, Friccion.REYNOLDS_TURBULENTO_MIN + 0.001)
        assertEquals(fTransicionFin, fTurbulento, 1e-6)
    }

    @Test
    fun `Reynolds se calcula con la viscosidad del agua a 20 grados`() {
        val re = Friccion.reynolds(velocidad = 2.0, diametroM = 0.1)
        assertEquals(2.0 * 0.1 / 1.004e-6, re, 1e-6)
    }
}
