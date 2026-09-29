package com.example.calculotuberias.domain

/** Accesorio con su coeficiente de pérdida local K. */
data class Accesorio(
    val nombre: String,
    val coeficienteK: Double,
    val categoria: CategoriaAccesorio,
)

enum class CategoriaAccesorio(val etiqueta: String) {
    VALVULAS("Válvulas"),
    CODOS_CURVAS("Codos y curvas"),
    DERIVACIONES("Derivaciones"),
    ENTRADAS_SALIDAS("Entradas y salidas"),
}

/** Accesorio seleccionado con su cantidad. */
data class AccesorioSeleccionado(
    val accesorio: Accesorio,
    val cantidad: Int = 0,
) {
    val aporteK: Double get() = cantidad * accesorio.coeficienteK
}

object CatalogoAccesorios {

    val todos: List<Accesorio> = listOf(
        Accesorio("Válvula de globo abierta", 10.0, CategoriaAccesorio.VALVULAS),
        Accesorio("Válvula check totalmente abierta", 2.5, CategoriaAccesorio.VALVULAS),
        Accesorio("Válvula en ángulo completamente abierta", 5.0, CategoriaAccesorio.VALVULAS),
        Accesorio("Válvula de compuerta totalmente abierta", 0.2, CategoriaAccesorio.VALVULAS),
        Accesorio("Válvula de compuerta con ¾ de apertura", 1.5, CategoriaAccesorio.VALVULAS),
        Accesorio("Válvula de compuerta con ½ de apertura", 5.6, CategoriaAccesorio.VALVULAS),
        Accesorio("Válvula de compuerta con ¼ de apertura", 24.0, CategoriaAccesorio.VALVULAS),
        Accesorio("Codo de 90°", 0.9, CategoriaAccesorio.CODOS_CURVAS),
        Accesorio("Codo de 45°", 0.42, CategoriaAccesorio.CODOS_CURVAS),
        Accesorio("Retorno (curva en U)", 2.2, CategoriaAccesorio.CODOS_CURVAS),
        Accesorio("Tee en sentido recto", 0.3, CategoriaAccesorio.DERIVACIONES),
        Accesorio("Tee a través de la salida lateral", 1.8, CategoriaAccesorio.DERIVACIONES),
        Accesorio("Ye de 45°, en sentido recto", 0.3, CategoriaAccesorio.DERIVACIONES),
        Accesorio("Ye de 45°, en salida lateral", 0.8, CategoriaAccesorio.DERIVACIONES),
        Accesorio("Unión", 0.3, CategoriaAccesorio.DERIVACIONES),
        Accesorio("Pérdida por entrada brusca", 0.8, CategoriaAccesorio.ENTRADAS_SALIDAS),
        Accesorio("Pérdida por entrada gradual", 0.2, CategoriaAccesorio.ENTRADAS_SALIDAS),
        Accesorio("Pérdida por pichancha", 5.5, CategoriaAccesorio.ENTRADAS_SALIDAS),
        Accesorio("Salida", 1.0, CategoriaAccesorio.ENTRADAS_SALIDAS),
    )

    val seleccionInicial: List<AccesorioSeleccionado> = todos.map { AccesorioSeleccionado(it) }
}

data class PerdidaLocal(
    val coeficienteKTotal: Double,
    val perdidaM: Double,
    val velocidad: Double,
    val area: Double,
    val cargaVelocidad: Double,
    val longitudEquivalenteM: Double?,
)

object PerdidasLocales {

    /**
     * Pérdidas menores por el método del coeficiente K: h_L = ΣK · V²/(2g).
     *
     * Si se aporta el factor de fricción y la longitud de la conducción también
     * se devuelve la longitud equivalente L_eq = ΣK · D / f.
     */
    fun calcular(
        caudalM3s: Double,
        diametroM: Double,
        coeficienteKTotal: Double,
        factorFriccion: Double? = null,
    ): Resultado<PerdidaLocal> {
        if (diametroM <= 0.0) return Resultado.Error(Errores.DIAMETRO_INVALIDO)
        if (caudalM3s <= 0.0) return Resultado.Error(Errores.CAUDAL_INVALIDO)

        val area = PerdidasFriccion.areaCircular(diametroM)
        val velocidad = caudalM3s / area
        val cargaVelocidad = velocidad * velocidad / (2.0 * Constantes.GRAVEDAD)
        val perdida = coeficienteKTotal * cargaVelocidad

        val longitudEquivalente = factorFriccion
            ?.takeIf { it > 0.0 }
            ?.let { coeficienteKTotal * diametroM / it }

        val advertencias = buildList {
            if (coeficienteKTotal <= 0.0) {
                add("No has seleccionado ningún accesorio: ΣK = 0, la pérdida local es nula.")
            }
        }

        return Resultado.Exito(
            PerdidaLocal(
                coeficienteKTotal = coeficienteKTotal,
                perdidaM = perdida,
                velocidad = velocidad,
                area = area,
                cargaVelocidad = cargaVelocidad,
                longitudEquivalenteM = longitudEquivalente,
            ),
            advertencias,
        )
    }
}
