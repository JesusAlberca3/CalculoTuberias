package com.example.calculotuberias.navigation

import kotlinx.serialization.Serializable

/**
 * Destinos de navegación con seguridad de tipos (Navigation Compose 2.8).
 *
 * Sustituye a las rutas en cadena de texto ("MenuChido", "Bienvenida", …), donde
 * una errata sólo se detectaba al ejecutar la app.
 */
@Serializable
object Inicio

@Serializable
object MenuConductos

@Serializable
object MenuPerdidas

@Serializable
object Caudal

@Serializable
object Diametro

@Serializable
object PerdidasPorFriccion

@Serializable
object PerdidasLocalesRuta

@Serializable
object CeleridadRuta

@Serializable
object CanalesRuta

@Serializable
object AcercaDe
