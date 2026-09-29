package com.example.calculotuberias.domain

/**
 * Un único registro por material con sus tres coeficientes de resistencia.
 *
 * La versión anterior mantenía tres tablas independientes (una en metros y dos
 * en milímetros) y eso provocaba que la misma tubería diera resultados distintos
 * según la pantalla. Aquí la rugosidad se almacena siempre en milímetros.
 */
data class MaterialTuberia(
    val nombre: String,
    /** Rugosidad absoluta ε [mm]. */
    val rugosidadMm: Double,
    /** Coeficiente C de Hazen-Williams [adimensional]. */
    val coeficienteHazenWilliams: Double,
    /** Coeficiente n de Manning [s/m^(1/3)]. */
    val coeficienteManning: Double,
) {
    val rugosidadM: Double get() = rugosidadMm / 1000.0
}

object CatalogoMateriales {

    val todos: List<MaterialTuberia> = listOf(
        MaterialTuberia("PVC", 0.0015, 150.0, 0.009),
        MaterialTuberia("Polietileno de alta densidad", 0.001524, 150.0, 0.009),
        MaterialTuberia("Policloruro de vinilo clorado", 0.0015, 150.0, 0.009),
        MaterialTuberia("Cobre", 0.0015, 135.0, 0.010),
        MaterialTuberia("Latón", 0.0015, 135.0, 0.011),
        MaterialTuberia("Plomo", 0.0015, 135.0, 0.011),
        MaterialTuberia("Vidrio", 0.0015, 140.0, 0.011),
        MaterialTuberia("Acero esmaltado", 0.0048, 148.0, 0.010),
        MaterialTuberia("Asbesto cemento", 0.03, 147.0, 0.011),
        MaterialTuberia("Acero nuevo", 0.045, 145.0, 0.011),
        MaterialTuberia("Hierro fundido asfaltado", 0.12, 148.0, 0.012),
        MaterialTuberia("Acero galvanizado", 0.15, 120.0, 0.016),
        MaterialTuberia("Hierro fundido", 0.15, 130.0, 0.012),
        MaterialTuberia("Hierro galvanizado", 0.15, 133.0, 0.016),
        MaterialTuberia("Madera (duelas)", 0.18, 120.0, 0.012),
        MaterialTuberia("Concreto (cimbra metálica)", 0.18, 140.0, 0.013),
        MaterialTuberia("Hierro dúctil", 0.25, 135.0, 0.022),
        MaterialTuberia("Concreto simple", 0.36, 135.0, 0.013),
        MaterialTuberia("Concreto (cimbra madera)", 0.6, 120.0, 0.015),
        MaterialTuberia("Acero remachado", 0.9, 110.0, 0.010),
    )

    val porDefecto: MaterialTuberia = todos.first()

    fun porNombre(nombre: String): MaterialTuberia =
        todos.firstOrNull { it.nombre == nombre } ?: porDefecto
}

/** Material de pared para el cálculo de celeridad del golpe de ariete. */
data class MaterialElastico(
    val nombre: String,
    /** Módulo de elasticidad de Young [GPa]. */
    val moduloYoungGPa: Double,
) {
    val moduloYoungPa: Double get() = moduloYoungGPa * 1e9
}

object CatalogoMaterialesElasticos {

    val todos: List<MaterialElastico> = listOf(
        MaterialElastico("PVC", 3.3),
        MaterialElastico("PEAD", 0.8),
        MaterialElastico("PRFV", 15.0),
        MaterialElastico("Asbesto cemento", 24.0),
        MaterialElastico("Concreto", 25.0),
        MaterialElastico("Concreto reforzado", 45.0),
        MaterialElastico("Hierro fundido", 90.0),
        MaterialElastico("Hierro dúctil", 172.0),
        MaterialElastico("Acero", 207.0),
    )

    val porDefecto: MaterialElastico = todos.first()

    fun porNombre(nombre: String): MaterialElastico =
        todos.firstOrNull { it.nombre == nombre } ?: porDefecto
}
