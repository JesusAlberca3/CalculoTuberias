package com.example.calculotuberias.ui.components

import com.example.calculotuberias.domain.Formato
import com.example.calculotuberias.domain.MaterialElastico
import com.example.calculotuberias.domain.MaterialTuberia

/** Resumen de los tres coeficientes del material, para mostrarlo bajo el selector. */
fun descripcionMaterial(material: MaterialTuberia): String =
    "ε = ${Formato.auto(material.rugosidadMm, 4)} mm   ·   " +
        "C = ${Formato.numero(material.coeficienteHazenWilliams, 0)}   ·   " +
        "n = ${Formato.numero(material.coeficienteManning, 3)}"

fun descripcionMaterialElastico(material: MaterialElastico): String =
    "E = ${Formato.numero(material.moduloYoungGPa, 1)} GPa"
