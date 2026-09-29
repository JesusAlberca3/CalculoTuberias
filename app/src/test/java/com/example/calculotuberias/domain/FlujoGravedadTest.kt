package com.example.calculotuberias.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

/**
 * La prueba clave de esta suite es [caudal_y_diametro_son_mutuamente_consistentes]:
 * detecta exactamente el defecto que tenía la versión anterior, donde el módulo de
 * caudal omitía la carga de velocidad y el de diámetro no.
 */
class FlujoGravedadTest {

    private val longitud = 1000.0
    private val carga = 20.0
    private val sumaK = 2.0
    private val rugosidadPvcMm = 0.0015

    @Test
    fun `caudal y diametro son mutuamente consistentes`() {
        val diametroOriginal = 0.30

        val q = (FlujoGravedad.caudal(diametroOriginal, longitud, rugosidadPvcMm, carga, sumaK)
            as Resultado.Exito).valor.caudalM3s

        val dRecuperado = (FlujoGravedad.diametro(q, longitud, rugosidadPvcMm, carga, sumaK)
            as Resultado.Exito).valor.diametroM

        assertEquals(
            "Encadenar Q(D) y D(Q) debe devolver el diámetro de partida",
            diametroOriginal, dRecuperado, 1e-6,
        )
    }

    @Test
    fun `la ecuacion de la energia se cumple con la carga disponible`() {
        val r = (FlujoGravedad.caudal(0.30, longitud, rugosidadPvcMm, carga, sumaK)
            as Resultado.Exito).valor

        val sumaDeCargas = r.cargaVelocidadM + r.perdidaFriccionM + r.perdidaLocalM
        assertEquals("H debe repartirse entre velocidad, fricción y locales", carga, sumaDeCargas, 1e-6)
    }

    @Test
    fun `el caudal calculado es coherente con el area por la velocidad`() {
        val d = 0.25
        val r = (FlujoGravedad.caudal(d, longitud, rugosidadPvcMm, carga, sumaK)
            as Resultado.Exito).valor
        assertEquals(PI * d * d / 4.0 * r.velocidad, r.caudalM3s, 1e-12)
    }

    @Test
    fun `el diametro converge en pocas iteraciones`() {
        val r = (FlujoGravedad.diametro(0.2, longitud, rugosidadPvcMm, carga, sumaK)
            as Resultado.Exito).valor
        assertTrue("iteraciones = ${r.iteraciones}", r.iteraciones in 1..50)
    }

    @Test
    fun `mas rugosidad produce menos caudal`() {
        val liso = (FlujoGravedad.caudal(0.30, longitud, 0.0015, carga, sumaK)
            as Resultado.Exito).valor.caudalM3s
        val rugoso = (FlujoGravedad.caudal(0.30, longitud, 0.9, carga, sumaK)
            as Resultado.Exito).valor.caudalM3s
        assertTrue("liso=$liso rugoso=$rugoso", rugoso < liso)
    }

    @Test
    fun `mas carga produce mas caudal`() {
        val baja = (FlujoGravedad.caudal(0.30, longitud, rugosidadPvcMm, 10.0, sumaK)
            as Resultado.Exito).valor.caudalM3s
        val alta = (FlujoGravedad.caudal(0.30, longitud, rugosidadPvcMm, 40.0, sumaK)
            as Resultado.Exito).valor.caudalM3s
        assertTrue("baja=$baja alta=$alta", alta > baja)
    }

    @Test
    fun `se rechazan entradas no fisicas`() {
        assertTrue(FlujoGravedad.caudal(0.0, longitud, 0.0015, carga, sumaK) is Resultado.Error)
        assertTrue(FlujoGravedad.caudal(0.3, 0.0, 0.0015, carga, sumaK) is Resultado.Error)
        assertTrue(FlujoGravedad.caudal(0.3, longitud, 0.0015, 0.0, sumaK) is Resultado.Error)
        assertTrue(FlujoGravedad.diametro(0.0, longitud, 0.0015, carga, sumaK) is Resultado.Error)
        assertTrue(FlujoGravedad.caudal(0.3, longitud, 0.0015, carga, -1.0) is Resultado.Error)
    }
}
