package com.example.calculotuberias.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sqrt

class CeleridadTest {

    private fun exito(r: Resultado<ResultadoCeleridad>): ResultadoCeleridad {
        assertTrue("Se esperaba éxito y se obtuvo $r", r is Resultado.Exito)
        return (r as Resultado.Exito).valor
    }

    @Test
    fun `coincide con la forma clasica de Korteweg`() {
        val d = 0.5
        val e = 0.008
        val young = 207e9

        val obtenido = exito(Celeridad.calcular(d, e, young)).celeridad

        val k = Constantes.MODULO_VOLUMETRICO_AGUA
        val rho = Constantes.DENSIDAD_AGUA
        val referencia = sqrt(k / rho) / sqrt(1.0 + k * d / (young * e))

        assertEquals(referencia, obtenido, 1e-9)
    }

    @Test
    fun `una pared infinitamente rigida tiende a la celeridad del agua libre`() {
        val r = exito(Celeridad.calcular(0.2, 0.01, 1e30))
        assertEquals(sqrt(Constantes.MODULO_VOLUMETRICO_AGUA / Constantes.DENSIDAD_AGUA), r.celeridad, 1e-3)
    }

    @Test
    fun `el acero da mayor celeridad que el PVC`() {
        val acero = exito(Celeridad.calcular(0.2, 0.006, 207e9)).celeridad
        val pvc = exito(Celeridad.calcular(0.2, 0.006, 3.3e9)).celeridad
        assertTrue("acero=$acero pvc=$pvc", acero > pvc)
    }

    @Test
    fun `tiempo critico y sobrepresion de Joukowsky`() {
        val longitud = 1200.0
        val velocidad = 1.8
        val r = exito(Celeridad.calcular(0.3, 0.01, 207e9, longitud, velocidad))

        assertNotNull(r.tiempoCriticoS)
        assertEquals(2.0 * longitud / r.celeridad, r.tiempoCriticoS!!, 1e-9)
        assertEquals(r.celeridad * velocidad / Constantes.GRAVEDAD, r.sobrepresionJoukowskyM!!, 1e-9)
    }

    @Test
    fun `se rechazan espesor y diametro nulos`() {
        assertTrue(Celeridad.calcular(0.0, 0.01, 207e9) is Resultado.Error)
        assertTrue(Celeridad.calcular(0.2, 0.0, 207e9) is Resultado.Error)
        assertTrue(Celeridad.calcular(0.2, 0.01, 0.0) is Resultado.Error)
    }
}

class PerdidasLocalesTest {

    @Test
    fun `aplica la formula K por carga de velocidad`() {
        val q = 0.05
        val d = 0.15
        val k = 11.8

        val r = (PerdidasLocales.calcular(q, d, k) as Resultado.Exito).valor

        val area = PI * d * d / 4.0
        val v = q / area
        assertEquals(k * v * v / (2.0 * Constantes.GRAVEDAD), r.perdidaM, 1e-12)
        assertEquals(v, r.velocidad, 1e-12)
    }

    @Test
    fun `la longitud equivalente usa K por D entre f`() {
        val r = (PerdidasLocales.calcular(0.05, 0.15, 11.8, factorFriccion = 0.02)
            as Resultado.Exito).valor
        assertEquals(11.8 * 0.15 / 0.02, r.longitudEquivalenteM!!, 1e-12)
    }

    @Test
    fun `el catalogo suma correctamente los coeficientes seleccionados`() {
        val seleccion = CatalogoAccesorios.seleccionInicial
            .map { if (it.accesorio.nombre == "Codo de 90°") it.copy(cantidad = 4) else it }
            .map { if (it.accesorio.nombre == "Salida") it.copy(cantidad = 1) else it }

        val total = seleccion.sumOf { it.aporteK }
        assertEquals(4 * 0.9 + 1.0, total, 1e-12)
    }

    @Test
    fun `se rechaza un diametro nulo`() {
        assertTrue(PerdidasLocales.calcular(0.05, 0.0, 3.0) is Resultado.Error)
        assertTrue(PerdidasLocales.calcular(0.0, 0.15, 3.0) is Resultado.Error)
    }
}

class CatalogoMaterialesTest {

    @Test
    fun `todos los materiales tienen coeficientes fisicamente validos`() {
        CatalogoMateriales.todos.forEach {
            assertTrue("${it.nombre}: ε debe ser ≥ 0", it.rugosidadMm >= 0.0)
            assertTrue("${it.nombre}: C debe estar entre 80 y 160", it.coeficienteHazenWilliams in 80.0..160.0)
            assertTrue("${it.nombre}: n debe estar entre 0.008 y 0.03", it.coeficienteManning in 0.008..0.03)
        }
    }

    @Test
    fun `la rugosidad en metros es la de milimetros entre mil`() {
        val pvc = CatalogoMateriales.porNombre("PVC")
        assertEquals(0.0015, pvc.rugosidadMm, 1e-12)
        assertEquals(1.5e-6, pvc.rugosidadM, 1e-15)
    }

    @Test
    fun `no hay nombres duplicados`() {
        val nombres = CatalogoMateriales.todos.map { it.nombre }
        assertEquals(nombres.size, nombres.distinct().size)
    }
}

class UnidadesTest {

    @Test
    fun `conversion de longitud ida y vuelta`() {
        UnidadLongitud.entries.forEach { unidad ->
            val original = 12.5
            assertEquals(original, unidad.desdeMetros(unidad.aMetros(original)), 1e-12)
        }
    }

    @Test
    fun `factores de longitud conocidos`() {
        assertEquals(0.0254, UnidadLongitud.PULGADA.aMetros(1.0), 1e-12)
        assertEquals(0.1, UnidadLongitud.MILIMETRO.aMetros(100.0), 1e-12)
        assertEquals(1.0, UnidadLongitud.METRO.aMetros(1.0), 1e-12)
    }

    @Test
    fun `factores de caudal conocidos`() {
        assertEquals(0.05, UnidadCaudal.LITRO_SEGUNDO.aM3s(50.0), 1e-12)
        assertEquals(1.0, UnidadCaudal.METRO3_HORA.aM3s(3600.0), 1e-12)
    }
}
