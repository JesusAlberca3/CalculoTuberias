package com.example.calculotuberias.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calculotuberias.R
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class GeometriaCanal(val label: String) {
    RECTANGULAR("Rectangular"),
    TRIANGULAR("Triangular"),
    TRAPEZOIDAL("Trapezoidal"),
    CIRCULAR("Circular")
}

data class ResultadoCanal(
    val area: Double,
    val radioHidraulico: Double,
    val caudal: Double,
    val velocidad: Double,
    val perimetroMojado: Double,
    val espejoAgua: Double,
    val energiaEspecifica: Double,
    val numeroFroude: Double,
    val regimenFlujo: String
)

sealed class CalculoCanalResult {
    data class Exito(val resultado: ResultadoCanal) : CalculoCanalResult()
    data class Error(val mensaje: String) : CalculoCanalResult()
}

sealed class ErrorCalculoCanal(val mensaje: String) {
    data object DatosInvalidos : ErrorCalculoCanal("Complete todos los campos con valores válidos")
    data object TiranteCircularInvalido : ErrorCalculoCanal("El tirante debe cumplir 0 < y ≤ D")
}

@SuppressLint("DefaultLocale")
@Composable
fun CanalesScreen(navController: NavController) {
    var geometria by remember { mutableStateOf(GeometriaCanal.RECTANGULAR) }
    var pendiente by remember { mutableStateOf("") }
    var manning by remember { mutableStateOf("") }
    var ancho by remember { mutableStateOf("") }
    var talud by remember { mutableStateOf("") }
    var tirante by remember { mutableStateOf("") }
    var diametro by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf<ResultadoCanal?>(null) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fondo_app),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Titulo("Canales - Flujo Uniforme")

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Datos de Entrada",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.text_blue)
                    )

                    DropdownGeometriaCanal(
                        geometriaSeleccionada = geometria,
                        onGeometriaSelected = {
                            geometria = it
                            resultado = null
                            errorMensaje = null
                        }
                    )

                    Input_1(
                        value = pendiente,
                        onValueChange = { pendiente = it },
                        labelText = "Pendiente hidráulica S (m/m)",
                        icon = Icons.Default.Settings
                    )

                    Input_1(
                        value = manning,
                        onValueChange = { manning = it },
                        labelText = "Coeficiente de Manning n",
                        icon = Icons.Default.Settings
                    )

                    when (geometria) {
                        GeometriaCanal.RECTANGULAR -> {
                            InputWithUnit(
                                value = ancho,
                                onConvertedValueChange = { ancho = it },
                                labelText = "Ancho de plantilla b",
                                icon = Icons.Default.Settings
                            )
                            InputWithUnit(
                                value = tirante,
                                onConvertedValueChange = { tirante = it },
                                labelText = "Tirante y",
                                icon = Icons.Default.Settings
                            )
                        }
                        GeometriaCanal.TRIANGULAR -> {
                            Input_1(
                                value = talud,
                                onValueChange = { talud = it },
                                labelText = "Talud z (H:V)",
                                icon = Icons.Default.Settings
                            )
                            InputWithUnit(
                                value = tirante,
                                onConvertedValueChange = { tirante = it },
                                labelText = "Tirante y",
                                icon = Icons.Default.Settings
                            )
                        }
                        GeometriaCanal.TRAPEZOIDAL -> {
                            InputWithUnit(
                                value = ancho,
                                onConvertedValueChange = { ancho = it },
                                labelText = "Ancho de plantilla b",
                                icon = Icons.Default.Settings
                            )
                            Input_1(
                                value = talud,
                                onValueChange = { talud = it },
                                labelText = "Talud z (H:V)",
                                icon = Icons.Default.Settings
                            )
                            InputWithUnit(
                                value = tirante,
                                onConvertedValueChange = { tirante = it },
                                labelText = "Tirante y",
                                icon = Icons.Default.Settings
                            )
                        }
                        GeometriaCanal.CIRCULAR -> {
                            InputWithUnit(
                                value = diametro,
                                onConvertedValueChange = { diametro = it },
                                labelText = "Diámetro D",
                                icon = Icons.Default.Settings
                            )
                            InputWithUnit(
                                value = tirante,
                                onConvertedValueChange = { tirante = it },
                                labelText = "Tirante y",
                                icon = Icons.Default.Settings
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.text_blue).copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Resultados del Cálculo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    when {
                        errorMensaje != null -> {
                            Text(
                                errorMensaje!!,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        resultado != null -> {
                            val res = resultado!!
                            ResultadoFila("Área hidráulica A", "${"%.4f".format(res.area)} m²")
                            ResultadoFila("Radio hidráulico R", "${"%.4f".format(res.radioHidraulico)} m")
                            ResultadoFila("Caudal Q", "${"%.4f".format(res.caudal)} m³/s")
                            ResultadoFila("Velocidad media V", "${"%.4f".format(res.velocidad)} m/s")
                            ResultadoFila("Perímetro mojado P", "${"%.4f".format(res.perimetroMojado)} m")
                            ResultadoFila("Espejo de agua T", "${"%.4f".format(res.espejoAgua)} m")
                            ResultadoFila("Energía específica E", "${"%.4f".format(res.energiaEspecifica)} m")
                            ResultadoFila("Número de Froude Fr", "${"%.4f".format(res.numeroFroude)}")
                            Text(
                                res.regimenFlujo,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        else -> {
                            Text(
                                "Ingresa los datos y presiona calcular",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val sVal = pendiente.toDoubleOrNull()
                    val nVal = manning.toDoubleOrNull()
                    val yVal = tirante.toDoubleOrNull()

                    if (sVal == null || nVal == null || nVal <= 0.0 || sVal < 0.0 || yVal == null || yVal <= 0.0) {
                        resultado = null
                        errorMensaje = ErrorCalculoCanal.DatosInvalidos.mensaje
                        return@Button
                    }

                    val calculo = when (geometria) {
                        GeometriaCanal.RECTANGULAR -> {
                            val bVal = ancho.toDoubleOrNull()
                            if (bVal == null || bVal <= 0.0) {
                                CalculoCanalResult.Error(ErrorCalculoCanal.DatosInvalidos.mensaje)
                            } else {
                                CalculoCanalResult.Exito(calcularCanalRectangular(sVal, nVal, bVal, yVal))
                            }
                        }
                        GeometriaCanal.TRIANGULAR -> {
                            val zVal = talud.toDoubleOrNull()
                            if (zVal == null || zVal <= 0.0) {
                                CalculoCanalResult.Error(ErrorCalculoCanal.DatosInvalidos.mensaje)
                            } else {
                                CalculoCanalResult.Exito(calcularCanalTriangular(sVal, nVal, zVal, yVal))
                            }
                        }
                        GeometriaCanal.TRAPEZOIDAL -> {
                            val bVal = ancho.toDoubleOrNull()
                            val zVal = talud.toDoubleOrNull()
                            if (bVal == null || bVal < 0.0 || zVal == null || zVal <= 0.0) {
                                CalculoCanalResult.Error(ErrorCalculoCanal.DatosInvalidos.mensaje)
                            } else {
                                CalculoCanalResult.Exito(calcularCanalTrapezoidal(sVal, nVal, bVal, zVal, yVal))
                            }
                        }
                        GeometriaCanal.CIRCULAR -> {
                            val dVal = diametro.toDoubleOrNull()
                            if (dVal == null || dVal <= 0.0) {
                                CalculoCanalResult.Error(ErrorCalculoCanal.DatosInvalidos.mensaje)
                            } else {
                                calcularCanalCircular(sVal, nVal, dVal, yVal)
                            }
                        }
                    }

                    when (calculo) {
                        is CalculoCanalResult.Exito -> {
                            resultado = calculo.resultado
                            errorMensaje = null
                        }
                        is CalculoCanalResult.Error -> {
                            resultado = null
                            errorMensaje = calculo.mensaje
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.text_blue),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Calcular", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ResultadoFila(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
        Text(valor, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownGeometriaCanal(
    geometriaSeleccionada: GeometriaCanal,
    onGeometriaSelected: (GeometriaCanal) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Geometría del canal",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(id = R.color.text_blue),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = geometriaSeleccionada.label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = colorResource(id = R.color.text_blue),
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                GeometriaCanal.entries.forEach { geometria ->
                    DropdownMenuItem(
                        text = { Text(geometria.label) },
                        onClick = {
                            onGeometriaSelected(geometria)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private const val G = 9.81

private fun regimenFlujo(fr: Double): String = when {
    fr < 1.0 -> "Flujo subcrítico"
    fr > 1.0 -> "Flujo supercrítico"
    else -> "Flujo crítico"
}

private fun calcularManning(
    s: Double,
    n: Double,
    area: Double,
    perimetro: Double,
    tirante: Double,
    espejoAgua: Double,
    profundidadFroude: Double
): ResultadoCanal {
    val radio = area / perimetro
    val caudal = (1.0 / n) * area * radio.pow(2.0 / 3.0) * sqrt(s)
    val velocidad = caudal / area
    val energia = tirante + (velocidad * velocidad) / (2.0 * G)
    val froude = velocidad / sqrt(G * profundidadFroude)

    return ResultadoCanal(
        area = area,
        radioHidraulico = radio,
        caudal = caudal,
        velocidad = velocidad,
        perimetroMojado = perimetro,
        espejoAgua = espejoAgua,
        energiaEspecifica = energia,
        numeroFroude = froude,
        regimenFlujo = regimenFlujo(froude)
    )
}

fun calcularCanalRectangular(s: Double, n: Double, b: Double, y: Double): ResultadoCanal {
    val area = b * y
    val perimetro = b + 2.0 * y
    return calcularManning(s, n, area, perimetro, y, b, y)
}

fun calcularCanalTriangular(s: Double, n: Double, z: Double, y: Double): ResultadoCanal {
    val area = z * y * y
    val perimetro = 2.0 * y * sqrt(1.0 + z * z)
    val espejo = 2.0 * z * y
    return calcularManning(s, n, area, perimetro, y, espejo, y / 2.0)
}

fun calcularCanalTrapezoidal(s: Double, n: Double, b: Double, z: Double, y: Double): ResultadoCanal {
    val area = y * (b + z * y)
    val perimetro = b + 2.0 * y * sqrt(1.0 + z * z)
    val espejo = b + 2.0 * z * y
    val profundidadHidraulica = area / espejo
    return calcularManning(s, n, area, perimetro, y, espejo, profundidadHidraulica)
}

fun calcularCanalCircular(s: Double, n: Double, d: Double, y: Double): CalculoCanalResult {
    if (y <= 0.0 || y > d) return CalculoCanalResult.Error(ErrorCalculoCanal.TiranteCircularInvalido.mensaje)

    val theta = 2.0 * acos(1.0 - 2.0 * y / d)
    val area = (d * d / 8.0) * (theta - sin(theta))
    val perimetro = (d / 2.0) * theta
    val espejo = d * sin(theta / 2.0)
    val profundidadHidraulica = area / espejo

    return CalculoCanalResult.Exito(
        calcularManning(s, n, area, perimetro, y, espejo, profundidadHidraulica)
    )
}
